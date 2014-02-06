package com.example.downloadmanager.sample;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.example.downloadmanager.DownloadManager;
import com.example.downloadmanager.DownloadManagerImpl;
import com.example.downloadmanager.DownloadTask;
import com.example.helper.HttpHelper;

public class MainActivity extends Activity implements OnClickListener, OnItemClickListener {

    private DownloadManager mDownloadManager = null;
    private DownloadTaskAdapter mAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        this.mDownloadManager = new DownloadManagerImpl(2);

        this.mAdapter = new DownloadTaskAdapter(this.mDownloadManager);
        ListView listview = (ListView) this.findViewById(android.R.id.list);
        listview.setAdapter(this.mAdapter);
        listview.setOnItemClickListener(this);

        Button button1 = (Button) this.findViewById(android.R.id.button1);
        button1.setOnClickListener(this);

        Button button2 = (Button) this.findViewById(android.R.id.button2);
        button2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case android.R.id.button1:
            this.mDownloadManager.start();
            break;
        case android.R.id.button2:
            new LoadPatternsListTask().execute();
            break;
        default:
            break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.mDownloadManager.shutdown();
    }

    private class LoadPatternsListTask extends AsyncTask<Void, Void, ArrayList<String>> {
        static final String URL = "http://www.tongrenlu.info/fm/music";

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            ArrayList<String> articleIds = new ArrayList<String>();

            try {
                JSONObject response = HttpHelper.loadJSON(URL);
                JSONObject page = response.getJSONObject("page");
                JSONArray items = page.getJSONArray("items");

                for (int i = 0; i < items.length(); i++) {
                    JSONObject patternInfo = (JSONObject) items.get(i);
                    String articleId = patternInfo.getString("articleId");
                    // String url =
                    // "http://www.tongrenlu.info/resource/%s/cover_%d.jpg";
                    articleIds.add(articleId);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return articleIds;
        }

        @Override
        protected void onPostExecute(ArrayList<String> articleIds) {
            File dir = MainActivity.this.getCacheDir();
            for (String articleId : articleIds) {
                String from = String.format("http://www.tongrenlu.info/resource/%s/cover_%d.jpg",
                                            articleId,
                                            400);
                File to = new File(dir, articleId + ".jpg");
                DownloadTask task = new DownloadTask(from, to);
                MainActivity.this.mDownloadManager.addTask(task);

            }

            MainActivity.this.mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> listview, View arg1, int position, long arg3) {
        DownloadTask task = (DownloadTask) listview.getItemAtPosition(position);
        if (task.isCancelled()) {
            this.mDownloadManager.removeTask(task);
        } else {
            this.mDownloadManager.cancelTask(task);
        }
    }
}
