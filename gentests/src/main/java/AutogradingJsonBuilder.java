import org.json.JSONArray;
import org.json.JSONObject;

public class AutogradingJsonBuilder {
    private JSONArray tests;

    AutogradingJsonBuilder() {
        this.tests = new JSONArray();
    }

    public void addTest(String name, String className, String testName, Integer timeout, Integer point) {
        JSONObject test = new JSONObject();
        test.put("name", name);
        test.put("setup", "");
        test.put("run", String.format("gradle test --tests %sTest.%s", className, testName));
        test.put("input", "");
        test.put("output", "");
        test.put("comparison", "included");
        test.put("timeout", timeout);
        test.put("points", point);
        this.tests.put(test);
    }

    public String toString() {
        JSONObject jo = new JSONObject();
        jo.put("tests", this.tests);
        return jo.toString(4);
    }
}
