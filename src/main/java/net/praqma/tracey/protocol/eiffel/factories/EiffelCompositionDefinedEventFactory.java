package net.praqma.tracey.protocol.eiffel.factories;

import com.google.protobuf.Message;
import net.praqma.tracey.protocol.eiffel.events.EiffelCompositionDefinedEventOuterClass.EiffelCompositionDefinedEvent;
import net.praqma.tracey.protocol.eiffel.events.EiffelCompositionDefinedEventOuterClass.EiffelCompositionDefinedEvent.EiffelCompositionDefinedEventData;
import net.praqma.tracey.protocol.eiffel.models.Models;
import net.praqma.tracey.protocol.eiffel.models.Models.Data.GAV;

import java.util.logging.Logger;

public class EiffelCompositionDefinedEventFactory extends BaseFactory {
    private static final Logger log = Logger.getLogger( EiffelCompositionDefinedEventFactory.class.getName() );
    private static final EiffelCompositionDefinedEventData.Builder data = EiffelCompositionDefinedEventData.newBuilder();

    public EiffelCompositionDefinedEventFactory(final String host, final String name, final String uri, final String domainId, final GAV gav) {
        super(host, name, uri, domainId, gav);
    }

    public EiffelCompositionDefinedEventFactory(final String name, final String uri, final String domainId, final GAV gav) {
        super(name, uri, domainId, gav);
    }

    public EiffelCompositionDefinedEventFactory(final String name, final String uri, final String domainId) {
        super(name, uri, domainId);
    }

    public void setName(final String name) {
        log.fine("Set composition name to " + name);
        data.setName(name);
    }

    @Override
    public Message.Builder create() {
        final EiffelCompositionDefinedEvent.Builder event = EiffelCompositionDefinedEvent.newBuilder();
        event.setData(data);
        event.setMeta(createMeta(Models.Meta.EiffelEventType.EiffelCompositionDefinedEvent, source));
        event.addAllLinks(links);
        return event;
    }
}
