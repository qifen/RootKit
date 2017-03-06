package com.wei.rootkit.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wei.rootkit.R;
import com.wei.rootkit.adapter.ListAdapter;
import com.wei.rootkit.model.Icon;
import com.wei.rootkit.model.Item;
import com.wei.rootkit.service.InfoService;
import com.wei.rootkit.service.MainService;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

public class MainActivity extends AppCompatActivity{

    private Toolbar toolbar;
    private ListView listView;
    private ListAdapter adapter;

    private List<Item> items;
    public static List<Icon> icons;

    private List<Item> result;      //用于存放选中的item
    private MaterialDialog mMaterialDialog;
    private MainService mainService = MainService.getInstance();
    private InfoService infoService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        //拷贝asset文件
        mainService.copyFiles(this.getApplicationContext());
    }

    private void initView() {
        result = new ArrayList<>();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.list_view);
        //添加检测按钮
        View footView = getLayoutInflater().inflate(R.layout.list_detect, null);
        footView.findViewById(R.id.item_detect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (result.size() != 0){

                    //将选中app的UID添加到uid_file
                    String inputUid="";
                    String uid=result.get(0).getId().trim();
                    inputUid=uid;
                    for(int i=1;i<result.size();i++){
                        uid=result.get(i).getId().trim();
                        inputUid=inputUid+";"+uid;
                    }
                    setUidFile(inputUid);

                    //加载内核模块rootkit.ko
                    infoService=InfoService.getInstance();
                    infoService.startDetect();

                    mMaterialDialog = new MaterialDialog(MainActivity.this)
                            .setTitle("开始检测选中的" + result.size() + "项应用")
                            .setMessage("请打开所选应用, 执行各项需检测的操作, 操作完毕后可" +
                                    "点击分析按钮。如需取消检测, 点击取消按钮")
                            .setPositiveButton("分析", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                                    intent.putExtra("result", (Serializable) result);
                                    startActivity(intent);
                                    mMaterialDialog.dismiss();
                                }
                            })
                            .setNegativeButton("取消", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    infoService.cancelDetect();
                                    mMaterialDialog.dismiss();
                                }
                            });

                    mMaterialDialog.show();
                }
            }
        });
        listView.addFooterView(footView);

        getAppInfo();
        adapter = new ListAdapter(this, items, icons);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListAdapter.ItemView itemView = (ListAdapter.ItemView) view.getTag();
                if (null != itemView){
                    itemView.checkBox.toggle();     //改变checkbox状态
                    ListAdapter.isSelected.put(position, itemView.checkBox.isChecked());    //修改map的值保存状态

                    if (itemView.checkBox.isChecked()){
                        result.add(items.get(position));
                    }else {
                        result.remove(items.get(position));
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if (id == R.id.action_about) {
            return true;
        }else if (id == R.id.action_exit){
            mainService.exit();
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void getAppInfo() {
        items = new ArrayList<>();
        icons = new ArrayList<>();
        //获取所有应用信息
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++){
            PackageInfo packageInfo = packages.get(i);
            //过滤系统应用
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                Item item = new Item();
                Icon icon = new Icon();
                item.setId(getUidByPackageName(packageInfo.packageName));
                item.setAppName(packageInfo.applicationInfo.loadLabel(getPackageManager()).toString());
                item.setPackageName(packageInfo.packageName);
                item.setVersionName(packageInfo.versionName);
                item.setVersionId(packageInfo.versionCode + "");
                item.setFirstInstallTime(packageInfo.firstInstallTime + "");
                item.setLastUpdateTime(packageInfo.lastUpdateTime + "");

                icon.setId(item.getId());
                icon.setAppName(item.getAppName());
                icon.setIcon(packageInfo.applicationInfo.loadIcon(getPackageManager()));
                items.add(item);
                icons.add(icon);
            }
        }
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

    /*
    修改uid_file为选中app的uid
     */
    private void setUidFile(String uid){
        try {
            FileOutputStream outStream = this.openFileOutput("uid_file", Context.MODE_PRIVATE);
            outStream.write(uid.getBytes());
            outStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
