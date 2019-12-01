package com.hackson.skdbc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hackson.skdbc.adapter.CarListAdapter;
import com.hackson.skdbc.entity.CarInfoBean;
import com.hackson.skdbc.utils.StatusUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CarListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private Toolbar toolbar;
    private String time;
    private String address;
    private ListView listView;
    private CarListAdapter adapter;
    private List<CarInfoBean> carList;

    public static final String[] addressStr = {
            "速派", "阿迪亚克", "柯米克", "明锐"
    };

    public static final int[] car_icon = {
            R.mipmap.supai, R.mipmap.aldiyake, R.mipmap.kemike, R.mipmap.mingrv
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_list);
        StatusUtil.setDarkStatusIcon(getWindow(), true);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitle("选择车辆");
        toolbar.setTitleTextColor(getResources().getColor(R.color.textColor));

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent() != null) {
            time = getIntent().getStringExtra("time");
            address = getIntent().getStringExtra("address");
        }

        listView = findViewById(R.id.lv);
        carList = new ArrayList<>();
        adapter = new CarListAdapter(this, carList);
        listView.setAdapter(adapter);

        for (int i = 0; i < 20; i++) {
            CarInfoBean bean = new CarInfoBean();
            carList.add(bean);
            bean.setCarName("Sokda " + addressStr[i % addressStr.length]);
            bean.setIcon(car_icon[i % car_icon.length]);
            Random random = new Random();
            float num = random.nextFloat() * 5;
            if (num <= 1) {
                num = 4;
            }
            if (num >= 4) {
                num -= 1;
            }
            bean.setPeopleNum("可乘1~" + ((int) (num)) + "人");
            bean.setTime("2019-12-01~12.03");

            bean.setType(((int) (num)) % 2 == 0 ? "半日游" : "一天内");
            if (num <= 2) {
                num += 1;
            }
            bean.setStarNum(num + 1);
        }

        adapter.notifyDataSetChanged();

        listView.setOnItemClickListener(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (toolbar != null) {
            toolbar.setTitle("选择车辆");
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CarInfoBean bean = carList.get(position);
        Intent intent = new Intent(this, CarDetailActivity.class);
        startActivity(intent);
    }
}
