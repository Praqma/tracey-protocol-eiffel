package net.praqma.tracey.protocol.eiffel.utils;

import net.praqma.tracey.protocol.eiffel.events.EiffelSourceChangeCreatedEventOuterClass.EiffelSourceChangeCreatedEvent.Author;
import net.praqma.tracey.protocol.eiffel.events.EiffelSourceChangeSubmittedEventOuterClass.EiffelSourceChangeSubmittedEvent.Submitter;
import net.praqma.tracey.protocol.eiffel.events.EiffelSourceChangeCreatedEventOuterClass.EiffelSourceChangeCreatedEvent.Change;
import net.praqma.tracey.protocol.eiffel.events.EiffelSourceChangeCreatedEventOuterClass.EiffelSourceChangeCreatedEvent.Issue;
import net.praqma.tracey.protocol.eiffel.models.Models.Data.GitIdentifier;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.HunkHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class GitUtils {
    private static final Logger log = Logger.getLogger( GitUtils.class.getName() );

    public static Repository openRepository(final String path) throws IOException {
        log.fine("Attempting to read repo: " + path);
        final FileRepositoryBuilder builder = new FileRepositoryBuilder();
        final Repository repository = builder.findGitDir(new File(path)).build();
        log.fine("Read repository: " + repository.getDirectory());
        return repository;
    }

    public static RevCommit getCommitById(final Repository repository, final String commitId) throws IOException {
        final ObjectId commitObj = repository.resolve(commitId);
        log.fine("Found commit: " + commitObj.getName());
        final RevWalk walk = new RevWalk(repository);
        final RevCommit commit = walk.parseCommit(commitObj);
        walk.dispose();
        return commit;
    }

    private static String parseChange(DiffEntry entry) {
        String result = "";
        switch (entry.getChangeType()) {
            case ADD:
            case MODIFY:
                result = entry.getChangeType().toString() + " " + entry.getNewPath();
                break;
            case DELETE:
                result = entry.getChangeType().toString() + " " + entry.getOldPath();
                break;
            case COPY:
            case RENAME:
                result = entry.getChangeType().toString() + " " + entry.getOldPath() + " => " + entry.getNewPath();
                break;
        }
        return result;
    }

    private static class PatchStat {
        public int insertions = 0;
        public int deletetions = 0;
    }

    private static PatchStat parseChangeStats(DiffFormatter df, DiffEntry entry) throws IOException {
        final PatchStat stat = new PatchStat();
        FileHeader fileHeader = df.toFileHeader(entry);
        List<? extends HunkHeader> hunks = fileHeader.getHunks();
        for (HunkHeader hunk : hunks) {
            for (Edit edit : hunk.toEditList()) {
                switch (edit.getType()) {
                    // An edit where beginA < endA && beginB == endB is a delete edit,
                    // that is sequence B has removed the elements between [beginA, endA).
                    case DELETE:
                        stat.deletetions += edit.getEndA() - edit.getBeginA();
                        break;
                    // An edit where beginA == endA && beginB < endB is an insert edit,
                    // that is sequence B inserted the elements in region [beginB, endB) at beginA.
                    case INSERT:
                        stat.insertions += edit.getEndB() - edit.getBeginB();
                        break;
                    // An edit where beginA < endA && beginB < endB is a replace edit,
                    // that is sequence B has replaced the range of elements between [beginA, endA) with those found in [beginB, endB).
                    case REPLACE:
                        stat.insertions += edit.getEndB() - edit.getBeginB();
                        stat.deletetions += edit.getEndA() - edit.getBeginA();
                        break;
                    case EMPTY:
                        break;
                }
                log.fine("Calculated stat for change " + edit.toString() + ", insertions " + stat.insertions + ", deletions " + stat.deletetions);
            }
        }
        return stat;
    }

    public static Change getChange(final Repository repository, final RevCommit commit) throws IOException {
        final Change.Builder change = Change.newBuilder();
        PatchStat finalStat = new PatchStat();
        String fileChange = "";
        if ( commit.getParentCount() > 0 ) {
            // We commit.getParent(0) will return incomplete RevCommit that does not contain tree
            // That's why we should resolve parent using getCommitById. Yes, I it is silly but this is how it is
            RevCommit parent = getCommitById(repository, commit.getParent(0).getName());
            log.fine("Get change info - compare " + commit.getName() + " and " + parent.getName());
            DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
            df.setRepository(repository);
            //df.setDiffComparator(RawTextComparator.DEFAULT);
            df.setDetectRenames(true);
            List<DiffEntry> diffs = df.scan(parent.getTree(), commit.getTree());
            for (DiffEntry diff : diffs) {
                fileChange = parseChange(diff);
                change.addFiles(fileChange);
                // Record change stats
                PatchStat stat = parseChangeStats(df, diff);
                finalStat.insertions += stat.insertions;
                finalStat.deletetions += stat.deletetions;
                log.fine(fileChange + ", inserted lines: " + stat.insertions + ", deleted lines: " + stat.deletetions);
            }
        } else {
            // This means it is either a shallow clone or first commit in the tree
            log.warning("No parents found - can't get a diff");
        }
        change.setDeletions(finalStat.deletetions);
        change.setInsertions(finalStat.insertions);
        return change.build();
    }

    public static GitIdentifier getGitId(final Repository repository, final String commitId, final String branch) throws
            IOException {
        final GitIdentifier.Builder gitId = GitIdentifier.newBuilder();
        gitId.setBranch(branch);
        // make sure to resolve commitId in case if we got a ref like HEAD or branch name
        gitId.setCommitId(getCommitById(repository, commitId).getName());
        final Config storedConfig = repository.getConfig();
        // Pick first remote
        final Set<String> remotes = storedConfig.getSubsections("remote");
        if ( ! remotes.isEmpty() ) {
            final String uri = storedConfig.getString("remote", remotes.iterator().next(), "url");
            gitId.setRepoUri(uri);
            // Get last part of the URI as a repo name
            gitId.setRepoName(uri.substring(uri.lastIndexOf('/') + 1).replaceAll(".git$", ""));
        } else {
            log.warning("No remotes configure for the repo " + repository.getDirectory() + " . Can't read remote url");
        }
        return gitId.build();
    }

    public static Author getAuthor(final RevCommit commit) {
        final Author.Builder author = Author.newBuilder();
        author.setEmail(commit.getAuthorIdent().getEmailAddress());
        author.setName(commit.getAuthorIdent().getName());
        // TODO: Have no idea where to get organisation and id
        return author.build();
    }

    public static Submitter getSubmitter(final RevCommit commit) {
        final Submitter.Builder submitter = Submitter.newBuilder();
        submitter.setEmail(commit.getCommitterIdent().getEmailAddress());
        submitter.setName(commit.getCommitterIdent().getName());
        // TODO: Have no idea where to get organisation and id
        return submitter.build();
    }

    // TODO: parse issues from the commit message
    public static List<Issue> getIssues(final RevCommit commit) {
        List<Issue> issues = new ArrayList<>();
        return issues;
    }
}
