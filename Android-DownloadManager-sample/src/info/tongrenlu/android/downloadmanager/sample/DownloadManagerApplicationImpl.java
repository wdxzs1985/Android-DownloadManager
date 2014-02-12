package info.tongrenlu.android.downloadmanager.sample;

import info.tongrenlu.android.downloadmanager.DownloadManager;
import info.tongrenlu.android.downloadmanager.DownloadManagerImpl;
import android.app.Application;

public class DownloadManagerApplicationImpl extends Application {

    private DownloadManager mDownloadManager = null;

    @Override
    public void onCreate() {
        super.onCreate();
        this.mDownloadManager = new DownloadManagerImpl(1);
    }

    public DownloadManager getDownloadManager() {
        return this.mDownloadManager;
    }

}
