import org.apache.commons.text.StringEscapeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class InvokeResults {
    public String methodName;
    public Boolean isStatic;
    public Map<String, String> argsAndStdOut;

    InvokeResults(String methodName, Boolean isStatic) {
        this.methodName = methodName;
        this.isStatic = isStatic;
        this.argsAndStdOut = new HashMap<>();
    }

    public void add(Object[] args, String stdOut) {
        this.argsAndStdOut.put(this.argsToString(args), this.stdOutToRawString(stdOut));
    }

    private String argsToString(Object[] args) {
        List<String> argStrList = new ArrayList<>();

        for (Object arg : args) {
            if (arg.getClass().isArray()) {
                String[] items =
                        Stream.of((String[]) arg).map(s -> "\"" + s + "\"").toArray(String[]::new);
                argStrList.add(String.format("new String[] {%s}", String.join(", ", items)));
            } else {
                argStrList.add(String.format("%s.valueOf(\"%s\")", arg.getClass().getSimpleName(), arg.toString()));
            }
        }

        return String.join(", ", argStrList);
    }

    private String stdOutToRawString(String stdOut) {
        String rawStdOut = StringEscapeUtils.escapeJava(stdOut);
        return "\"" + rawStdOut + "\"";
    }
}
