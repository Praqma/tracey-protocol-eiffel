package net.praqma.tracey.protocol.eiffel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Logger;
import net.praqma.tracey.protocol.eiffel.EiffelEventOuterClass.*;

public class EiffelEventFactory {
    private static final Logger log = Logger.getLogger( EiffelEvent.class.getName() );

    public static EiffelEvent create(final EiffelEvent.Meta meta,
                                     final com.google.protobuf.GeneratedMessage data,
                                     final List<EiffelEvent.Link> links) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final EiffelEvent.Builder event = EiffelEvent.newBuilder();
        event.setMeta(meta);
        event.addAllLinks(links);
        // Determine data type provided and call correct method to set it
        final String dataClassName = meta.getType().name();
        log.info("Provided data type: " + dataClassName);
        // TODO: Based on naming convention - it should be a better way
        final String dataClassFullName = "net.praqma.tracey.protocol.eiffel." + dataClassName + "OuterClass$" + dataClassName;
        final Class<?> dataClass = Class.forName(dataClassFullName);
        log.info("Provided data type: " + dataClass.getCanonicalName());
        final Method setDataMethod = event.getClass().getMethod("set" + dataClassName, dataClass);
        setDataMethod.invoke(event, dataClass.cast(data));
        return event.build();
    }
}
