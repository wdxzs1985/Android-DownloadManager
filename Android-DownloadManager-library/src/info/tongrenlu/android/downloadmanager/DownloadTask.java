package info.tongrenlu.android.downloadmanager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import android.os.AsyncTask;

public class DownloadTask extends AsyncTask<Object, Long, DownloadTaskInfo> {

    private final Set<DownloadListener> listeners = Collections.synchronizedSet(new HashSet<DownloadListener>());

    private final DownloadTaskInfo taskinfo = new DownloadTaskInfo();

    public DownloadTask(String from, String to) {
        this.taskinfo.setFrom(from);
        this.taskinfo.setTo(to);
    }

    public void registerListener(DownloadListener listener) {
        this.listeners.add(listener);
    }

    public void unregisterListener(DownloadListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    protected DownloadTaskInfo doInBackground(final Object... params) {
        InputStream input = null;
        OutputStream output = null;
        String form = this.taskinfo.getFrom();
        String to = this.taskinfo.getTo();
        boolean isAppend = false;
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

            final byte data[] = new byte[1024];
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
        } catch (final MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (final IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(output);
        }
        return this.taskinfo;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.taskinfo.setRead(0l);
        this.taskinfo.setTotal(0l);
        this.taskinfo.setProgress(0);
        for (DownloadListener listener : this.listeners) {
            listener.onDownloadStart(this.taskinfo);
        }
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
        long read = values[0];
        long total = values[1];
        int progress = (int) (read * 100 / total);

        this.taskinfo.setRead(read);
        this.taskinfo.setTotal(total);
        this.taskinfo.setProgress(progress);

        for (DownloadListener listener : this.listeners) {
            listener.onDownloadProgressUpdate(this.taskinfo);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        for (DownloadListener listener : this.listeners) {
            listener.onDownloadCancel(this.taskinfo);
        }
    }

    @Override
    protected void onPostExecute(DownloadTaskInfo result) {
        super.onPostExecute(result);
        for (DownloadListener listener : this.listeners) {
            listener.onDownloadFinish(this.taskinfo);
        }
    }

    public DownloadTaskInfo getTaskinfo() {
        return this.taskinfo;
    }
}
