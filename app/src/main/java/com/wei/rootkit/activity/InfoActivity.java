package com.wei.rootkit.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.wei.rootkit.R;
import com.wei.rootkit.model.Icon;
import com.wei.rootkit.model.Item;
import com.wei.rootkit.util.RootKitUtil;

/**
 * Created by weiyilin on 17/1/12.
 */

public class InfoActivity extends AppCompatActivity{

    private Item item;

    private TextView appName;
    private TextView packageName;
    private TextView uid;
    private TextView versionName;
    private TextView versionId;
    private TextView installTime;
    private TextView updateTime;
    private TextView itemName;
    private ImageView itemIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_activity);
        initView();
    }

    private void initView() {
        item = (Item) getIntent().getExtras().getSerializable("item");

        appName = (TextView) findViewById(R.id.app_name);
        packageName = (TextView) findViewById(R.id.package_name);
        uid = (TextView) findViewById(R.id.uid);
        versionName = (TextView) findViewById(R.id.version_name);
        versionId = (TextView) findViewById(R.id.version_id);
        installTime = (TextView) findViewById(R.id.install_time);
        updateTime = (TextView) findViewById(R.id.update_time);
        itemIcon = (ImageView) findViewById(R.id.item_icon);
        itemName = (TextView) findViewById(R.id.item_name);

        appName.setText(item.getAppName());
        packageName.setText(item.getPackageName());
        uid.setText(item.getId());
        versionName.setText(item.getVersionName());
        versionId.setText(item.getVersionId());
        installTime.setText(RootKitUtil.stampToDate(item.getFirstInstallTime()));
        updateTime.setText(RootKitUtil.stampToDate(item.getLastUpdateTime()));
        itemName.setText(item.getAppName());

        for (int i = 0; i < MainActivity.icons.size(); i ++){
            Icon icon = MainActivity.icons.get(i);
            if (item.getAppName().equals(icon.getAppName()) && item.getId().equals(icon.getId())){
                itemIcon.setImageBitmap(RootKitUtil.drawableToBitmap(icon.getIcon()));
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}
