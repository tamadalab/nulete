import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

public class TestClassBuilder {
    private String className;
    private StringBuilder testMethodSB;

    TestClassBuilder(String className) {
        this.className = className;
        this.testMethodSB = new StringBuilder();
    }

    public void addTestMethod(InvokeResults results) {
        if (results.argsAndStdOut.isEmpty()) {
            return;
        }

        String invokeTemplate = this.readResource("InvokeTemplate.txt");
        String testMethodTemplate = this.readResource("TestMethodTemplate.txt");
        String receiver;
        String methodName = results.methodName;
        StringBuilder invokeSB = new StringBuilder();

        if (results.isStatic) {
            receiver = this.className;
        } else {
            invokeSB.append(
                    String.format(
                            "\n        %s instance = new %s();\n", this.className, this.className));
            receiver = "instance";
        }

        for (Map.Entry<String, String> ety : results.argsAndStdOut.entrySet()) {
            String args = ety.getKey();
            String stdOut = ety.getValue();
            invokeSB.append(
                    String.format(invokeTemplate, receiver, methodName, args, stdOut));
        }

        this.testMethodSB.append(String.format(testMethodTemplate, methodName + "Test", invokeSB));
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
