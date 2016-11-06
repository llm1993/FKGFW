package ami.nobody.fuckgfw;

import android.os.Handler;
import android.os.Message;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class HttpThread extends Thread {
    private Handler handler;
    private String urlStr;
    private boolean radio1 = false;
    private boolean radio2 = false;

    public HttpThread(Handler handler, String urlStr, boolean radio1, boolean radio2) {
        this.handler = handler;
        if (urlStr != null && !urlStr.equals("")) {
            this.urlStr = urlStr;
        }
        this.radio1 = radio1;
        this.radio2 = radio2;
    }

    @Override
    public void run() {

        if (!Utils.checkNetwork()) {
            handler.sendMessage(handler.obtainMessage(0, "网络不可用！！！"));
            return;
        }

        int cmdResult = -1;
        boolean isRoot = ShellUtils.checkRootPermission();
        if (!isRoot) {
            handler.sendMessage(handler.obtainMessage(0, "not get root!!!"));
            return;
        }

        handler.sendMessage(handler.obtainMessage(0, "start"));

        try {
            List<String> cmdList = new ArrayList<String>();
            cmdList.add("mount -o rw,remount /system");
            if (radio2)
                cmdList.add("echo \"127.0.0.1\tlocalhost\" > /etc/hosts");
            Map<String, String> map = Utils.merge(urlStr);

            if (map.size() == 0) {
                handler.sendMessage(handler.obtainMessage(0, "更新失败!!!"));
                return;
            }
            Iterator iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                int temp = 0;
                String key = (String) iterator.next();
                if (!radio1) {
                    if (map.get(key).equals("127.0.0.1")) {
                        continue;
                    }
                }
                cmdList.add("echo \"" + map.get(key) + "\t" + key + "\" >> /etc/hosts");
            }
            cmdList.add("chmod 644 /etc/hosts");
            cmdResult = ShellUtils.execCommand(cmdList);
        } catch (Exception e) {
            handler.sendMessage(handler.obtainMessage(0, "更新失败!!!"));
            return;
        }
        Message msg = handler.obtainMessage();
        if (cmdResult == 0) {
            msg.obj = "update hosts success!";
        } else {
            msg.obj = "更新失败!!!";
        }
        handler.sendMessage(msg);
    }

    private boolean check(String line) {
        line = line.trim();
        if (line.equals(""))
            return false;
        char headchar = line.charAt(0);
        if (headchar == '#' || (headchar >= '1' && headchar < '9'))
            return true;
        return false;
    }
}
