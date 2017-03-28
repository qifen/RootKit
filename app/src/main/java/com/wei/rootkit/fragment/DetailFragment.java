package com.wei.rootkit.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wei.rootkit.R;
import com.wei.rootkit.activity.DetailActivity;
import com.wei.rootkit.service.DetailService;

import java.io.File;

/**
 * Created by weiyilin on 16/12/27.
 */

public class DetailFragment extends Fragment {
    private View rootView;
    private int index=0;
    private String packageName="";
    private DetailService detailService=DetailService.getInstance();

    private TextView textView;
    private ImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.detail_fragment, container, false);
        Bundle bundle=this.getArguments();
        packageName=bundle.getString("packageName");
        index=bundle.getInt("index");

        if(index==0){//显示日志
            String content=detailService.getLogContent(packageName.trim());
            textView=(TextView) rootView.findViewById(R.id.textView);
            textView.setMovementMethod(ScrollingMovementMethod.getInstance());
            textView.setText(content);

        }else{//显示图
            String imagePath="/sdcard/pic/a.png";
            //String imagePath="/sdcard/pic/"+packageName.trim();
            File f=new File(imagePath);

            if(!f.exists()){
                detailService.generatePicture(packageName.trim(),this.getContext());
            }

            imageView = (ImageView) rootView.findViewById(R.id.pinchImageView);
            Bitmap bm = BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(bm);

        }
        return rootView;
    }
}
