package com.wei.rootkit.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wei.rootkit.R;
import com.wei.rootkit.activity.InfoActivity;
import com.wei.rootkit.model.Item;

import java.util.List;

/**
 * Created by weiyilin on 16/12/27.
 */

public class ListAdapter extends BaseAdapter {
    private Context context;
    private List<Item> list;

    public ListAdapter(Context context, List<Item> items){
        this.context = context;
        this.list = items;
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
            itemView.icon = (ImageView) convertView.findViewById(R.id.icon);
            itemView.name = (TextView) convertView.findViewById(R.id.item_title);
            itemView.version = (TextView) convertView.findViewById(R.id.item_subtitle);
            itemView.detect = (Button) convertView.findViewById(R.id.item_detect);

            convertView.setTag(itemView);
        }else {
            itemView = (ItemView) convertView.getTag();
        }

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

        return convertView;
    }

    /**
     * 造写假数据
     */
    private void initData(){
//        list = new ArrayList<>();
//        Item qq = new Item("QQ", "92.18MB");
//        Item weiXin = new Item("微信", "64.76MB");
//        Item qqAnquan = new Item("QQ安全中心", "2.78MB");
//        Item qqZone = new Item("QQ空间", "22.46MB");
//        Item kugou = new Item("酷狗音乐", "23.02MB");
//        Item taobao = new Item("手机淘宝", "100.08MB");
//        Item yingyongbao = new Item("应用宝", "46.17MB");
//        Item qqBrower = new Item("QQ浏览器", "56.56MB");
//        Item zhifubao = new Item("支付宝", "120.28MB");
//
//        list.add(qq);
//        list.add(weiXin);
//        list.add(qqAnquan);
//        list.add(qqZone);
//        list.add(kugou);
//        list.add(taobao);
//        list.add(yingyongbao);
//        list.add(qqBrower);
//        list.add(zhifubao);
    }

    private class ItemView{
        ImageView icon;
        TextView name;
        TextView version;
        Button detect;
    }
}
