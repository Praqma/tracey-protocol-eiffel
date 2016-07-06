package net.praqma.tracey.protocol.eiffel.cli;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;

import net.praqma.tracey.protocol.eiffel.events.EiffelSourceChangeCreatedEventOuterClass.EiffelSourceChangeCreatedEvent;
import net.praqma.tracey.protocol.eiffel.factories.EiffelSourceChangeCreatedEventFactory;
import net.praqma.tracey.protocol.eiffel.models.Models.Link;
import net.praqma.tracey.protocol.eiffel.models.Models.Data.Serializer;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.protobuf.util.JsonFormat;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.*;

public class Main {
    private static final Logger log = Logger.getLogger(Main.class.getName());

    public static void main (String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        ArgumentParser parser = ArgumentParsers.newArgumentParser("generator")
                .defaultHelp(true)
                .description("Generate Eiffel messages");
        Subparsers subparsers = parser.addSubparsers();
        Subparser eiffelSourceChangeCreatedEvent = subparsers.addParser("EiffelSourceChangeCreatedEvent");
        parser.addArgument("-f", "--file").dest("file").help("Path to the file to save generated message");
        parser.addArgument("-d", "--debug").dest("debug").action(Arguments.storeTrue()).setDefault(false).help("Output debug logs");
        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        if (ns.getBoolean("debug") == true) {
            Logger rootLog = Logger.getLogger("");
            rootLog.setLevel( Level.FINE );
            rootLog.getHandlers()[0].setLevel( Level.FINE );
        }


        Serializer gav = Serializer.newBuilder().setArtifactId("id").setGroupId("group").setVersion("1.0.0").build();
        final EiffelSourceChangeCreatedEventFactory factory = new EiffelSourceChangeCreatedEventFactory("host", "name", "uri", "domainId", gav);

        final List<Link> links = new ArrayList<>();
        links.add(Link.newBuilder().setType(Link.LinkType.PREVIOUS_VERSION).setId(UUID.randomUUID().toString()).build());
        links.add(Link.newBuilder().setType(Link.LinkType.CAUSE).setId(UUID.randomUUID().toString()).build());
        factory.parseFromGit(Paths.get(".").toAbsolutePath().normalize().toString(), "HEAD", "master");
        final EiffelSourceChangeCreatedEvent.Builder event = (EiffelSourceChangeCreatedEvent.Builder) factory.create();
        event.addAllLinks(links);

        if (ns.getString("file") != null) {
            File f = new File("example.json");
            if(f.exists()) {
                f.delete();
            }
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("example.json"), "utf-8"))) {
                writer.write(JsonFormat.printer().print(event));
            }
        } else {
            log.info(JsonFormat.printer().print(event));
        }
    }
}
