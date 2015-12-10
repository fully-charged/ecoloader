package devious.loader;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public final class ClientStarter {

    private ClientStarter() {
    }

    public static void start(final File jar) throws Exception {
        final ClassLoader cl = new URLClassLoader(new URL[]{jar.toURI().toURL()});
        final Class clientClass = cl.loadClass("Client");
        final Method mainMethod = clientClass.getDeclaredMethod("main", String[].class);
        mainMethod.invoke(null, (Object) new String[0]);
    }
}
