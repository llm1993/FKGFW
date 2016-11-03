package ami.nobody.fuckgfw;


import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShellUtils {

    private ShellUtils() {
    }

    public static boolean checkRootPermission() {
        List<String> listCmd = new ArrayList<String>();
        listCmd.add("echo root");
        return execCommand(listCmd) == 0;
    }

    public static int execCommand(List<String> commands) {
        int result = -1;
        if (commands == null || commands.size() == 0) {
            return result;
        }
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command == null) {
                    continue;
                }
                os.write(command.getBytes());
                os.writeBytes("\n");
                os.flush();
            }
            os.writeBytes("exit\n");
            os.flush();
            result = process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (process != null) {
                process.destroy();
            }
        }
        return result;
    }
}
