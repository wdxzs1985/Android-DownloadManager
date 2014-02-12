package info.tongrenlu.android.downloadmanager;


public class DownloadTaskInfo {

    private String mFrom;
    private String mTo;
    private long mRead = 0;
    private long mTotal = 0;
    private int mProgress = 0;

    public String getFrom() {
        return this.mFrom;
    }

    public void setFrom(String from) {
        this.mFrom = from;
    }

    public String getTo() {
        return this.mTo;
    }

    public void setTo(String to) {
        this.mTo = to;
    }

    public long getRead() {
        return this.mRead;
    }

    public void setRead(long read) {
        this.mRead = read;
    }

    public long getTotal() {
        return this.mTotal;
    }

    public void setTotal(long total) {
        this.mTotal = total;
    }

    public int getProgress() {
        return this.mProgress;
    }

    public void setProgress(int progress) {
        this.mProgress = progress;
    }

}
