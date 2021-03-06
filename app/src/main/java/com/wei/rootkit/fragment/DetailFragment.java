package com.wei.rootkit.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wei.rootkit.R;
import com.wei.rootkit.service.DetailService;

/**
 * Created by weiyilin on 16/12/27.
 */

public class DetailFragment extends Fragment {
    private View rootView;
    private DetailService detailService=DetailService.getInstance();

    private TextView textView;
    private ImageView imageView;

    private LinearLayout linearLayout;
    private TextView content;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.detail_fragment, container, false);
        initView();
        return rootView;
    }

    private void initView(){
        textView = (TextView) rootView.findViewById(R.id.textView);
        imageView = (ImageView) rootView.findViewById(R.id.pinchImageView);
        linearLayout = (LinearLayout) rootView.findViewById(R.id.ll_layout);
        content = (TextView) rootView.findViewById(R.id.content);
    }

    public void loadView(){
        detailService.setDetailFragment(this);

        Bundle bundle = getArguments();
        String packageName = bundle.getString("packageName");
        int index = bundle.getInt("index");

        if(index == 0){//显示日志
            String content = detailService.getLogContent(packageName.trim());
            Log.d("hahaha", content + "yafen");

            //String content = "日志";

            textView.setMovementMethod(ScrollingMovementMethod.getInstance());
            textView.setText(content);
            textView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);

        }else if (index == 1){//显示图
            detailService.generatePicture(packageName.trim(), this.getContext());

            //add
        }else if (index == 2){//显示分析结果
            detailService.getAnalysisResult(packageName.trim(),this.getContext());
//            textView.setMovementMethod(ScrollingMovementMethod.getInstance());
//            textView.setText("恶意软件");
//            textView.setVisibility(View.VISIBLE);
//            imageView.setVisibility(View.GONE);
        }
    }
}
