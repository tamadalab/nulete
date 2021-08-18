import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GenerateTest {
    private void run() {
        try {
            List<Class> classes = this.getClasses();
            for (Class c : classes) {
                System.out.printf("Class : %s\n", c.getName());

                for (Method m : c.getDeclaredMethods()) {
                    System.out.printf("Method : %s\n", m.getName());
                }

                System.out.println();
            }

            String[] classNames = classes.stream().map(c -> c.getName()).toArray(String[]::new);
            this.createEmptyFiles(classNames);

            for (Map.Entry<String, JSONArray> testcase : this.getTestCases().entrySet()) {
                String targetClassName = testcase.getKey();
                JSONArray jsonArr = testcase.getValue();

                System.out.printf("Class: %s\n", targetClassName);

                for (Object obj : jsonArr) {
                    JSONObject jsonObj = (JSONObject) obj;

                    String methodName = jsonObj.getString("method");
                    JSONArray args = jsonObj.getJSONArray("args");

                    System.out.printf("method: %s\n", methodName);
                    System.out.printf("args: %s\n", args.toString());
                }
            }
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

        for (Path filePath : filePaths) {
            Files.createFile(filePath);
        }
    }

    private Map<String, JSONArray> getTestCases() throws IOException, JSONException {
        File currendDir = new File(".");
        Map<String, JSONArray> testcases = new HashMap<>();
        for (String jsonFileName : this.getJsonFileNames(currendDir)) {
            String targetClassName = jsonFileName.replace(".json", "");
            String jsonString = Files.readString(Path.of(jsonFileName));
            testcases.put(targetClassName, new JSONArray(jsonString));
        }

        return testcases;
    }

    private List<String> getJsonFileNames(File dir) throws IOException {
        return Stream.of(dir.listFiles())
                .map(file -> file.getName())
                .filter(name -> name.endsWith(".json"))
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        new GenerateTest().run();
    }
}
