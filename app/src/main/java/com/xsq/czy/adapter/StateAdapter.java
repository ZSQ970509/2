package com.xsq.czy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xsq.czy.R;
import com.xsq.czy.beans.Device;

import java.util.List;

/**
 * Created by Administrator on 2017/5/2.
 */
public class StateAdapter extends BaseAdapter {

    private Context context;

    private List<Device> list;

    public StateAdapter(Context context, List<Device> list) {
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
        Device device = list.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.state_item, null);
            holder.numberTv = (TextView) convertView.findViewById(R.id.state_item_number);
            holder.areaTv = (TextView) convertView.findViewById(R.id.state_item_area);
            holder.operatorTv = (TextView) convertView.findViewById(R.id.state_item_operator);
            holder.stateTv = (TextView) convertView.findViewById(R.id.state_item_state_img);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.numberTv.setText(device.getName() == null ? "" : device.getName());
        holder.operatorTv.setText(device.getMemberName() == null ? "" : device.getMemberName());
        holder.areaTv.setText(device.getDept() == null ? "" : device.getDept());
        if (device.getState().equals("0")) {
            holder.stateTv.setText("已开锁");
        }else {
            holder.stateTv.setText("已关锁");
        }
        return convertView;
    }

    class ViewHolder {
        TextView numberTv,areaTv,operatorTv,stateTv;
    }

}
