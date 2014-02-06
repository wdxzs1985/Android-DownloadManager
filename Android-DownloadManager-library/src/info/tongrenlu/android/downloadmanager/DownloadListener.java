package info.tongrenlu.android.downloadmanager;

public interface DownloadListener {

    void onDownloadStart(DownloadTask task);

    void onDownloadCancel(DownloadTask task);

    void onDownloadFinish(DownloadTask task);

    void onDownloadProgressUpdate(DownloadTask task);

}