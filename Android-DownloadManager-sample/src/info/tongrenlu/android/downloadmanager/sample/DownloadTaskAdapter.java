package info.tongrenlu.android.downloadmanager.sample;

import info.tongrenlu.android.downloadmanager.BaseDownloadTaskAdapter;
import info.tongrenlu.android.downloadmanager.DownloadListener;
import info.tongrenlu.android.downloadmanager.DownloadManager;
import info.tongrenlu.android.downloadmanager.DownloadTask;
import info.tongrenlu.android.downloadmanager.DownloadTaskInfo;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class DownloadTaskAdapter extends BaseDownloadTaskAdapter {

    public DownloadTaskAdapter(DownloadManager downloadManager) {
        super(downloadManager);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;
        final Context context = parent.getContext();
        if (view == null) {
            view = View.inflate(context, R.layout.list_item, null);
            holder = new ViewHolder();
            holder.parent = view;
            holder.icon = (ImageView) view.findViewById(android.R.id.icon);
            holder.text1 = (TextView) view.findViewById(android.R.id.text1);
            holder.text2 = (TextView) view.findViewById(android.R.id.text2);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        DownloadTask task = this.getItem(position);
        holder.bind(task);
        holder.update(task.getTaskinfo());
        return view;
    }

    public class ViewHolder implements DownloadListener {
        public View parent;
        public ImageView icon;
        public TextView text1;
        public TextView text2;
        public DownloadTask task;

        public void bind(DownloadTask task) {
            if (this.task != null) {
                task.unregisterListener(this);
            }
            this.task = task;
            task.registerListener(this);
        }

        public void update(DownloadTaskInfo taskinfo) {
            this.icon.setImageDrawable(null);
            this.text1.setText(taskinfo.getFrom());
            switch (this.task.getStatus()) {
            case PENDING:
                this.text2.setText("PENDING");
                break;
            case RUNNING:
                if (this.task.isCancelled()) {
                    this.text2.setText("RUNNING && canceled");
                } else if (taskinfo.getProgress() < 100) {
                    this.text2.setText(String.format("%d%% (%d / %d)",
                                                     taskinfo.getProgress(),
                                                     taskinfo.getRead(),
                                                     taskinfo.getTotal()));
                } else if (taskinfo.getProgress() == 100) {
                    String path = taskinfo.getTo();
                    Drawable background = BitmapDrawable.createFromPath(path);
                    this.icon.setImageDrawable(background);
                    this.text2.setText("");
                }
                break;
            case FINISHED:
                if (taskinfo.getProgress() == 100) {
                    String path = taskinfo.getTo();
                    Drawable background = BitmapDrawable.createFromPath(path);
                    this.icon.setImageDrawable(background);
                    this.text2.setText("");
                } else {
                    this.text2.setText("FINISHED && canceled");
                }
                break;
            default:
                break;
            }
        }

        @Override
        public void onDownloadStart(DownloadTaskInfo taskinfo) {
            this.update(taskinfo);
        }

        @Override
        public void onDownloadCancel(DownloadTaskInfo taskinfo) {
            this.update(taskinfo);
        }

        @Override
        public void onDownloadFinish(DownloadTaskInfo taskinfo) {
            this.update(taskinfo);
        }

        @Override
        public void onDownloadProgressUpdate(DownloadTaskInfo taskinfo) {
            this.update(taskinfo);
        }
    }

}
