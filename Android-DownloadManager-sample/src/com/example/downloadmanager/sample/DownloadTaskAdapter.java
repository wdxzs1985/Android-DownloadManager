package com.example.downloadmanager.sample;

import java.io.File;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.downloadmanager.BaseDownloadTaskAdapter;
import com.example.downloadmanager.DownloadListener;
import com.example.downloadmanager.DownloadManager;
import com.example.downloadmanager.DownloadTask;

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
        holder.update(task);
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

        public void update(DownloadTask task) {
            this.icon.setImageDrawable(null);
            this.text1.setText(task.getFrom());
            switch (task.getStatus()) {
            case PENDING:
                this.text2.setText("PENDING");
                break;
            case RUNNING:
                if (task.isCancelled()) {
                    this.text2.setText("RUNNING && canceled");
                } else if (task.getProgress() < 100) {
                    this.text2.setText(String.format("%d%% (%d / %d)",
                                                     task.getProgress(),
                                                     task.getRead(),
                                                     task.getTotal()));
                } else if (task.getProgress() == 100) {
                    File to = task.getTo();
                    Drawable background = BitmapDrawable.createFromPath(to.getAbsolutePath());
                    this.icon.setImageDrawable(background);
                    this.text2.setText("");
                }
                break;
            case FINISHED:
                if (task.getProgress() == 100) {
                    File to = task.getTo();
                    Drawable background = BitmapDrawable.createFromPath(to.getAbsolutePath());
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
        public void onDownloadStart(DownloadTask task) {
            this.update(task);
        }

        @Override
        public void onDownloadCancel(DownloadTask task) {
            this.update(task);
        }

        @Override
        public void onDownloadFinish(DownloadTask task) {
            this.update(task);
        }

        @Override
        public void onDownloadProgressUpdate(DownloadTask task) {
            this.update(task);
        }
    }

}
