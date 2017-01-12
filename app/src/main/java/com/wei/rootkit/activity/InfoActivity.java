package com.wei.rootkit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wei.rootkit.R;
import com.wei.rootkit.model.Item;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by weiyilin on 17/1/12.
 */

public class InfoActivity extends AppCompatActivity implements View.OnClickListener{

    private MaterialDialog mMaterialDialog;
    private Button detect;
    private Item item;
    private TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_activity);
        initView();
    }

    private void initView() {
        item = (Item) getIntent().getExtras().getSerializable("item");

        name = (TextView) findViewById(R.id.app_name);
        name.setText(item.getName());
        detect = (Button) findViewById(R.id.item_detect);
        detect.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.item_detect && item != null){
            mMaterialDialog = new MaterialDialog(this)
                    .setTitle("开始检测 " + item.getName())
                    .setMessage("请打开" + item.getName() + ", 执行各项需检测的操作, 操作完毕后可" +
                            "点击分析按钮。如需取消检测, 点击取消按钮")
                    .setPositiveButton("分析", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(InfoActivity.this, DetailActivity.class);
                            startActivity(intent);
                            mMaterialDialog.dismiss();
                        }
                    })
                    .setNegativeButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMaterialDialog.dismiss();
                        }
                    });

            mMaterialDialog.show();
        }
    }
}
