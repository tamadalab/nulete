import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.text.StringEscapeUtils;

public record Result(String methodName, Boolean isStatic, String argsString, String rawStdIn, String rawStdOut,
        String rawComment) {
    public static Result fromObjectArgsAndNotRawString(String methodName, Boolean isStatic, Object[] args, String stdIn,
            String stdOut, String comment) {
        String argsString = Result.objectArgsToString(args);
        String rawStdIn = StringEscapeUtils.escapeJava(stdIn);
        String rawStdOut = StringEscapeUtils.escapeJava(stdOut);
        String rawComment = StringEscapeUtils.escapeJava(comment);

        return new Result(methodName, isStatic, argsString, rawStdIn, rawStdOut, rawComment);
    }

    private static String objectArgsToString(Object[] args) {
        List<String> argStrList = new ArrayList<>();

        for (Object arg : args) {
            if (arg.getClass().isArray()) {
                String[] items = Stream.of((String[]) arg).map(s -> "\"" + s + "\"").toArray(String[]::new);
                argStrList.add(String.format("new String[] {%s}", String.join(", ", items)));
            } else {
                argStrList.add(String.format("%s.valueOf(\"%s\")", arg.getClass().getSimpleName(), arg.toString()));
            }
        }

        return String.join(", ", argStrList);
    }
}
