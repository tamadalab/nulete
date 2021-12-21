import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;


public class TargetClassLoader {
    private URL[] urls;

    TargetClassLoader() throws MalformedURLException {
        File currentDir = new File(".");
        this.urls = new URL[] {currentDir.toURI().toURL()};
    }

    Class loadClass(String className) throws ClassNotFoundException, IOException {
        try (URLClassLoader urlcl = new URLClassLoader(this.urls)) {
            return urlcl.loadClass(className);
        }
    }
}
