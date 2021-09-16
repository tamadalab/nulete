import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GenerateTest {
    private void run() {
        PrintStream defaultStdOut = System.out;
        try {
            TargetClassLoader loader = new TargetClassLoader();
            Map<String, JSONArray> testcases = this.getTestCases();

            for (Map.Entry<String, JSONArray> ety : testcases.entrySet()) {
                String className = ety.getKey();
                JSONArray testcase = ety.getValue();

                Class<?> c = loader.loadClass(className);
                Object instance = c.getDeclaredConstructor().newInstance();

                TestClassBuilder builder = new TestClassBuilder(className);

                for (Method m : c.getDeclaredMethods()) {
                    String methodName = m.getName();
                    Boolean isStatic = Modifier.isStatic(m.getModifiers());
                    m.setAccessible(true);

                    InvokeResults results = new InvokeResults(methodName, isStatic);

                    for (Object o : testcase) {
                        JSONObject jsonObj = (JSONObject) o;
                        String targetMethodName = jsonObj.getString("method");

                        if (!methodName.equals(targetMethodName)) {
                            continue;
                        }

                        Object[] args = ArgsParser.parseMethodArgs(m, jsonObj.getJSONArray("args"));

                        ByteArrayOutputStream baos = this.stdOutCaptureStart();

                        if (isStatic) {
                            m.invoke(null, args);
                        } else {
                            m.invoke(instance, args);
                        }

                        System.setOut(defaultStdOut);

                        results.add(args, baos.toString());
                    }

                    builder.addTestMethod(results);
                }

                Files.writeString(Path.of(className + "Test.java"), builder.toString());
            }

        } catch (Exception e) {
            System.setOut(defaultStdOut);
            e.printStackTrace();
            System.exit(1);
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
