package net.praqma.tracey.protocol.eiffel;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.logging.Logger;
import net.praqma.tracey.protocol.eiffel.EiffelEventOuterClass.*;

public class EventFactory {
    private static final Logger log = Logger.getLogger( EventFactory.class.getName() );
    private static final String DEFAULT = "undefined";

    private static String getResourceNameOrNull() {
        // Fix later
        return null;
    }

    private static String getHostNameOrNull() {
        try {
            return InetAddress.getLocalHost().getCanonicalHostName();
        } catch (UnknownHostException e) {
            log.warning("Can't get hostname, will use 'null' instead. Error: " + e.getMessage());
        }
        return null;
    }

    private static String getEventId() {
        return UUID.randomUUID().toString();
    }

    private static long getTimeStamp() {
        return java.lang.System.currentTimeMillis();
    }

    protected static EiffelEvent.Builder prepareEiffelEvent() {
        final EiffelEvent.Builder event = EiffelEvent.newBuilder();
        final EiffelEvent.Meta.Source.Builder source = EiffelEvent.Meta.Source.newBuilder();
        final EiffelEvent.Meta.Builder meta = EiffelEvent.Meta.newBuilder();
        meta.setId(getEventId());
        meta.setTime(getTimeStamp());
        meta.setType(EiffelEvent.Meta.EventType.EiffelSourceChangeCreatedEvent);
        source.setHost(getHostNameOrNull() != null ? getHostNameOrNull() : DEFAULT);
        source.setName(getResourceNameOrNull() != null ? getResourceNameOrNull() : DEFAULT);
        meta.setSource(source.build());
        event.setMeta(meta.build());

        return event;
    }
}
