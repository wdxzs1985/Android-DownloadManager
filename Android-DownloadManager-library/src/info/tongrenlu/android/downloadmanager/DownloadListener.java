package info.tongrenlu.android.downloadmanager;

public interface DownloadListener {

    void onDownloadStart(DownloadTaskInfo taskinfo);

    void onDownloadCancel(DownloadTaskInfo taskinfo);

    void onDownloadFinish(DownloadTaskInfo taskinfo);

    void onDownloadProgressUpdate(DownloadTaskInfo taskinfo);

}
