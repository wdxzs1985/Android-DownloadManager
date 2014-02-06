package info.tongrenlu.android.downloadmanager;

import java.util.List;

public interface DownloadManager {

    public List<DownloadTask> getTasks();

    public boolean addTask(DownloadTask task);

    public boolean cancelTask(DownloadTask task);

    public boolean removeTask(DownloadTask task);

    public void start();

    public void shutdown();
}
