import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

public class TestClassBuilder {
    private String className;
    private StringBuilder testMethodSB;

    TestClassBuilder(String className) {
        this.className = className;
        this.testMethodSB = new StringBuilder();
    }

    public void addTestMethod(String testName, List<Result> results) {
        String invokeTemplate = this.readResource("InvokeTemplate.txt");
        String testMethodTemplate = this.readResource("TestMethodTemplate.txt");
        StringBuilder invokeSB = new StringBuilder();

        for (Result r : results) {
            String receiver = r.isStatic() ? this.className : String.format("new %s()", this.className);
            invokeSB.append(String.format(invokeTemplate, r.rawStdIn(), receiver, r.methodName(), r.argsString(),
                    r.rawStdOut(), r.rawComment()));
        }

        this.testMethodSB.append(String.format(testMethodTemplate, testName, invokeSB));
    }

    public String toString() {
        String testClassTemplate = this.readResource("TestClassTemplate.txt");

        return String.format(testClassTemplate, this.className + "Test", this.testMethodSB);
    }

    private String readResource(String path) {
        InputStream in = this.getClass().getResourceAsStream(path);
        BufferedReader bu = new BufferedReader(new InputStreamReader(in));
        String s = bu.lines().collect(Collectors.joining("\n"));

        return s;
    }
}
