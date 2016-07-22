package net.praqma.tracey.protocol.eiffel.factories;

import com.google.protobuf.Message;
import net.praqma.tracey.protocol.eiffel.events.EiffelArtifactCreatedEventOuterClass.EiffelArtifactCreatedEvent;
import net.praqma.tracey.protocol.eiffel.events.EiffelArtifactCreatedEventOuterClass.EiffelArtifactCreatedEvent.FileInformation;
import net.praqma.tracey.protocol.eiffel.events.EiffelArtifactCreatedEventOuterClass.EiffelArtifactCreatedEvent.EiffelArtifactCreatedEventData;
import net.praqma.tracey.protocol.eiffel.models.Models.Data.GAV;
import net.praqma.tracey.protocol.eiffel.models.Models.Meta;
import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class EiffelArtifactCreatedEventFactory extends BaseFactory {
    private static final Logger LOG = Logger.getLogger( EiffelArtifactCreatedEventFactory.class.getName() );
    private static final EiffelArtifactCreatedEventData.Builder DATA = EiffelArtifactCreatedEventData.newBuilder();
    private static final List<FileInformation> FILEINFORMATIONLIST = new ArrayList<>();

    public EiffelArtifactCreatedEventFactory(final String host, final String name, final String uri, final String domainId, final GAV gav) {
        super(host, name, uri, domainId, gav);
    }

    public EiffelArtifactCreatedEventFactory(final String name, final String uri, final String domainId, final GAV gav) {
        super(name, uri, domainId, gav);
    }

    public EiffelArtifactCreatedEventFactory(final String name, final String uri, final String domainId) {
        super(name, uri, domainId);
    }

    public void parseFromPom(final String path) throws IOException, XmlPullParserException {
        LOG.fine(String.format("Read EiffelArtifactCreatedEvent GAV details from %s", path));

        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8")) ) {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(in);
            setGav(model.getGroupId(), model.getArtifactId(), model.getVersion());
        }
    }

    public void setBuildCommand(final String command) {
        LOG.fine(String.format("Set build command to %s", command));
        DATA.setBuildCommand(command);
    }

    public void setGav(final String groupId, final String artifactId, final String version) {
        final GAV.Builder gav = GAV.newBuilder();
        gav.setGroupId(groupId).setArtifactId(artifactId).setVersion(version);
        DATA.setGav(gav.build());
        LOG.fine(String.format("Set GAV to the following:%nGroupId %s", DATA.getGav().getGroupId()));
        LOG.fine(String.format("ArtifactId %s", DATA.getGav().getArtifactId()));
        LOG.fine(String.format("Version %s", DATA.getGav().getVersion()));
    }

    public void addFileInformation(FileInformation fileInformation) {
        LOG.fine(String.format("Add file information %s", fileInformation.toString()));
        FILEINFORMATIONLIST.add(fileInformation);
    }

    @Override
    public Message.Builder create() {
        final EiffelArtifactCreatedEvent.Builder event = EiffelArtifactCreatedEvent.newBuilder();
        DATA.addAllFileInformation(FILEINFORMATIONLIST);
        event.setData(DATA);
        event.setMeta(createMeta(Meta.EiffelEventType.EiffelArtifactCreatedEvent, source));
        event.addAllLinks(links);
        return event;
    }
}
