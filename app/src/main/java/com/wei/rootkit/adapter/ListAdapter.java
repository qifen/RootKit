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
import com.wei.rootkit.model.Item;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by weiyilin on 16/12/27.
 */

public class ListAdapter extends BaseAdapter {
    private Context context;
    private List<Item> list;
    private MaterialDialog mMaterialDialog;

    public ListAdapter(Context context){
        this.context = context;
        initData();
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
            itemView.size = (TextView) convertView.findViewById(R.id.item_subtitle);
            itemView.detect = (Button) convertView.findViewById(R.id.item_detect);

            convertView.setTag(itemView);
        }else {
            itemView = (ItemView) convertView.getTag();
        }

        itemView.name.setText(item.getName());
        itemView.size.setText(item.getSize());
        itemView.detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog = new MaterialDialog(context)
                        .setTitle("开始检测 " + item.getName())
                        .setMessage("请打开" + item.getName() + ", 执行各项需检测的操作, 操作完毕后可" +
                                "点击分析按钮。如需取消检测, 点击取消按钮")
                        .setPositiveButton("分析", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, DetailActivity.class);
                                context.startActivity(intent);
                                mMaterialDialog.dismiss();
                            }
                        })
                        .setNegativeButton("取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mMaterialDialog.dismiss();
                            }
                        });

                mMaterialDialog.show();
            }
        });

        return convertView;
    }

    /**
     * 造写假数据
     */
    private void initData(){
        list = new ArrayList<>();
        Item qq = new Item("QQ", "92.18MB");
        Item weiXin = new Item("微信", "64.76MB");
        Item qqAnquan = new Item("QQ安全中心", "2.78MB");
        Item qqZone = new Item("QQ空间", "22.46MB");
        Item kugou = new Item("酷狗音乐", "23.02MB");
        Item taobao = new Item("手机淘宝", "100.08MB");
        Item yingyongbao = new Item("应用宝", "46.17MB");
        Item qqBrower = new Item("QQ浏览器", "56.56MB");
        Item zhifubao = new Item("支付宝", "120.28MB");

        list.add(qq);
        list.add(weiXin);
        list.add(qqAnquan);
        list.add(qqZone);
        list.add(kugou);
        list.add(taobao);
        list.add(yingyongbao);
        list.add(qqBrower);
        list.add(zhifubao);
    }

    private class ItemView{
        ImageView icon;
        TextView name;
        TextView size;
        Button detect;
    }
}
