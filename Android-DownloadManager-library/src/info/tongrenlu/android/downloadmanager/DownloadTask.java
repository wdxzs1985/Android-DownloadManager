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

public class DownloadTask extends AsyncTask<Object, Long, File> {

    private final String mFrom;
    private final File mTo;
    private long mRead = 0;
    private long mTotal = 0;
    private int mProgress = 0;
    private final Set<DownloadListener> listeners = Collections.synchronizedSet(new HashSet<DownloadListener>());

    public DownloadTask(String from, File to) {
        this.mFrom = from;
        this.mTo = to;
    }

    public void registerListener(DownloadListener listener) {
        this.listeners.add(listener);
    }

    public void unregisterListener(DownloadListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    protected File doInBackground(final Object... params) {
        boolean isAppend = this.mRead > 0;
        InputStream input = null;
        OutputStream output = null;
        try {
            final URL url = new URL(this.mFrom);
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
            if (isAppend) {
                input.skip(this.mRead);
            }
            output = FileUtils.openOutputStream(this.mTo, isAppend);

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
        return this.mTo;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.mRead = 0;
        this.mTotal = 0;
        this.mProgress = 0;
        for (DownloadListener listener : this.listeners) {
            listener.onDownloadStart(this);
        }
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
        this.mRead = values[0];
        this.mTotal = values[1];
        this.mProgress = (int) (this.mRead * 100 / this.mTotal);
        for (DownloadListener listener : this.listeners) {
            listener.onDownloadProgressUpdate(this);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        for (DownloadListener listener : this.listeners) {
            listener.onDownloadCancel(this);
        }
    }

    @Override
    protected void onPostExecute(File result) {
        super.onPostExecute(result);
        for (DownloadListener listener : this.listeners) {
            listener.onDownloadFinish(this);
        }
    }

    public long getRead() {
        return this.mRead;
    }

    public long getTotal() {
        return this.mTotal;
    }

    public int getProgress() {
        return this.mProgress;
    }

    public String getFrom() {
        return this.mFrom;
    }

    public File getTo() {
        return this.mTo;
    }

}
