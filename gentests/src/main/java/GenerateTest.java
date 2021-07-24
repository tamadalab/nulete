import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GenerateTest {
    private void run() {
        try {
            List<Class> classes = this.getClasses();
            for (Class c : classes) {
                System.out.printf("Class : %s\n", c.getName());
                try {
                    for (Method m : c.getDeclaredMethods()) {
                        System.out.printf("Method : %s\n", m.getName());
                    }
                } catch (SecurityException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
                System.out.println();
            }

            String[] classNames = classes.stream().map(c -> c.getName()).toArray(String[]::new);
            this.createEmptyFiles(classNames);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private List<Class> getClasses()
            throws MalformedURLException, ClassNotFoundException, IOException {
        File currentDir = new File(".");
        List<String> classNames = this.getClassNames(currentDir);
        List<Class> classes = new ArrayList<>();
        URL[] urls = new URL[] {currentDir.toURI().toURL()};

        try (URLClassLoader urlcl = new URLClassLoader(urls)) {
            for (String className : classNames) {
                classes.add(urlcl.loadClass(className));
            }
        }

        return classes;
    }

    private List<String> getClassNames(File dir) {
        return Stream.of(dir.listFiles())
                .map(file -> file.getName())
                .filter(name -> name.endsWith(".class"))
                .map(name -> name.replace(".class", ""))
                .collect(Collectors.toList());
    }

    private void createEmptyFiles(String[] classNames) throws IOException {
        Path[] filePaths =
                Stream.of(classNames).map(name -> Path.of(name + "Test.java")).toArray(Path[]::new);

        try {
            for (Path filePath : filePaths) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        new GenerateTest().run();
    }
}
