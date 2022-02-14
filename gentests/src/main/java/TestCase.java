/*
テストケースを表すレコード
設定ファイルの以下の部分を扱うためのもの

{
  "name": "",
  "timeout": 0,
  "point": 0
  "tests": [
    {
      "method": "",
      "args": [],
      "stdin": [],
      "comment": ""
    }
  ]
}
*/

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public record TestCase(String name, String testName, Integer timeout, Integer point, List<TestItem> testItems) {
    public static TestCase fromJSONObject(JSONObject jo) {
        String name = "";
        String testName = "";
        Integer timeout = 0;
        Integer point = 0;
        List<TestItem> testItems = null;

        try {
            name = jo.getString("name");
            testName = "_" + name.replace(" ", "_");
            timeout = jo.has("timeout") ? jo.getInt("timeout") : 10;
            point = jo.has("point") ? jo.getInt("point") : 0;
            testItems = new ArrayList<>();

            for (Object o : jo.getJSONArray("tests")) {
                testItems.add(TestItem.fromJSONObject((JSONObject) o)); // ClassCastExceptionの可能性あり
            }

        } catch (JSONException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return new TestCase(name, testName, timeout, point, testItems);
    }
}
