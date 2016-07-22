package net.praqma.tracey.protocol.eiffel.factories;

import com.google.protobuf.Message;
import java.util.logging.Level;
import net.praqma.tracey.protocol.eiffel.events.EiffelConfidenceLevelModifiedEventOuterClass.EiffelConfidenceLevelModifiedEvent;
import net.praqma.tracey.protocol.eiffel.events.EiffelConfidenceLevelModifiedEventOuterClass.EiffelConfidenceLevelModifiedEvent.EiffelConfidenceLevelModifiedEventData;
import net.praqma.tracey.protocol.eiffel.events.EiffelConfidenceLevelModifiedEventOuterClass.EiffelConfidenceLevelModifiedEvent.EiffelConfidenceLevelType;
import net.praqma.tracey.protocol.eiffel.models.Models.Data.GAV;
import net.praqma.tracey.protocol.eiffel.models.Models.Data.Person;
import net.praqma.tracey.protocol.eiffel.models.Models.Meta;

import java.util.logging.Logger;

public class EiffelConfidenceLevelModifiedEventFactory extends BaseFactory {
    private static final Logger LOG = Logger.getLogger( EiffelConfidenceLevelModifiedEventFactory.class.getName() );
    private final EiffelConfidenceLevelModifiedEventData.Builder data = EiffelConfidenceLevelModifiedEventData.newBuilder();

    public EiffelConfidenceLevelModifiedEventFactory(final String host, final String name, final String uri, final String domainId, final GAV gav) {
        super(host, name, uri, domainId, gav);
    }

    public EiffelConfidenceLevelModifiedEventFactory(final String name, final String uri, final String domainId, final GAV gav) {
        super(name, uri, domainId, gav);
    }

    public EiffelConfidenceLevelModifiedEventFactory(final String name, final String uri, final String domainId) {
        super(name, uri, domainId);
    }

    public void setName(final String name) {
        LOG.log(Level.FINE, "Set confidence level name to {0}", name);
        data.setName(name);
    }

    public void setValue(final EiffelConfidenceLevelType value) {
        LOG.log(Level.FINE, "Set confidence level value to {0}", value);
        data.setValue(value);
    }

    public void setIssuier(final String name, final String email) {
        final Person.Builder issuer = Person.newBuilder();
        data.setIssuer(issuer.setName(name).setEmail(email).build());
        LOG.log(Level.FINE, "Set issuer to {0}", data.getIssuer().toString());
    }

    @Override
    public Message.Builder create() {
        final EiffelConfidenceLevelModifiedEvent.Builder event = EiffelConfidenceLevelModifiedEvent.newBuilder();
        event.setData(data);
        event.setMeta(createMeta(Meta.EiffelEventType.EiffelConfidenceLevelModifiedEvent, source));
        event.addAllLinks(links);
        return event;
    }
}
