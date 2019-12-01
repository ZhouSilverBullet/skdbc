package com.hackson.skdbc.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.hackson.skdbc.R;
import com.hackson.skdbc.entity.CarInfoBean;

import java.util.List;

/**
 * Email: zhousaito@163.com
 * Created by zhousaito 2019-12-01 11:22
 * Version: 1.0
 * Description:
 */
public class CarListAdapter extends BaseAdapter {
    private final Context context;
    private final LayoutInflater inflater;
    private List<CarInfoBean> list;

    public CarListAdapter(Context context, List<CarInfoBean> list) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_car_list, null);
        }
        ImageView ivIcon = convertView.findViewById(R.id.iv_icon);
        TextView tvCarName = convertView.findViewById(R.id.tv_car_name);
        TextView tvTime = convertView.findViewById(R.id.tv_time);
        TextView tvType = convertView.findViewById(R.id.tv_type);
        TextView tvPeopleNum = convertView.findViewById(R.id.tv_people_num);
        RatingBar rbStar = convertView.findViewById(R.id.rb_star);

        CarInfoBean bean = list.get(position);

        ivIcon.setImageResource(bean.getIcon());
        tvCarName.setText(bean.getCarName());
        tvPeopleNum.setText(bean.getPeopleNum());
        tvType.setText(bean.getType());
        tvTime.setText(bean.getTime());
        rbStar.setRating(bean.getStarNum());

        return convertView;
    }
}
