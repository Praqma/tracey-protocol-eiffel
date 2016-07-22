package net.praqma.tracey.protocol.eiffel.factories;

import com.google.protobuf.Message;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import net.praqma.tracey.protocol.eiffel.events.EiffelCompositionDefinedEventOuterClass.EiffelCompositionDefinedEvent;
import net.praqma.tracey.protocol.eiffel.events.EiffelCompositionDefinedEventOuterClass.EiffelCompositionDefinedEvent.EiffelCompositionDefinedEventData;
import net.praqma.tracey.protocol.eiffel.models.Models;
import net.praqma.tracey.protocol.eiffel.models.Models.Data.GAV;

import java.util.logging.Logger;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class EiffelCompositionDefinedEventFactory extends BaseFactory {
    private static final Logger LOG = Logger.getLogger( EiffelCompositionDefinedEventFactory.class.getName() );
    private static final EiffelCompositionDefinedEventData.Builder DATA = EiffelCompositionDefinedEventData.newBuilder();

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
        LOG.log(Level.FINE, "Set composition name to {0}", name);
        DATA.setName(name);
    }

    public void setVersion(String version) {
        LOG.log(Level.FINE, "Set composition version to {0}", version);
        DATA.setVersion(version);

    }

    public void parseFromPom(String pomPath) throws IOException, XmlPullParserException {
        LOG.fine(String.format("Read EiffelCompositionDefinedEvent GAV details from %s", pomPath));

        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(pomPath), "UTF-8")) ) {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(in);
            DATA.setVersion(model.getVersion());
        }
    }

    @Override
    public Message.Builder create() {
        final EiffelCompositionDefinedEvent.Builder event = EiffelCompositionDefinedEvent.newBuilder();
        event.setData(DATA);
        event.setMeta(createMeta(Models.Meta.EiffelEventType.EiffelCompositionDefinedEvent, source));
        event.addAllLinks(links);
        return event;
    }
}
