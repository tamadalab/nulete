/*
テスト項目を表すレコード
設定ファイルの以下の部分を扱うためのもの

{
  "method": "",
  "args": [],
  "stdin": [],
  "comment": ""
}
*/

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public record TestItem(String method, JSONArray args, String stdin, String comment) {
    public static TestItem fromJSONObject(JSONObject jo) {
        String method = "";
        JSONArray args = null;
        String stdin = "";
        String comment = "";

        try {
            method = jo.getString("method");
            args = jo.getJSONArray("args");
            stdin = jo.has("stdin") ? TestItem.stdinFromJSONArray(jo.getJSONArray("stdin")) : "";
            comment = jo.has("comment") ? jo.getString("comment") : "something wrong";

        } catch (JSONException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return new TestItem(method, args, stdin, comment);
    }

    private static String stdinFromJSONArray(JSONArray ja) throws JSONException {
        List<String> lines = new ArrayList<>();
        for (Integer index = 0; index < ja.length(); index++) {
            lines.add(ja.getString(index));
        }
        return String.join("\n", lines);
    }
}
