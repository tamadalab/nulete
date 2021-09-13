import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

public class GenerateTest {
    private void run() {
        PrintStream defaultStdOut = System.out;
        try {
            TargetClassLoader loader = new TargetClassLoader();
            Map<String, JSONArray> testcases = this.getTestCases();

            for (Map.Entry<String, JSONArray> ety : testcases.entrySet()) {
                String className = ety.getKey();
                JSONArray testcase = ety.getValue();

                System.out.printf("  Class: %s\n", className);

                Class<?> c = loader.loadClass(className);
                Object instance = c.getDeclaredConstructor().newInstance();

                for (Method m : c.getDeclaredMethods()) {
                    String methodName = m.getName();
                    Boolean isStatic = Modifier.isStatic(m.getModifiers());
                    m.setAccessible(true);

                    System.out.printf("    Method: %s\n", methodName);

                    for (Object o : testcase) {
                        JSONObject jsonObj = (JSONObject) o;
                        String targetMethodName = jsonObj.getString("method");

                        if (!methodName.equals(targetMethodName)) {
                            continue;
                        }

                        Object[] args = ArgsParser.parseMethodArgs(m, jsonObj.getJSONArray("args"));

                        System.out.printf("      Args:");
                        for (Object arg: args) {
                            if (arg.getClass().isArray()) {
                                System.out.printf("[");
                                for (Object item: (Object[]) arg) {
                                    System.out.printf("%s,", item);
                                }
                                System.out.printf("],");
                            } else {
                                System.out.printf("%s,", arg);
                            }
                        }
                        System.out.println();

                        ByteArrayOutputStream baos = this.stdOutCaptureStart();

                        if (isStatic) {
                            m.invoke(null, args);
                        } else {
                            m.invoke(instance, args);
                        }

                        System.setOut(defaultStdOut);

                        System.out.println(baos.toString());
                    }
                }

                System.out.println();
            }

            this.createEmptyFiles(testcases.keySet().toArray(new String[0]));
        } catch (Exception e) {
            System.setOut(defaultStdOut);
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void createEmptyFiles(String[] classNames) throws IOException {
        Path[] filePaths =
                Stream.of(classNames).map(name -> Path.of(name + "Test.java")).toArray(Path[]::new);

        for (Path filePath : filePaths) {
            Files.createFile(filePath);
        }
    }

    private Map<String, JSONArray> getTestCases() throws IOException, JSONException {
        File currentDir = new File(".");
        Map<String, JSONArray> testcases = new HashMap<>();
        for (String jsonFileName : this.getJsonFileNames(currentDir)) {
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

    private ByteArrayOutputStream stdOutCaptureStart() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        return baos;
    }

    public static void main(String[] args) {
        new GenerateTest().run();
    }
}
