package com.wei.rootkit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import com.wei.rootkit.R;
import com.wei.rootkit.fragment.DetailFragment;
import com.wei.rootkit.ui.PagerTab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by weiyilin on 16/12/27.
 */

public class DetailActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    private HashMap<Integer, Fragment> mFragments = new HashMap<>();
    private ViewPager viewPager;
    private PagerTab tabs;
    private DetailPagerAdapter pagerAdapter;

    private List<String> titleList;

    private String packageName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        Intent reciIntent = getIntent();   //接收上一级activity传过来的参数
        packageName = reciIntent.getStringExtra("packageName");

        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void initView(){
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabs = (PagerTab) findViewById(R.id.tabs);

        titleList = new ArrayList<>();
        titleList.add("行为日志");
        titleList.add("行为图");
        titleList.add("辨别结果");

        pagerAdapter = new DetailPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabs.setViewPager(viewPager);
        tabs.setOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        DetailFragment detailFragment = (DetailFragment) pagerAdapter.getItem(position);
        detailFragment.loadView();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class DetailPagerAdapter extends FragmentStatePagerAdapter {

        private String[] info_names;

        public DetailPagerAdapter(FragmentManager fm) {
            super(fm);
            info_names = titleList.toArray(new String[titleList.size()]);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return info_names[position];
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = mFragments.get(position);

            if (fragment == null) {
                switch (position) {
                    case 0:
                        fragment = new DetailFragment();
                        break;
                    default:
                        fragment = new DetailFragment();
                        break;

                }
                mFragments.put(position, fragment);
                Bundle bundle = new Bundle();
                bundle.putInt("index", position);
                bundle.putString("packageName", packageName.trim());
                fragment.setArguments(bundle);
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return info_names.length;
        }
    }
}
