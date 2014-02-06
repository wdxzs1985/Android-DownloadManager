package info.tongrenlu.android.downloadmanager;

import android.os.Parcel;
import android.os.Parcelable;

public class DownloadTaskInfo implements Parcelable {

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeStringArray(new String[] { this.mFrom,
                this.mTo,
                String.valueOf(this.mTotal),
                String.valueOf(this.mRead),
                String.valueOf(this.mProgress) });
    }

    public static final Parcelable.Creator<DownloadTaskInfo> CREATOR = new Parcelable.Creator<DownloadTaskInfo>() {
        @Override
        public DownloadTaskInfo createFromParcel(final Parcel in) {
            final String[] data = new String[4];
            in.readStringArray(data);

            final DownloadTaskInfo taskinfo = new DownloadTaskInfo();
            taskinfo.mFrom = data[0];
            taskinfo.mTo = data[1];
            taskinfo.mTotal = Long.valueOf(data[2]);
            taskinfo.mRead = Long.valueOf(data[3]);
            taskinfo.mProgress = Integer.valueOf(data[4]);
            return taskinfo;
        }

        @Override
        public DownloadTaskInfo[] newArray(final int size) {
            return new DownloadTaskInfo[size];
        }
    };
}
