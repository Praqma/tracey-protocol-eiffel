package net.praqma.tracey.protocol.eiffel.factories;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import com.google.protobuf.Message;
import com.jcabi.manifests.Manifests;
import java.util.logging.Level;
import net.praqma.tracey.protocol.eiffel.models.Models.Link;
import net.praqma.tracey.protocol.eiffel.models.Models.Data;
import net.praqma.tracey.protocol.eiffel.models.Models.Meta;

public abstract class BaseFactory {
    private static final Logger LOG = Logger.getLogger( BaseFactory.class.getName() );
    protected Data.Source source = null;
    protected final List<Link> links = new ArrayList<>();

    public BaseFactory(final String host, final String name, final String uri, final String domainId, final Data.GAV gav) {
        source = Data.Source.newBuilder().setHost(host).setName(name).setUri(uri).setDomainId(domainId).setSerializer(gav).build();
        LOG.log(Level.FINE, "New factory with the source: {0}", source.toString());
    }

    public BaseFactory(final String name, final String uri, final String domainId, final Data.GAV gav) {
        source = Data.Source.newBuilder().setHost(getHostName()).setName(name).setUri(uri).setDomainId(domainId).setSerializer(gav).build();
        LOG.log(Level.FINE, "New factory with the source: {0}", source.toString());
    }

    public BaseFactory(final String name, final String uri, final String domainId) {
        source = Data.Source.newBuilder().setHost(getHostName()).setName(name).setUri(uri).setDomainId(domainId).setSerializer(getGAV()).build();
        LOG.log(Level.FINE, "New factory with the source: {0}", source.toString());
    }

    public void addLink(final Link link) {
        LOG.log(Level.FINE, "Add link {0}", link.toString());
        links.add(link);
    }

    public abstract Message.Builder create();

    public Meta createMeta(final Meta.EiffelEventType type, final Data.Source source) {
        final Meta.Builder meta = Meta.newBuilder();
        meta.setId(UUID.randomUUID().toString());
        meta.setType(type);
        meta.setTime(java.lang.System.currentTimeMillis());
        meta.setSource(source);
        LOG.log(Level.FINE, "Create new meta {0}", meta.toString());
        return meta.build();
    }

    private String getHostName() {
        String hostname = "Unknown";
        try
        {
            hostname = InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException e) {
            LOG.log(Level.WARNING, "Hostname can not be resolved due to the following. Use {0} as a hostname\n{1}", new Object[]{hostname, e.getMessage()});
        }
        LOG.log(Level.FINE, "Return hostname: {0}", hostname);
        return hostname;
    }

    private Data.GAV getGAV() {
        Data.GAV gav = Data.GAV.newBuilder()
                .setGroupId(Manifests.read("Implementation-Vendor-Id"))
                .setArtifactId(Manifests.read("Implementation-Title"))
                .setVersion(Manifests.read("Implementation-Version"))
                .build();
        LOG.log(Level.FINE, "Return GAV: {0}", gav.toString());
        return gav;
    }
}
