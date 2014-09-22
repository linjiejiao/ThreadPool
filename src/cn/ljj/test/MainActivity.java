
package cn.ljj.test;

import cn.ljj.threadpool.AbstractTask;
import cn.ljj.threadpool.R;
import cn.ljj.threadpool.ThreadPool;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

    TextView text;
    private String TAG = "MainActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
        findViewById(R.id.btn3).setOnClickListener(this);
        text = (TextView) findViewById(R.id.text);
    }
    ThreadPool pool = new ThreadPool(4);
    int count = 0;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                pool.addToPool(new AbstractTask() {
                    int id=count ++;
                    @Override
                    public void execute() {
                        log("Task " + id + " start");
                        try {
                            Thread.sleep(2000);
                            log("Task " + id + " running");
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        log("Task " + id + " end");
                    }
                });
                break;
            case R.id.btn2:
                pool.shutdown();
                pool = new ThreadPool(4);
                count = 0;
                break;
            case R.id.btn3:
                log("running Task count=" + pool.getRunningTasks().size());
                log("pending Task count=" + pool.getPendingTasks().size());
                break;
        }
    }

    void log(final String log) {
        Log.e(TAG, log);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(log + "\n" + text.getText());
            }
        });
    }

}
