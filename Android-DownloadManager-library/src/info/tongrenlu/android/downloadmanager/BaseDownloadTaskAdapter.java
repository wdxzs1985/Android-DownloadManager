package info.tongrenlu.android.downloadmanager;

import android.widget.BaseAdapter;

public abstract class BaseDownloadTaskAdapter extends BaseAdapter {

    private final DownloadManager mDownloadManager;

    public BaseDownloadTaskAdapter(DownloadManager downloadManager) {
        this.mDownloadManager = downloadManager;
    }

    @Override
    public int getCount() {
        return this.mDownloadManager.getTasks().size();
    }

    @Override
    public DownloadTask getItem(int position) {
        return this.mDownloadManager.getTasks().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
