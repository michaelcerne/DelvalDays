package com.michaelcerne.delvaldays;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.firebase.messaging.FirebaseMessaging;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private JSONArray mData;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout refreshView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().subscribeToTopic("all");
        FloatingActionButton fab = findViewById(R.id.fab);
        refreshView = findViewById(R.id.refreshView);

        mRecyclerView = findViewById(R.id.my_recycler_view);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        fab.setOnClickListener(view -> updateRecycler());
        refreshView.setOnRefreshListener(() -> updateRecycler());

        updateRecycler();

    }

    private void updateRecycler() {
        runOnUiThread(() -> {
            mData = new JSONArray();
            mAdapter = new MyAdapter(mData);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        });
        findViewById(R.id.errorPanel).setVisibility(View.GONE);
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://www.michaelcerne.com/dc/dvd/api.php?days=30")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showError(e.toString());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "Error: " + response.message(),
                                Toast.LENGTH_LONG).show();
                        findViewById(R.id.errorPanel).setVisibility(View.VISIBLE);
                        refreshView.setRefreshing(false);
                    });
                    throw new IOException("Unexpected code " + response);
                } else {
                    String res = response.body().string();
                    try {
                        JSONArray json = new JSONArray(res);
                        mData = json;
                        runOnUiThread(() -> {
                            mAdapter = new MyAdapter(mData);
                            mRecyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                            findViewById(R.id.errorPanel).setVisibility(View.GONE);
                            refreshView.setRefreshing(false);
                        });
                    } catch (JSONException e) {
                        showError(e.toString());
                        Log.e("JSON", "Couldn't convert string to JSON");
                    }
                }
            }
        });
    }

    private void showError(String e) {
        runOnUiThread(() -> {
            Toast.makeText(MainActivity.this, "Error: " + e,
                    Toast.LENGTH_LONG).show();
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            findViewById(R.id.errorPanel).setVisibility(View.VISIBLE);
            refreshView.setRefreshing(false);
        });
    }
}
