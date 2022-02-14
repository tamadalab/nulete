import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

public class ArgsParser {
    public static Object[] parseMethodArgs(Method m, JSONArray args) throws JSONException, Exception {
        Class<?>[] types = m.getParameterTypes();

        if (types.length != args.length()) {
            throw new Exception("引数の数が一致しません。");
        }

        List<Object> objList = new ArrayList<>();
        for (int i = 0; i < types.length; i++) {
            Class<?> c = types[i];

            if (c.equals(int.class) || c.equals(Integer.class)) {
                objList.add(args.getInt(i));
            } else if (c.equals(long.class) || c.equals(Long.class)) {
                objList.add(args.getLong(i));
            } else if (c.equals(float.class) || c.equals(Float.class)) {
                objList.add(args.getFloat(i));
            } else if (c.equals(double.class) || c.equals(Double.class)) {
                objList.add(args.getDouble(i));
            } else if (c.equals(String.class)) {
                objList.add(args.getString(i));
            } else if (c.equals(String[].class)) {
                JSONArray jsonArr = args.getJSONArray(i);
                List<String> strList = new ArrayList<>();

                for (int ii = 0; ii < jsonArr.length(); ii++) {
                    strList.add(jsonArr.getString(ii));
                }

                objList.add(strList.toArray(new String[0]));
            } else {
                throw new Exception("引数の型が未対応です。");
            }
        }

        return objList.toArray();
    }
}
