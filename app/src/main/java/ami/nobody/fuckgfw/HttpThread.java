package ami.nobody.fuckgfw;

import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class HttpThread extends Thread {
    private Handler handler;

    public HttpThread(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            URL url = new URL("http://www.qualcomm.cn/generate_204");
            HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setRequestMethod("GET");
            if (httpUrlConnection.getResponseCode() != 204) {
                Message msg = new Message();
                msg.obj = "网络不可用！！！";
                handler.sendMessage(msg);
                return;
            }
        } catch (Exception e) {
            Message msg = new Message();
            msg.obj = "更新失败！！！";
            handler.sendMessage(msg);
            return;
        }

        int cmdResult = -1;
        boolean isRoot = ShellUtils.checkRootPermission();
        Message msg = new Message();
        if (!isRoot) {
            msg.obj = "not get root!!!";
            handler.sendMessage(msg);
            return;
        }
        msg.obj = "start";
        handler.sendMessage(msg);
        try {
            URL url = new URL("https://raw.githubusercontent.com/racaljk/hosts/master/hosts");
            HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setRequestMethod("GET");
            BufferedReader bfr = new BufferedReader(new InputStreamReader(httpUrlConnection.getInputStream()));
            List<String> cmdList = new ArrayList<String>();
            cmdList.add("mount -o rw,remount /system");
            cmdList.add("echo \"\" > /etc/hosts");
            String line = null;
            while ((line = bfr.readLine()) != null) {
                cmdList.add("echo \"" + line + "\" >> /etc/hosts");
            }
            cmdList.add("chmod 644 /etc/hosts");
            cmdResult = ShellUtils.execCommand(cmdList);
        } catch (Exception e) {
            msg.obj = "更新失败!!!";
            handler.sendMessage(msg);
            return;
        }
        msg = new Message();
        if (cmdResult == 0) {
            msg.obj = "update hosts success!";
        } else {
            msg.obj = "update hosts fail!";
        }
        handler.sendMessage(msg);
    }
}
