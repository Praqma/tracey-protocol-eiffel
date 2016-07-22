package net.praqma.tracey.protocol.eiffel.factories;

import com.google.protobuf.Message;
import net.praqma.tracey.protocol.eiffel.events.EiffelSourceChangeSubmittedEventOuterClass.EiffelSourceChangeSubmittedEvent;
import net.praqma.tracey.protocol.eiffel.events.EiffelSourceChangeSubmittedEventOuterClass.EiffelSourceChangeSubmittedEvent.EiffelSourceChangeSubmittedEventData;
import net.praqma.tracey.protocol.eiffel.models.Models;
import net.praqma.tracey.protocol.eiffel.models.Models.Data.GAV;
import net.praqma.tracey.protocol.eiffel.utils.GitUtils;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EiffelSourceChangeSubmittedEventFactory extends BaseFactory {
    private static final Logger LOG = Logger.getLogger( EiffelSourceChangeSubmittedEventFactory.class.getName() );
    private static EiffelSourceChangeSubmittedEventData.Builder data = EiffelSourceChangeSubmittedEventData.newBuilder();

    public EiffelSourceChangeSubmittedEventFactory(final String host, final String name, final String uri, final String domainId, final GAV gav) {
        super(host, name, uri, domainId, gav);
    }

    public EiffelSourceChangeSubmittedEventFactory(final String name, final String uri, final String domainId, final GAV gav) {
        super(name, uri, domainId, gav);
    }

    public EiffelSourceChangeSubmittedEventFactory(final String name, final String uri, final String domainId) {
        super(name, uri, domainId);
    }

    public void parseFromGit(final String path, final String commitId, final String branch) throws IOException {
        LOG.log(Level.FINE, "Parse EiffelSourceChangeSubmittedEvent details from repo {0} commit {1} branch {2}", new Object[]{path, commitId, branch});
        final Repository repository = GitUtils.openRepository(path);
        final RevCommit commit = GitUtils.getCommitById(repository, commitId);
        data.setGitIdentifier(GitUtils.getGitId(repository, commitId, branch));
        data.setSubmitter(GitUtils.getSubmitter(commit));
        LOG.log(Level.FINE, "Set submitter to {0}", data.getSubmitter().toString());
        LOG.log(Level.FINE, "Set Git identifier to {0}", data.getGitIdentifier().toString());
    }

    @Override
    public Message.Builder create() {
        final EiffelSourceChangeSubmittedEvent.Builder event = EiffelSourceChangeSubmittedEvent.newBuilder();
        event.setData(data);
        event.setMeta(createMeta(Models.Meta.EiffelEventType.EiffelSourceChangeSubmittedEvent, source));
        event.addAllLinks(links);
        return event;
    }
}
