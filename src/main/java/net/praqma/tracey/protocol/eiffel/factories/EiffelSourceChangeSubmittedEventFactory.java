package net.praqma.tracey.protocol.eiffel.factories;

import com.google.protobuf.Message;
import net.praqma.tracey.protocol.eiffel.events.EiffelSourceChangeSubmittedEventOuterClass.EiffelSourceChangeSubmittedEvent;
import net.praqma.tracey.protocol.eiffel.events.EiffelSourceChangeSubmittedEventOuterClass.EiffelSourceChangeSubmittedEvent.EiffelSourceChangeSubmittedEventData;
import net.praqma.tracey.protocol.eiffel.models.Models;
import net.praqma.tracey.protocol.eiffel.models.Models.Data.Serializer;
import net.praqma.tracey.protocol.eiffel.utils.GitUtils;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;
import java.util.logging.Logger;

public class EiffelSourceChangeSubmittedEventFactory extends BaseFactory {
    private static final Logger log = Logger.getLogger( EiffelSourceChangeSubmittedEventFactory.class.getName() );
    private static final EiffelSourceChangeSubmittedEventData.Builder data = EiffelSourceChangeSubmittedEventData.newBuilder();

    public EiffelSourceChangeSubmittedEventFactory(final String host, final String name, final String uri, final String domainId, final Serializer gav) {
        super(host, name, uri, domainId, gav);
    }

    public void parseFromGit(final String path, final String commitId, final String branch) throws IOException {
        log.fine("Parse EiffelSourceChangeSubmittedEvent details from repo " + path + " commit " + commitId + " branch " + branch);
        final Repository repository = GitUtils.openRepository(path);
        final RevCommit commit = GitUtils.getCommitById(repository, commitId);
        data.setGitIdentifier(GitUtils.getGitId(repository, commitId, branch));
        data.setSubmitter(GitUtils.getSubmitter(commit));
        log.fine("Set submitter to " + data.getSubmitter().toString());
        log.fine("Set Git identifier to " + data.getGitIdentifier().toString());
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
