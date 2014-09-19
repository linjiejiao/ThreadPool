
package cn.ljj.threadpool;

import android.util.Log;

public class PoolThread extends Thread {
    private boolean isQuit = false;
    private AbstractTask mTask = null;
    private boolean isIdle = true;
    private String TAG = "PoolThread";

    public PoolThread(String name) {
        super(name);
    }

    @Override
    public void run() {
        Log.e(TAG, getName() + " is ready to accept task!");
        while (!isQuit) {
            try {
                synchronized (this) {
                    if(mTask == null){
                        wait();
                    }
                    if(isQuit){
                        Log.e(TAG, getName() + " is Quit!");
                        return;
                    }
                }
                isIdle = false;
                AbstractTask currentTask = mTask;
                mTask = null;
                if (currentTask != null) {
                    try {
                        currentTask.setTaskStatus(AbstractTask.STATUS_RUNNING);
                        currentTask.execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //Task finish, return to be idle
                        isIdle = true;
                        //this will eventually fill a new task to the pool if necessary
                        currentTask.setTaskStatus(AbstractTask.STATUS_FINISHED);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.e(TAG, getName() + " is Quit!");
        super.run();
    }

    public void quit() {
        isQuit = true;
        synchronized (this) {   //wake up thread to quit
            notify();
        }
    }

    public boolean isIdle() {
        return isIdle;
    }

    public void setTask(AbstractTask task) {
        mTask = task;
        synchronized (this) {   //wake up thread to execute new task
            notify();
        }
    }
}
