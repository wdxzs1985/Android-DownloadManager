package info.tongrenlu.android.downloadmanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import android.os.AsyncTask.Status;

public class DownloadManagerImpl implements DownloadManager, DownloadListener {

    private final List<DownloadTask> mQueue = Collections.synchronizedList(new ArrayList<DownloadTask>());
    private DownloadTask running = null;

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
            this.running = this.mQueue.remove(0);
            this.running.registerListener(this);
            this.running.execute();
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
        this.running = null;
    }

    @Override
    public void onDownloadFinish(final DownloadTaskInfo taskinfo) {
        this.running = null;
        this.start();
    }

    @Override
    public void onDownloadProgressUpdate(final DownloadTaskInfo taskinfo) {
    }

    public boolean isIdle() {
        return this.running == null;
    }

    public DownloadTask getRunning() {
        return this.running;
    }

}
