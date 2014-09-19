
package cn.ljj.threadpool;

public abstract class AbstractTask {
    public static final int STATUS_PENDING = 0;
    public static final int STATUS_RUNNING = 1;
    public static final int STATUS_FINISHED = 2;
    private int mStatus = STATUS_PENDING;
    private ITaskStatusChangeListner mStatusListner = null;

    public int getTaskStatus() {
        return mStatus;
    }

    /**
     * the thread pool updates the task status by calling this method, callback
     * to notify status changed
     */
    protected void setTaskStatus(int status) {
        mStatus = status;
        if (mStatusListner != null) {
            mStatusListner.onStatusChanged(this, status);
        }
    }

    protected void setStatusChangeListner(ITaskStatusChangeListner l) {
        mStatusListner = l;
    }

    /**
     * implements this method to do the task work.
     */
    public abstract void execute();

    protected interface ITaskStatusChangeListner {
        public void onStatusChanged(AbstractTask task, int status);
    }
}
