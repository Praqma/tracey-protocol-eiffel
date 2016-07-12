package net.praqma.tracey.protocol.eiffel.factories;

import com.google.protobuf.Message;
import net.praqma.tracey.protocol.eiffel.events.EiffelArtifactCreatedEventOuterClass.EiffelArtifactCreatedEvent;
import net.praqma.tracey.protocol.eiffel.events.EiffelArtifactCreatedEventOuterClass.EiffelArtifactCreatedEvent.FileInformation;
import net.praqma.tracey.protocol.eiffel.events.EiffelArtifactCreatedEventOuterClass.EiffelArtifactCreatedEvent.EiffelArtifactCreatedEventData;
import net.praqma.tracey.protocol.eiffel.models.Models.Data.Serializer;
import net.praqma.tracey.protocol.eiffel.models.Models.Meta;
import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class EiffelArtifactCreatedEventFactory extends BaseFactory {
    private static final Logger log = Logger.getLogger( EiffelArtifactCreatedEventFactory.class.getName() );
    private static final EiffelArtifactCreatedEventData.Builder data = EiffelArtifactCreatedEventData.newBuilder();
    private static final List<FileInformation> fileInformationList = new ArrayList<>();

    public EiffelArtifactCreatedEventFactory(final String host, final String name, final String uri, final String domainId, final Serializer gav) {
        super(host, name, uri, domainId, gav);
    }

    public void parseFromPom(final String path) throws IOException, XmlPullParserException {
        log.fine("Read EiffelArtifactCreatedEvent GAV details from " + path);
        final BufferedReader in = new BufferedReader(new FileReader(path));
        final MavenXpp3Reader reader = new MavenXpp3Reader();
        final Model model = reader.read(in);
        setGav(model.getGroupId(), model.getArtifactId(), model.getVersion());
    }

    public void setBuildCommand(final String command) {
        log.fine("Set build command to " + command);
        data.setBuildCommand(command);
    }

    public void setGav(final String groupId, final String artifactId, final String version) {
        final Serializer.Builder gav = Serializer.newBuilder();
        gav.setGroupId(groupId).setArtifactId(artifactId).setVersion(version);
        data.setGav(gav.build());
        log.fine("Set GAV to the following:\nGroupId " + data.getGav().getGroupId());
        log.fine("ArtifactId " + data.getGav().getArtifactId());
        log.fine("Version " + data.getGav().getVersion());
    }

    public void addFileInformation(FileInformation fileInformation) {
        log.fine("Add file information " + fileInformation.toString());
        fileInformationList.add(fileInformation);
    }

    @Override
    public Message.Builder create() {
        final EiffelArtifactCreatedEvent.Builder event = EiffelArtifactCreatedEvent.newBuilder();
        data.addAllFileInformation(fileInformationList);
        event.setData(data);
        event.setMeta(createMeta(Meta.EiffelEventType.EiffelArtifactCreatedEvent, source));
        event.addAllLinks(links);
        return event;
    }
}
