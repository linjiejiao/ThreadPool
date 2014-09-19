
package cn.ljj.threadpool;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import cn.ljj.threadpool.AbstractTask.ITaskStatusChangeListner;

public class ThreadPool implements IThreadPool, ITaskStatusChangeListner {
    private List<AbstractTask> mTaskPending;
    private List<AbstractTask> mTaskRunning;
    private PoolThread[] mPoolThreads = null;
    private int mPoolSize = 1;
    private String TAG = "ThreadPool";

    public ThreadPool(int size) {
        mPoolSize = size;
        mTaskPending = new ArrayList<AbstractTask>();
        mTaskRunning = new ArrayList<AbstractTask>();
        mPoolThreads = new PoolThread[size];
    }

    /**
     * Add a task to the pool, if there is a idle thread, execute it
     * immediately; otherwise, the task will execute until there is a idle
     * thread.
     */
    @Override
    public int addToPool(AbstractTask task) {
        mTaskPending.add(task);
        task.setStatusChangeListner(this);
        fillPool();
        return mTaskPending.size();
    }

    @Override
    public List<AbstractTask> getRunningTasks() {
        return new ArrayList<AbstractTask>(mTaskRunning);
    }

    @Override
    public List<AbstractTask> getPendingTasks() {
        return new ArrayList<AbstractTask>(mTaskPending);
    }

    @Override
    public int getPoolSize() {
        return mPoolSize;
    }

    /**
     * Just to remove all pending tasks, running tasks will not stop until
     * finished.
     */
    @Override
    public void shutdown() {
        mTaskPending.clear();
        for (int i = 0; i < mPoolSize; i++) {
            PoolThread thread = mPoolThreads[i];
            if (thread != null) {
                thread.quit();
            }
        }
    }

    /**
     * Called when any task status changed.
     */
    @Override
    public void onStatusChanged(AbstractTask task, int status) {
        switch (status) {
            case AbstractTask.STATUS_PENDING:
                break;
            case AbstractTask.STATUS_RUNNING:
                break;
            case AbstractTask.STATUS_FINISHED:
                mTaskRunning.remove(task);
                fillPool();
                break;
            default:
        }
    }

    /**
     * Fill the running task list from pending list
     */
    synchronized private void fillPool() {
        if (mTaskRunning.size() < mPoolSize && mTaskPending.size() > 0) {
            PoolThread thread = getAvailableThread();
            if (thread != null) {
                AbstractTask task = mTaskPending.remove(0);
                if (task != null) {
                    thread.setTask(task);
                    mTaskRunning.add(task);
                } else {
                    Log.e(TAG, "fillPool: read a null task from mTaskRunning");
                }
            } else {
                Log.e(TAG, "fillPool: No available Thread!!");
            }
        } else if (mTaskRunning.size() >= mPoolSize) {
            Log.e(TAG, "fillPool: no idle thread, keep task in pending!");
        }
    }

    /**
     * Get idle thread from the Thread pool if there is one.
     */
    private PoolThread getAvailableThread() {
        for (int i = 0; i < mPoolSize; i++) {
            PoolThread thread = mPoolThreads[i];
            String str = "getAvailuableThread thread[" + i + "]=" + thread;
            if (thread != null) {
                str += ", idel=" + mPoolThreads[i].isIdle();
            }
            Log.e(TAG, str);
        }
        for (int i = 0; i < mPoolSize; i++) {
            PoolThread thread = mPoolThreads[i];
            if (thread == null) {
                mPoolThreads[i] = new PoolThread("PoolThread " + i);
                thread = mPoolThreads[i];
                thread.start();
            }
            if (thread.isIdle()) {
                Log.e(TAG, "getAvailuableThread return thread[" + i + "] =" + thread);
                return thread;
            }
        }
        Log.e(TAG, "getAvailuableThread return null");
        return null;
    }
}
