/*
以下のような設定ファイルの内容を表すレコード
[
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
]
*/

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public record TestCases(String className, List<TestCase> testCases) {
    public static TestCases fromJsonFile(String fileName) {
        String className = fileName.replace(".json", "");
        String jsonString = "";
        JSONArray rootArray = null;

        try {
            jsonString = Files.readString(Path.of(fileName));
            rootArray = new JSONArray(jsonString);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.printf("%s could not be read.\n", fileName);
            System.exit(1);

        } catch (JSONException e) {
            e.printStackTrace();
            System.out.printf("%s could not be parsed.\n", fileName);
            System.exit(1);
        }

        return TestCases.fromJSONArray(className, rootArray);
    }

    public static TestCases fromJSONArray(String className, JSONArray rootArray) {
        List<TestCase> testCases = new ArrayList<>();

        for (Object o : rootArray) {
            testCases.add(TestCase.fromJSONObject((JSONObject) o)); // ClassCastExceptionの可能性あり
        }

        return new TestCases(className, testCases);
    }
}
