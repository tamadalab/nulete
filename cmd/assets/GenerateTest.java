import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class GenerateTest {
    public static void main(String[] args) {
        File currentDir = new File(".");
        File[] files = currentDir.listFiles();
        File[] classFiles = Stream.of(files)
            .filter(file -> file.getName().endsWith(".class"))
            .filter(file -> file.getName() != "GenerateTest.class")
            .toArray(File[]::new);

        for (File classFile: classFiles) {
            Path testJavaPath = Path.of(classFile.getName().replace(".class", "Test.java"));

            try {
                Files.createFile(testJavaPath);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
}
