import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GenerateTest {
    private void run() {
        PrintStream defaultStdOut = System.out;

        try {
            TargetClassLoader loader = new TargetClassLoader();
            AutogradingJsonBuilder ajb = new AutogradingJsonBuilder();

            for (String jsonFileName : this.getJsonFileNames()) {
                TestCases tcs = TestCases.fromJsonFile(jsonFileName);

                Class<?> c = loader.loadClass(tcs.className());
                Constructor<?> constructor = c.getDeclaredConstructor();
                constructor.setAccessible(true);

                TestClassBuilder tcb = new TestClassBuilder(tcs.className());

                for (TestCase tc : tcs.testCases()) {
                    List<Result> results = new ArrayList<>();

                    for (TestItem ti : tc.testItems()) {
                        for (Method m : c.getDeclaredMethods()) {
                            if (!ti.method().equals(m.getName())) {
                                continue;
                            }

                            m.setAccessible(true);

                            Boolean isStatic = Modifier.isStatic(m.getModifiers());
                            Object[] args = ArgsParser.parseMethodArgs(m, ti.args());
                            System.setIn(new ByteArrayInputStream(ti.stdin().getBytes()));
                            ByteArrayOutputStream baos = this.stdOutCaptureStart();

                            if (isStatic) {
                                m.invoke(null, args);
                            } else {
                                m.invoke(constructor.newInstance(), args);
                            }

                            results.add(Result.fromObjectArgsAndNotRawString(ti.method(), isStatic, args, ti.stdin(),
                                    baos.toString(), ti.comment()));
                            break;
                        }
                    }

                    ajb.addTest(tc.name(), tcs.className(), tc.testName(), tc.timeout(), tc.point());
                    tcb.addTestMethod(tc.testName(), results);
                }

                Files.writeString(Path.of(tcs.className() + "Test.java"), tcb.toString());
            }

            Files.writeString(Path.of("autograding.json"), ajb.toString());

        } catch (Exception e) {
            System.setOut(defaultStdOut);
            e.printStackTrace();
            System.exit(1);
        }
    }

    private List<String> getJsonFileNames() throws IOException {
        return Stream.of(new File(".").listFiles()).map(file -> file.getName()).filter(name -> name.endsWith(".json"))
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
