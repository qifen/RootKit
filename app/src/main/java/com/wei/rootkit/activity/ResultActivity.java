package com.wei.rootkit.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.ListView;

import com.wei.rootkit.R;
import com.wei.rootkit.adapter.ResultAdapter;
import com.wei.rootkit.model.Item;

import java.util.List;

/**
 * Created by weiyilin on 17/3/6.
 */

public class ResultActivity extends AppCompatActivity {

    private ListView listView;
    private ResultAdapter adapter;
    private List<Item> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_activity);
        initView();
    }

    private void initView(){
        listView = (ListView) findViewById(R.id.list_view);
        results = (List<Item>) getIntent().getSerializableExtra("result");
        adapter = new ResultAdapter(this, results);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
