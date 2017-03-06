package com.wei.rootkit.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.wei.rootkit.R;
import com.wei.rootkit.activity.InfoActivity;
import com.wei.rootkit.model.Icon;
import com.wei.rootkit.model.Item;
import com.wei.rootkit.util.RootKitUtil;

import java.util.HashMap;
import java.util.List;

/**
 * Created by weiyilin on 16/12/27.
 */

public class ListAdapter extends BaseAdapter {
    public static HashMap<Integer, Boolean> isSelected;
    private Context context;
    private List<Item> list;
    private List<Icon> icons;

    public ListAdapter(Context context, List<Item> items, List<Icon> icons){
        this.context = context;
        this.list = items;
        this.icons = icons;
        initCheck();
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
        ItemView itemView;
        final Item item = list.get(position);
        if (convertView == null){
            itemView = new ItemView();
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, null);
            itemView.checkBox = (CheckBox) convertView.findViewById(R.id.item_check);
            itemView.icon = (ImageView) convertView.findViewById(R.id.icon);
            itemView.name = (TextView) convertView.findViewById(R.id.item_title);
            itemView.version = (TextView) convertView.findViewById(R.id.item_subtitle);
            itemView.detect = (Button) convertView.findViewById(R.id.item_detect);

            convertView.setTag(itemView);
        }else {
            itemView = (ItemView) convertView.getTag();
        }

        itemView.icon.setImageBitmap(RootKitUtil.drawableToBitmap(icons.get(position).getIcon()));
        itemView.name.setText(item.getAppName());
        itemView.version.setText("版本号: " + item.getVersionId());
        itemView.detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.item_detect){
                    Intent intent = new Intent(context, InfoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("item", item);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            }
        });
        itemView.checkBox.setChecked(isSelected.get(position));

        return convertView;
    }


    /**
     * 初始化,设置所有checkbox未选择
     */
    private void initCheck(){
        isSelected = new HashMap<>();
        for (int i = 0; i < list.size(); i++){
            isSelected.put(i, false);
        }
    }

    public class ItemView{
        public CheckBox checkBox;
        public ImageView icon;
        public TextView name;
        public TextView version;
        public Button detect;
    }


}
