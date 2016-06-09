package net.praqma.tracey.protocol.eiffel;

import java.util.UUID;
import java.util.logging.Logger;
import net.praqma.tracey.protocol.eiffel.EiffelEventOuterClass.*;

public class MetaFactory {
    private static final Logger log = Logger.getLogger( EiffelEvent.Meta.class.getName() );

    // TODO: Fix me
    private static EiffelEvent.Meta.Source getSource() {
        final EiffelEvent.Meta.Source.Builder source = EiffelEvent.Meta.Source.newBuilder();
        return source.build();
    }

    public static EiffelEvent.Meta create(final String domainId, final EiffelEvent.Meta.EventType type) {
        final EiffelEvent.Meta.Builder meta = EiffelEventOuterClass.EiffelEvent.Meta.newBuilder();
        meta.setId(UUID.randomUUID().toString());
        meta.setDomainId(domainId);
        meta.setType(type);
        // TODO: figure out how to set version
        meta.setVersion("One Day it will be a version in here");
        meta.setTime(java.lang.System.currentTimeMillis());
        meta.setSource(getSource());
        return meta.build();
    }
}
