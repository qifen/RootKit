package com.wei.rootkit.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.ListView;

import com.wei.rootkit.R;
import com.wei.rootkit.adapter.ListAdapter;

/**
 * Created by weiyilin on 16/12/27.
 */

public class ListActivity extends AppCompatActivity {
    private ListView listView;
    private ListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);
        listView = (ListView) findViewById(R.id.list_view);
        adapter = new ListAdapter(this);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
