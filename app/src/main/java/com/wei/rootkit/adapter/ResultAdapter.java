package com.wei.rootkit.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wei.rootkit.R;
import com.wei.rootkit.activity.DetailActivity;
import com.wei.rootkit.activity.MainActivity;
import com.wei.rootkit.model.Icon;
import com.wei.rootkit.model.Item;
import com.wei.rootkit.service.ResultService;
import com.wei.rootkit.util.RootKitUtil;

import java.util.List;

/**
 * Created by weiyilin on 17/3/6.
 */

public class ResultAdapter extends BaseAdapter {
    private Context context;
    private List<Item> list;
    private ResultService resultService=ResultService.getInstance();

    public ResultAdapter(Context context, List<Item> list){
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list == null ? null : list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        Item item = list.get(position);
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.result_item, null);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
            viewHolder.name = (TextView) convertView.findViewById(R.id.item_title);
            viewHolder.version = (TextView) convertView.findViewById(R.id.item_subtitle);
            viewHolder.detail = (Button) convertView.findViewById(R.id.item_detail);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(item.getAppName());
        viewHolder.version.setText("版本号: " + item.getVersionId());
        final String packageName=item.getPackageName().trim();
        final String uid=item.getId().trim();
        viewHolder.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.item_detail){
                    resultService.generateLog(packageName,uid);

                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("packageName",packageName);
                    context.startActivity(intent);
                }
            }
        });
        for (int i = 0; i < MainActivity.icons.size(); i ++){
            Icon icon = MainActivity.icons.get(i);
            if (item.getAppName().equals(icon.getAppName()) && item.getId().equals(icon.getId())){
                viewHolder.icon.setImageBitmap(RootKitUtil.drawableToBitmap(icon.getIcon()));
                break;
            }
        }
        return convertView;
    }

    public class ViewHolder{
        public ImageView icon;
        public TextView name;
        public TextView version;
        public Button detail;
    }
}
