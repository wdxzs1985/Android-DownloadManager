package info.tongrenlu.android.downloadmanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import android.os.AsyncTask.Status;

public class DownloadManagerImpl implements DownloadManager, DownloadListener {

    private final List<DownloadTask> mQueue = Collections.synchronizedList(new ArrayList<DownloadTask>());
    private volatile boolean idle = true;

    @Override
    public List<DownloadTask> getTasks() {
        return this.mQueue;
    }

    @Override
    public boolean addTask(final DownloadTask task) {
        final boolean added = this.mQueue.add(task);
        return added;
    }

    @Override
    public boolean cancelTask(final DownloadTask task) {
        boolean canceled = false;
        if (task == null) {
            return canceled;
        }
        canceled = task.cancel(true);
        return canceled;
    }

    @Override
    public boolean removeTask(final DownloadTask task) {
        boolean removed = false;
        if (task == null) {
            return removed;
        } else if (task.getStatus() == Status.RUNNING) {
            task.cancel(true);
        }
        removed = this.mQueue.remove(task);
        return removed;
    }

    @Override
    public void start() {
        if (this.isIdle() && CollectionUtils.isNotEmpty(this.mQueue)) {
            this.setIdle(false);
            final DownloadTask task = this.mQueue.remove(0);
            task.registerListener(this);
            task.execute();
        }
    }

    @Override
    public void shutdown() {
        while (CollectionUtils.isNotEmpty(this.mQueue)) {
            this.removeTask(this.mQueue.get(0));
        }
    }

    @Override
    public void onDownloadStart(final DownloadTaskInfo taskinfo) {
    }

    @Override
    public void onDownloadCancel(final DownloadTaskInfo taskinfo) {
        this.setIdle(true);
    }

    @Override
    public void onDownloadFinish(final DownloadTaskInfo taskinfo) {
        this.setIdle(true);
        this.start();
    }

    @Override
    public void onDownloadProgressUpdate(final DownloadTaskInfo taskinfo) {
    }

    public boolean isIdle() {
        return this.idle;
    }

    public void setIdle(boolean idle) {
        this.idle = idle;
    }

}
