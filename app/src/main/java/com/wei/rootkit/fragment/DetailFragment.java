package com.wei.rootkit.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wei.rootkit.R;

/**
 * Created by weiyilin on 16/12/27.
 */

public class DetailFragment extends Fragment {
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.detail_fragment, container, false);
        return rootView;
    }
}
