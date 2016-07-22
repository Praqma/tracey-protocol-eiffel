package net.praqma.tracey.protocol.eiffel.factories;

import com.google.protobuf.Message;
import net.praqma.tracey.protocol.eiffel.events.EiffelArtifactPublishedEventOuterClass.EiffelArtifactPublishedEvent;
import net.praqma.tracey.protocol.eiffel.events.EiffelArtifactPublishedEventOuterClass.EiffelArtifactPublishedEvent.EiffelArtifactPublishedEventData;
import net.praqma.tracey.protocol.eiffel.models.Models.Data.GAV;
import net.praqma.tracey.protocol.eiffel.models.Models.Data.Location;
import net.praqma.tracey.protocol.eiffel.models.Models.Meta;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EiffelArtifactPublishedEventFactory extends BaseFactory {
    private static final Logger LOG = Logger.getLogger( EiffelArtifactPublishedEventFactory.class.getName() );
    private static final EiffelArtifactPublishedEventData.Builder DATA = EiffelArtifactPublishedEventData.newBuilder();
    private static final List<Location> LOCATIONS = new ArrayList<>();

    public EiffelArtifactPublishedEventFactory(final String host, final String name, final String uri, final String domainId, final GAV gav) {
        super(host, name, uri, domainId, gav);
    }

    public EiffelArtifactPublishedEventFactory(final String name, final String uri, final String domainId, final GAV gav) {
        super(name, uri, domainId, gav);
    }

    public EiffelArtifactPublishedEventFactory(final String name, final String uri, final String domainId) {
        super(name, uri, domainId);
    }

    public void addLocation(Location location) {
        LOG.log(Level.FINE, "Add artifact location %s", location.toString());
        LOCATIONS.add(location);
    }

    @Override
    public Message.Builder create() {
        final EiffelArtifactPublishedEvent.Builder event = EiffelArtifactPublishedEvent.newBuilder();
        DATA.addAllLocations(LOCATIONS);
        event.setData(DATA);
        event.setMeta(createMeta(Meta.EiffelEventType.EiffelArtifactPublishedEvent, source));
        event.addAllLinks(links);
        return event;
    }
}
