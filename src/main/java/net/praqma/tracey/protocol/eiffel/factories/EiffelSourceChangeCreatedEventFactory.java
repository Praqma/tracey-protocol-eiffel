package net.praqma.tracey.protocol.eiffel.factories;

import com.google.protobuf.Message;
import net.praqma.tracey.protocol.eiffel.events.EiffelSourceChangeCreatedEventOuterClass.EiffelSourceChangeCreatedEvent;
import net.praqma.tracey.protocol.eiffel.events.EiffelSourceChangeCreatedEventOuterClass.EiffelSourceChangeCreatedEvent.EiffelSourceChangeCreatedEventData;
import net.praqma.tracey.protocol.eiffel.models.Models;
import net.praqma.tracey.protocol.eiffel.models.Models.Data.GAV;

import net.praqma.tracey.protocol.eiffel.utils.GitUtils;

import java.util.logging.Logger;

import net.praqma.utils.parsers.cmg.api.CommitMessageParser;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;
import java.util.logging.Level;

public class EiffelSourceChangeCreatedEventFactory extends BaseFactory {
    private static final Logger LOG = Logger.getLogger(EiffelSourceChangeCreatedEventFactory.class.getName());
    private final EiffelSourceChangeCreatedEventData.Builder data = EiffelSourceChangeCreatedEventData.newBuilder();

    public EiffelSourceChangeCreatedEventFactory(final String host, final String name, final String uri, final String domainId, final GAV gav) {
        super(host, name, uri, domainId, gav);
    }

    public EiffelSourceChangeCreatedEventFactory(final String name, final String uri, final String domainId, final GAV gav) {
        super(name, uri, domainId, gav);
    }

    public EiffelSourceChangeCreatedEventFactory(final String name, final String uri, final String domainId) {
        super(name, uri, domainId);
    }

    public void parseFromGit(final String path, final String commitId, final String branch, final CommitMessageParser parser) throws IOException {
        LOG.log(Level.FINE, "Parse EiffelSourceChangeCreatedEvent details from repo {0} commit {1} branch {2}", new Object[]{path, commitId, branch});
        final Repository repository = GitUtils.openRepository(path);
        final RevCommit commit = GitUtils.getCommitById(repository, commitId);
        data.setGitIdentifier(GitUtils.getGitId(repository, commitId, branch));
        data.setAuthor(GitUtils.getAuthor(commit));
        data.addAllIssues(GitUtils.getIssues(commit, parser));
        data.setChange(GitUtils.getChange(repository, commit));
        LOG.log(Level.FINE, "Set author to {0}", data.getAuthor().toString());
        LOG.log(Level.FINE, "Set Git identifier to {0}", data.getGitIdentifier().toString());
        LOG.log(Level.FINE, "Set issues to {0}", data.getIssuesList().toString());
        LOG.log(Level.FINE, "Set change to {0}", data.getChange().toString());
    }

    @Override
    public Message.Builder create() {
        final EiffelSourceChangeCreatedEvent.Builder event = EiffelSourceChangeCreatedEvent.newBuilder();
        event.setData(data);
        event.setMeta(createMeta(Models.Meta.EiffelEventType.EiffelSourceChangeCreatedEvent, source));
        event.addAllLinks(links);
        return event;
    }
}
