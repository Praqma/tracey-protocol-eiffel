package net.praqma.tracey.protocol.eiffel.factories;

import java.util.UUID;
import java.util.logging.Logger;

import com.google.protobuf.Message;
import net.praqma.tracey.protocol.eiffel.models.Models.Data;
import net.praqma.tracey.protocol.eiffel.models.Models.Meta;

public abstract class BaseFactory {
    private static final Logger log = Logger.getLogger( Meta.class.getName() );
    private Data.Source source = null;

    public BaseFactory(final String host, final String name, final String uri, final String domainId, final Data.Serializer gav) {
        source = Data.Source.newBuilder().setHost(host).setName(name).setUri(uri).setDomainId(domainId).setSerializer(gav).build();
    }

    public Data.Source getSource() {
        return source;
    }

    public abstract Message.Builder create();

    public Meta createMeta(final Meta.EiffelEventType type, final Data.Source source) {
        final Meta.Builder meta = Meta.newBuilder();
        meta.setId(UUID.randomUUID().toString());
        meta.setType(type);
        meta.setTime(java.lang.System.currentTimeMillis());
        meta.setSource(source);
        return meta.build();
    }
}
