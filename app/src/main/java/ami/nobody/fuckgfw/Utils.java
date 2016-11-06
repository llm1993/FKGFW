package ami.nobody.fuckgfw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by samsung on 16-11-6.
 */

public class Utils {

    private static String urlStr = "https://raw.githubusercontent.com/racaljk/hosts/master/hosts";

    public static void main(String[] a) {
        merge(urlStr);
    }

    public static boolean checkNetwork() {

        try {
            Request request = new Request.Builder().url("http://www.qualcomm.cn/generate_204").build();
            Response response = client.newCall(request).execute();
            if (response.code() == 204) {
                return true;
            } else
                return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    private static OkHttpClient client = new OkHttpClient();

    public static Map<String, String> merge(String urlStr) {
        Map map = new LinkedHashMap();
        if (urlStr != null) {
            if (urlStr.contains(",")) {
                String urls[] = urlStr.split(",");
                for (String url : urls) {
                    execute(map, url.trim());
                }
            } else {
                execute(map, urlStr.trim());

            }
        }
        return map;
    }

    private static void execute(Map map, String url) {
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = client.newCall(request).execute();
            Reader reader = response.body().charStream();
            BufferedReader bfr = new BufferedReader(reader);
            String line;
            while ((line = bfr.readLine()) != null) {
                check(map, line);
            }
        } catch (Exception e) {
            e.printStackTrace();
            map = new LinkedHashMap();
        }
    }

    private static void check(Map map, String line) {
        if (line.trim().length() == 0)
            return;
        char header = line.trim().charAt(0);
        if (!(header == '#') && (header >= '1' && header <= '9')) {
            // contains more than one space
            while (line.contains("  ")) {
                line = line.replace("  ", " ");
            }
            while (line.contains("\t")) {
                line = line.replace("\t", " ");
            }
            String strArr[] = line.split(" ");
            String ip = strArr[0].trim();
            String host = strArr[1].trim();
            if (host.contains("."))
                map.put(host, ip);
        }
    }
}
