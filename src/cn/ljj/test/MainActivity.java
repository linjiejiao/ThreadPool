
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
                        Log.e(TAG, "Task " + id + " start");
                        try {
                            Thread.sleep(3000);
                            Log.e(TAG, "Task " + id + " running");
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.e(TAG, "Task " + id + " end");
                    }
                });
                break;
            case R.id.btn2:
                pool.shutdown();
                break;
            case R.id.btn3:
                Log.e(TAG, "running Task count=" + pool.getRunningTasks().size());
                Log.e(TAG, "pending Task count=" + pool.getPendingTasks().size());
                break;
        }
    }

    void log(String log) {
        Log.e(TAG, log);
        text.setText(log + "\n" + text.getText());
    }

}
