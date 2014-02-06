package com.example.downloadmanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.collections.CollectionUtils;

import android.os.AsyncTask.Status;

public class DownloadManagerImpl implements DownloadManager {

    private final List<DownloadTask> mQueue = Collections.synchronizedList(new ArrayList<DownloadTask>());
    private final ExecutorService exec;

    public DownloadManagerImpl(int maxThreads) {
        this.exec = Executors.newFixedThreadPool(maxThreads);
    }

    @Override
    public List<DownloadTask> getTasks() {
        return this.mQueue;
    }

    @Override
    public boolean addTask(DownloadTask task) {
        boolean added = this.mQueue.add(task);
        return added;
    }

    @Override
    public boolean cancelTask(DownloadTask task) {
        boolean canceled = false;
        if (task == null) {
            return canceled;
        }
        canceled = task.cancel(true);
        return canceled;
    }

    @Override
    public boolean removeTask(DownloadTask task) {
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
        for (DownloadTask task : this.mQueue) {
            if (task.getStatus() == Status.PENDING) {
                task.executeOnExecutor(this.exec);
            }
        }
    }

    @Override
    public void shutdown() {
        if (CollectionUtils.isNotEmpty(this.mQueue)) {
            this.removeTask(this.mQueue.get(0));
        }
    }

}
