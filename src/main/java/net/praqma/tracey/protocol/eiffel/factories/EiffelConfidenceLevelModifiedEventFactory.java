package net.praqma.tracey.protocol.eiffel.factories;

import com.google.protobuf.Message;
import net.praqma.tracey.protocol.eiffel.events.EiffelConfidenceLevelModifiedEventOuterClass.EiffelConfidenceLevelModifiedEvent;
import net.praqma.tracey.protocol.eiffel.events.EiffelConfidenceLevelModifiedEventOuterClass.EiffelConfidenceLevelModifiedEvent.EiffelConfidenceLevelModifiedEventData;
import net.praqma.tracey.protocol.eiffel.events.EiffelConfidenceLevelModifiedEventOuterClass.EiffelConfidenceLevelModifiedEvent.Issuer;
import net.praqma.tracey.protocol.eiffel.events.EiffelConfidenceLevelModifiedEventOuterClass.EiffelConfidenceLevelModifiedEvent.EiffelConfidenceLevelType;
import net.praqma.tracey.protocol.eiffel.models.Models.Data.Serializer;
import net.praqma.tracey.protocol.eiffel.models.Models.Meta;

import java.util.logging.Logger;

public class EiffelConfidenceLevelModifiedEventFactory extends BaseFactory {
    private static final Logger log = Logger.getLogger( EiffelConfidenceLevelModifiedEventFactory.class.getName() );
    private static final EiffelConfidenceLevelModifiedEventData.Builder data = EiffelConfidenceLevelModifiedEventData.newBuilder();

    public EiffelConfidenceLevelModifiedEventFactory(final String host, final String name, final String uri, final String domainId, final Serializer gav) {
        super(host, name, uri, domainId, gav);
    }

    public void setName(final String name) {
        log.fine("Set confidence level name to " + name);
        data.setName(name);
    }

    public void setValue(final EiffelConfidenceLevelType value) {
        log.fine("Set confidence level value to " + value);
        data.setValue(value);
    }

    public void setIssuier(final String name, final String email) {
        final Issuer.Builder issuer = Issuer.newBuilder();
        data.setIssuer(issuer.setName(name).setEmail(email).build());
        log.fine("Set issuer to " + data.getIssuer().toString());
    }

    @Override
    public Message.Builder create() {
        final EiffelConfidenceLevelModifiedEvent.Builder event = EiffelConfidenceLevelModifiedEvent.newBuilder();
        event.setData(data);
        event.setMeta(createMeta(Meta.EiffelEventType.EiffelCompositionDefinedEvent, source));
        event.addAllLinks(links);
        return event;
    }
}
