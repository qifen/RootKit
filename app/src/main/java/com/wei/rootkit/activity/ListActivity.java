package com.wei.rootkit.activity;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;

import com.wei.rootkit.R;
import com.wei.rootkit.adapter.ListAdapter;
import com.wei.rootkit.model.Item;

import java.util.ArrayList;
import java.util.List;

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
        adapter = new ListAdapter(this, getAppInfo());
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private List<Item> getAppInfo() {
        List<Item> items = new ArrayList<>();
        //获取所有应用信息
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++){
            PackageInfo packageInfo = packages.get(i);
            Item item = new Item();
            item.setId(getUidByPackageName(packageInfo.packageName));
            item.setAppName(packageInfo.applicationInfo.loadLabel(getPackageManager()).toString());
            item.setPackageName(packageInfo.packageName);
//            item.setIcon(packageInfo.applicationInfo.loadIcon(getPackageManager()));
            item.setVersionName(packageInfo.versionName);
            item.setVersionId(packageInfo.versionCode + "");
            item.setFirstInstallTime(packageInfo.firstInstallTime + "");
            item.setLastUpdateTime(packageInfo.lastUpdateTime + "");
            items.add(item);
        }
        return items;
    }

    private String getUidByPackageName(String name) {
        String uid = null;
        try {
            PackageManager pm = getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(name, PackageManager.GET_ACTIVITIES);
            uid = ai.uid + "";
        } catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
        return uid;
    }
}
