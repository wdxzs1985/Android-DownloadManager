package info.tongrenlu.android.downloadmanager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import android.os.AsyncTask;

public class DownloadTask extends AsyncTask<Object, Long, DownloadTaskInfo> {

    public static final int KB = 1024;

    private final Set<DownloadListener> listeners = Collections.synchronizedSet(new HashSet<DownloadListener>());
    private DownloadTaskInfo mTaskinfo = null;

    private int mBufferLength = KB * 8;

    public DownloadTask(final String from, final String to) {
        this.mTaskinfo = new DownloadTaskInfo();
        this.mTaskinfo.setFrom(from);
        this.mTaskinfo.setTo(to);
    }

    public DownloadTask(final DownloadTaskInfo taskinfo) {
        this.mTaskinfo = taskinfo;
    }

    public void registerListener(final DownloadListener listener) {
        this.listeners.add(listener);
    }

    public void unregisterListener(final DownloadListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    protected DownloadTaskInfo doInBackground(final Object... params) {
        InputStream input = null;
        OutputStream output = null;
        final String form = this.mTaskinfo.getFrom();
        final String to = this.mTaskinfo.getTo();
        final boolean isAppend = false;
        try {
            final URL url = new URL(form);
            final URLConnection connection = url.openConnection();
            connection.setConnectTimeout(60 * 1000);
            connection.setReadTimeout(60 * 1000);
            // connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            long loaded = 0;
            final long contentLength = connection.getContentLength();
            // download the file
            input = new BufferedInputStream(url.openStream());
            output = FileUtils.openOutputStream(new File(to), isAppend);

            final byte data[] = new byte[this.mBufferLength];
            int count;
            while ((count = input.read(data)) != -1) {
                loaded += count;
                // publishing the progress....
                this.publishProgress(loaded, contentLength);
                output.write(data, 0, count);
                output.flush();
                if (this.isCancelled()) {
                    break;
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();
            this.cancel(true);
        } finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(output);
        }
        return this.mTaskinfo;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.mTaskinfo.setRead(0l);
        this.mTaskinfo.setTotal(0l);
        this.mTaskinfo.setProgress(0);
        for (final DownloadListener listener : this.listeners) {
            listener.onDownloadStart(this.mTaskinfo);
        }
    }

    @Override
    protected void onProgressUpdate(final Long... values) {
        super.onProgressUpdate(values);
        final long read = values[0];
        final long total = values[1];
        final int progress = (int) (read * 100 / total);

        this.mTaskinfo.setRead(read);
        this.mTaskinfo.setTotal(total);
        this.mTaskinfo.setProgress(progress);

        for (final DownloadListener listener : this.listeners) {
            listener.onDownloadProgressUpdate(this.mTaskinfo);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        for (final DownloadListener listener : this.listeners) {
            listener.onDownloadCancel(this.mTaskinfo);
        }
    }

    @Override
    protected void onPostExecute(final DownloadTaskInfo result) {
        super.onPostExecute(result);
        for (final DownloadListener listener : this.listeners) {
            listener.onDownloadFinish(this.mTaskinfo);
        }
        this.listeners.clear();
    }

    public DownloadTaskInfo getTaskinfo() {
        return this.mTaskinfo;
    }

    public int getmBufferLength() {
        return this.mBufferLength;
    }

    public void setmBufferLength(int mBufferLength) {
        this.mBufferLength = mBufferLength;
    }
}
