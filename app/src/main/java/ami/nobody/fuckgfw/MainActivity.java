package ami.nobody.fuckgfw;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private EditText editText;
    private RadioButton radioButton1;
    private RadioButton radioButton2;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.editText);
        radioButton1 = (RadioButton) findViewById(R.id.radioButton1);
        radioButton2 = (RadioButton) findViewById(R.id.radioButton2);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable()) {
                    String url = String.valueOf(editText.getText());
                    if (!url.trim().equals("") && !url.startsWith("http")) {
                        Toast.makeText(getApplicationContext(), "地址有误！", Toast.LENGTH_SHORT).show();
                    }
                    boolean radio1IsChecked = false;
                    boolean radio2IsChecked = false;
                    if(radioButton1.isChecked()){
                        radio1IsChecked = true;
                    }
                    if(radioButton2.isChecked()){
                        radio2IsChecked = true;
                    }
                    HttpThread httpThread = new HttpThread(handler, url,radio1IsChecked,radio2IsChecked);
                    httpThread.start();
                } else {
                    Message message = new Message();
                    message.obj = "网络不可用！！！";
                    handler.sendMessage(message);
                }
            }
        });
    }

    /**
     * 检测网络是否连接
     *
     * @return
     */
    private boolean isNetworkAvailable() {
        // 得到网络连接信息
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // 去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
            return manager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }
}
