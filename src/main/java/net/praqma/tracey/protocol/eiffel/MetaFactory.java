package net.praqma.tracey.protocol.eiffel;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import net.praqma.tracey.protocol.eiffel.EiffelEventOuterClass.*;

public class MetaFactory {
    private static final Logger log = Logger.getLogger( EiffelEvent.Meta.class.getName() );

    public static EiffelEvent.Meta.Source getSource(final String host, final String name, final String uri, final EiffelEvent.Meta.Source.Serializer gav) {
        final EiffelEvent.Meta.Source.Builder source = EiffelEvent.Meta.Source.newBuilder();
        source.setHost(host);
        source.setName(name);
        source.setUri(uri);
        source.setSerializer(gav);
        return source.build();
    }

    public static EiffelEvent.Meta.Source.Serializer getSerializer(final String artifactId, final String version, final String groupId) {
        final EiffelEvent.Meta.Source.Serializer.Builder gav = EiffelEvent.Meta.Source.Serializer.newBuilder();
        return gav.setGroupId(groupId).setArtifactId(artifactId).setVersion(version).build();
    }

    public static EiffelEvent.Meta create(final String domainId, final EiffelEvent.Meta.EventType type, final EiffelEvent.Meta.Source source) {
        final EiffelEvent.Meta.Builder meta = EiffelEventOuterClass.EiffelEvent.Meta.newBuilder();
        meta.setId(UUID.randomUUID().toString());
        meta.setDomainId(domainId);
        meta.setType(type);
        meta.setTime(java.lang.System.currentTimeMillis());
        meta.setSource(source);
        return meta.build();
    }
}
