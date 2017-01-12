package com.wei.rootkit.activity;

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

public class DetailActivity extends AppCompatActivity {
    private HashMap<Integer, Fragment> mFragments = new HashMap<Integer, Fragment>();
    private ViewPager viewPager;
    private PagerTab tabs;
    private FragmentStatePagerAdapter pagerAdapter;

    private List<String> titleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
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

        pagerAdapter = new InfoPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabs.setViewPager(viewPager);
    }

    private class InfoPagerAdapter extends FragmentStatePagerAdapter {

        private String[] info_names;

        public InfoPagerAdapter(FragmentManager fm) {
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
