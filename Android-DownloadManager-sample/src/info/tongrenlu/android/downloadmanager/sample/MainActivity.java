package info.tongrenlu.android.downloadmanager.sample;

import info.tongrenlu.android.helper.HttpHelper;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener, OnItemClickListener {

    private LocalBroadcastManager mLocalBroadcastManager = null;
    private BroadcastReceiver mAdapterReceiver = null;
    private DownloadTaskAdapter mAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        this.mAdapter = new DownloadTaskAdapter(DownloadService.DOWNLOAD_MANAGER);
        ListView listview = (ListView) this.findViewById(android.R.id.list);
        listview.setAdapter(this.mAdapter);
        listview.setOnItemClickListener(this);

        Button button1 = (Button) this.findViewById(android.R.id.button1);
        button1.setOnClickListener(this);

        Button button2 = (Button) this.findViewById(android.R.id.button2);
        button2.setOnClickListener(this);

        this.mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        this.mAdapterReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                final String action = intent.getAction();
                if (DownloadService.EVENT_UPDATE.equals(action)) {
                    MainActivity.this.mAdapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this,
                                   "EVENT_UPDATE",
                                   Toast.LENGTH_SHORT).show();
                }
            }
        };
        final IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadService.EVENT_UPDATE);
        this.mLocalBroadcastManager.registerReceiver(this.mAdapterReceiver,
                                                     filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.mLocalBroadcastManager.unregisterReceiver(this.mAdapterReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case android.R.id.button1:
            Context context = MainActivity.this.getApplicationContext();
            Intent intent = new Intent(context, DownloadService.class);
            intent.setAction(DownloadService.ACTION_START);
            context.startService(intent);
            break;
        case android.R.id.button2:
            new LoadPatternsListTask().execute();
            break;
        default:
            break;
        }
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
            Context context = MainActivity.this.getApplicationContext();
            Intent intent = new Intent(context, DownloadService.class);
            intent.setAction(DownloadService.ACTION_ADD);
            intent.putExtra("articleIds", articleIds);
            context.startService(intent);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> listview, View arg1, int position, long arg3) {
        Context context = MainActivity.this.getApplicationContext();
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(DownloadService.ACTION_REMOVE);
        intent.putExtra("position", position);
        context.startService(intent);
    }
}
