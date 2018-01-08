package com.xsq.czy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xsq.czy.R;
import com.xsq.czy.beans.Apply;

import java.util.List;

/**
 * Created by Administrator on 2017/5/2.
 */
public class OldActionAdapter extends BaseAdapter {

    private Context context;

    private List<Apply> list;

    public OldActionAdapter(Context context, List<Apply> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null ;
        Apply apply = list.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.old_action_item, null);
            holder.numberTv = (TextView) convertView.findViewById(R.id.old_action_item_number);
            holder.areaTv = (TextView) convertView.findViewById(R.id.old_action_item_area);
            holder.operatorTv = (TextView) convertView.findViewById(R.id.old_action_item_operator);
            holder.timeTv = (TextView) convertView.findViewById(R.id.old_action_item_time);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.numberTv.setText(apply.getDeviceName());
        holder.operatorTv.setText(apply.getMemberName());
        holder.areaTv.setText(apply.getDept());
        holder.timeTv.setText(apply.getTime());
        return convertView;
    }

    class ViewHolder {
        TextView timeTv, numberTv,areaTv,operatorTv;
    }

}
