package net.praqma.tracey.protocol.eiffel.factories;

import com.google.protobuf.Message;
import net.praqma.tracey.protocol.eiffel.events.EiffelSourceChangeCreatedEventOuterClass.EiffelSourceChangeCreatedEvent;
import net.praqma.tracey.protocol.eiffel.events.EiffelSourceChangeCreatedEventOuterClass.EiffelSourceChangeCreatedEvent.EiffelSourceChangeCreatedEventData;
import net.praqma.tracey.protocol.eiffel.models.Models;
import net.praqma.tracey.protocol.eiffel.models.Models.Data.Serializer;

import net.praqma.tracey.protocol.eiffel.utils.GitUtils;

import java.util.logging.Logger;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;

public class EiffelSourceChangeCreatedEventFactory extends BaseFactory {
    private static final Logger log = Logger.getLogger( EiffelSourceChangeCreatedEventFactory.class.getName() );
    private static final EiffelSourceChangeCreatedEventData.Builder data = EiffelSourceChangeCreatedEventData.newBuilder();

    public EiffelSourceChangeCreatedEventFactory(final String host, final String name, final String uri, final String domainId, final Serializer gav) {
        super(host, name, uri, domainId, gav);
    }

    public void parseFromGit(final String path, final String commitId, final String branch) throws IOException {
        final Repository repository = GitUtils.openRepository(path);
        final RevCommit commit = GitUtils.getCommitById(repository, commitId);
        data.setGitIdentifier(GitUtils.getGitId(repository, commitId, branch));
        data.setAuthor(GitUtils.getAuthor(commit));
        data.addAllIssues(GitUtils.getIssues(commit));
        data.setChange(GitUtils.getChange(repository, commit));
    }

    @Override
    public Message.Builder create() {
        final EiffelSourceChangeCreatedEvent.Builder event = EiffelSourceChangeCreatedEvent.newBuilder();
        event.setData(data);
        event.setMeta(createMeta(Models.Meta.EiffelEventType.EiffelSourceChangeCreatedEvent, getSource()));
        return event;
    }
}
