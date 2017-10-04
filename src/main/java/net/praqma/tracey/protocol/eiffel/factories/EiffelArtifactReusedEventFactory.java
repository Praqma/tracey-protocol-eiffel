package net.praqma.tracey.protocol.eiffel.factories;

import com.google.protobuf.Message;
import net.praqma.tracey.protocol.eiffel.events.EiffelArtifactReusedEventOuterClass;
import net.praqma.tracey.protocol.eiffel.models.Models;

/**
 * Created by mads on 10/4/17.
 */
public class EiffelArtifactReusedEventFactory extends BaseFactory {

    public EiffelArtifactReusedEventFactory(final String name, final String uri, final String domainId) {
        super(name, uri, domainId);
    }

    @Override
    public Message.Builder create() {
        EiffelArtifactReusedEventOuterClass.EiffelArtifactReusedEvent.Builder event = EiffelArtifactReusedEventOuterClass.EiffelArtifactReusedEvent.newBuilder();
        event.setMeta(createMeta(Models.Meta.EiffelEventType.EiffelArtifactReusedEvent, source));
        event.addAllLinks(links);
        return event;
    }

}
