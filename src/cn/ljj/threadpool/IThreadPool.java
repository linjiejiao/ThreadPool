package cn.ljj.threadpool;

import java.util.List;

public interface IThreadPool {
    public int addToPool(AbstractTask task);
    public List<AbstractTask> getRunningTasks();
    public List<AbstractTask> getPendingTasks();
    public int getPoolSize();
    public void shutdown();
}
