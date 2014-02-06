package info.tongrenlu.android.downloadmanager.sample;

import info.tongrenlu.android.downloadmanager.DownloadManager;
import info.tongrenlu.android.downloadmanager.DownloadManagerImpl;
import info.tongrenlu.android.downloadmanager.DownloadTask;

import java.io.File;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

public class DownloadService extends Service {

    public static final DownloadManager DOWNLOAD_MANAGER = new DownloadManagerImpl(2);
    public static final String ACTION_ADD = "info.tongrenlu.android.downloadmanager.sample.DownloadService.action.add";
    public static final String ACTION_REMOVE = "info.tongrenlu.android.downloadmanager.sample.DownloadService.action.remove";
    public static final String ACTION_START = "info.tongrenlu.android.downloadmanager.sample.DownloadService.action.start";
    public static final String ACTION_STOP = "info.tongrenlu.android.downloadmanager.sample.DownloadService.action.stop";

    public static final String EVENT_UPDATE = "info.tongrenlu.android.downloadmanager.sample.DownloadService.event.update";

    private LocalBroadcastManager mLocalBroadcastManager;

    @Override
    public void onCreate() {
        this.mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (ACTION_ADD.equals(action)) {
            this.processAddRequest(intent);
        } else if (ACTION_REMOVE.equals(action)) {
            this.processRemoveRequest(intent);
        } else if (ACTION_START.equals(action)) {
            DOWNLOAD_MANAGER.start();
        } else if (ACTION_STOP.equals(action)) {
            this.stopSelf();
        }
        return START_NOT_STICKY;
    }

    private void processAddRequest(Intent intent) {
        File dir = this.getCacheDir();
        List<String> articleIds = intent.getStringArrayListExtra("articleIds");
        for (String articleId : articleIds) {
            String from = String.format("http://www.tongrenlu.info/resource/%s/cover_%d.jpg",
                                        articleId,
                                        400);
            String to = new File(dir, articleId + ".jpg").getAbsolutePath();
            DownloadTask task = new DownloadTask(from, to);
            DOWNLOAD_MANAGER.addTask(task);
        }
        this.performEventUpdate();
    }

    private void processRemoveRequest(Intent intent) {
        int position = intent.getIntExtra("position", -1);
        if (position >= 0) {
            DownloadTask task = DOWNLOAD_MANAGER.getTasks().get(position);
            DOWNLOAD_MANAGER.removeTask(task);
        }
        this.performEventUpdate();
    }

    private void performEventUpdate() {
        Intent intent = new Intent(EVENT_UPDATE);
        this.mLocalBroadcastManager.sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("shutdown");
        DownloadService.DOWNLOAD_MANAGER.shutdown();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
