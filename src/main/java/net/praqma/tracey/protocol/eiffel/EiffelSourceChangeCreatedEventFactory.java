package net.praqma.tracey.protocol.eiffel;

import net.praqma.tracey.protocol.eiffel.EiffelSourceChangeCreatedEventOuterClass.*;
import net.praqma.tracey.protocol.eiffel.EiffelEventOuterClass.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;

public class EiffelSourceChangeCreatedEventFactory {
    private static final Logger log = Logger.getLogger( EiffelSourceChangeCreatedEventFactory.class.getName() );

    private static Repository openRepository(final String path) throws IOException {
        final FileRepositoryBuilder builder = new FileRepositoryBuilder();
        final Repository repository = builder.setGitDir(new File(path))
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir() // scan up the file system tree
                .build();
        log.info("Read repository: " + repository.getDirectory());
        return repository;
    }

    private static RevCommit getCommitById(final Repository repository, final String commitId) throws IOException {
        final Ref commitObj = repository.exactRef(commitId);
        log.info("Found head: " + commitObj);
        final RevWalk walk = new RevWalk(repository);
        final RevCommit commit = walk.parseCommit(commitObj.getObjectId());
        log.info("Commit-Message: " + commit.getFullMessage());
        walk.dispose();
        return commit;
    }

    private static EiffelSourceChangeCreatedEvent.GitIdentifier getGitId(final Repository repository, final String commitId, final String branch) {
        final EiffelSourceChangeCreatedEvent.GitIdentifier.Builder gitId = EiffelSourceChangeCreatedEvent.GitIdentifier.newBuilder();
        gitId.setBranch(branch);
        gitId.setCommitId(commitId);
        gitId.setRepoName(repository.getDirectory().getName());
        final Config storedConfig = repository.getConfig();
        // Pick first remote
        final Set<String> remotes = storedConfig.getSubsections("remote");
        if ( ! remotes.isEmpty() ) {
            gitId.setRepoUri(storedConfig.getString("remote", remotes.iterator().next(), "url"));
        } else {
            log.warning("No remotes configure for the repo " + repository.getDirectory() + " . Can't read remote url");
        }
        return gitId.build();
    }

    private static EiffelSourceChangeCreatedEvent.Author getAuthor(final RevCommit commit) {
        final EiffelSourceChangeCreatedEvent.Author.Builder author = EiffelSourceChangeCreatedEvent.Author.newBuilder();
        author.setEmail(commit.getAuthorIdent().getEmailAddress());
        author.setEmail(commit.getAuthorIdent().getName());
        // TODO: Have no idea where to get organisation and id
        return author.build();
    }

    // TODO: parse issues from the commit message
    private static List<EiffelSourceChangeCreatedEvent.Issue> getIssues(final RevCommit commit) {
        List<EiffelSourceChangeCreatedEvent.Issue> issues = new ArrayList<>();
        return issues;
    }

    public static EiffelEvent createFromGit(final String path, final String commitId, final String branch) throws IOException {
        final EiffelEvent.Builder event = EiffelEventOuterClass.EiffelEvent.newBuilder();
        final EiffelSourceChangeCreatedEvent.Builder eventData = EiffelSourceChangeCreatedEvent.newBuilder();
        final Repository repository = openRepository(path);
        final RevCommit commit = getCommitById(repository, commitId);
        eventData.setGitIdentifier(getGitId(repository, commitId, branch));
        eventData.setAuthor(getAuthor(commit));
        final int issueNumber = 0;
        for (EiffelSourceChangeCreatedEvent.Issue issue: getIssues(commit)) {
            eventData.setIssues(issueNumber, issue);
        }
        event.setEiffelSourceChangeCreatedEventData(eventData.build());
        event.setMeta(MetaFactory.create("my domainId", EiffelEvent.Meta.EventType.EiffelSourceChangeCreatedEvent));
        return event.build();
    }
}
