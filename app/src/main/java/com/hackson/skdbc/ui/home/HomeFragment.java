package com.hackson.skdbc.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.hackson.skdbc.CarListActivity;
import com.hackson.skdbc.MainActivity;
import com.hackson.skdbc.R;
import com.hackson.skdbc.SearchActivity;
import com.hackson.skdbc.dialog.LoadingDialog;
import com.hackson.skdbc.timerselect.BottomDialogView;
import com.hackson.skdbc.utils.MarkerOverlay;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements LocationSource, AMapLocationListener, AMap.OnMapLoadedListener, PoiSearch.OnPoiSearchListener {

    private HomeViewModel homeViewModel;
    private TextureMapView mapView;
    private AMap aMap;
    private MyLocationStyle myLocationStyle;
    private OnLocationChangedListener mListener;
    private AMapLocationClient locationClient;
    private AMapLocationClientOption clientOption;
    private View llSkipSearch;
    private boolean isLocation;
    private MarkerOverlay markerOverlay;
    private AMapLocation aMapLocation;
    private PoiSearch.Query query;
    private LatLng lo;
    private PoiSearch poiSearch;
    private TextView tvAddress;
    private TextView tvTime;
    private BottomDialogView bottomDialogView;
    private boolean isMapLoaded;
    private LoadingDialog loadingDialog;
    private String time;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        initMap(root, savedInstanceState);

        (((MainActivity) getActivity()).getToolbar()).setNavigationIcon(R.mipmap.user_icon);

        initView(root);

        if (loadingDialog == null && !isLocation && !isMapLoaded) {
            loadingDialog = new LoadingDialog(getActivity());
            loadingDialog.show();
        }

        return root;
    }

    private void initView(View view) {
        tvAddress = view.findViewById(R.id.tv_address);
        tvTime = view.findViewById(R.id.tv_time);
        view.findViewById(R.id.ll_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomDialogView == null) {
                    bottomDialogView = new BottomDialogView(getActivity(), true);
                    bottomDialogView.setConfirmClickListener(new BottomDialogView.onConfirmClick() {
                        @Override
                        public void onClick(String time) {
                            HomeFragment.this.time = time;
                            tvTime.setText("时间：" + time);
                            toSkipList();
                        }
                    });
                }
                bottomDialogView.show();
            }
        });

        llSkipSearch = view.findViewById(R.id.ll_search_location);
        llSkipSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    private void toSkipList() {
        String address = tvAddress.getText().toString().trim();
        String timeValue = tvTime.getText().toString().trim();

        if (TextUtils.isEmpty(timeValue) || TextUtils.isEmpty(address)) {
            return;
        }

        if (loadingDialog != null) {
            loadingDialog.show();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getActivity(), CarListActivity.class);
                intent.putExtra("time", time);
                intent.putExtra("address", address);
                startActivity(intent);
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
            }
        }, 1000);
    }

    private void initMap(View root, Bundle savedInstanceState) {
        mapView = (TextureMapView) root.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.getUiSettings().setMyLocationButtonEnabled(false);
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.setLocationSource(this);
        aMap.setMyLocationEnabled(true);
        aMap.setOnMapLoadedListener(this);
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (locationClient == null) {
            locationClient = new AMapLocationClient(getActivity());
            clientOption = new AMapLocationClientOption();
            locationClient.setLocationListener(this);
            clientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//高精度定位
            clientOption.setOnceLocationLatest(true);//设置单次精确定位
            locationClient.setLocationOption(clientOption);
            locationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (locationClient != null) {
            locationClient.stopLocation();
            locationClient.onDestroy();
        }
        locationClient = null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0 && !isLocation) {
                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
                this.aMapLocation = aMapLocation;
                isLocation = true;
                aMap.setMyLocationEnabled(false);
                load();
            } else {
                if (!isLocation) {
                    String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                    Log.e("AmapErr", errText);
                } else {
                    Log.i("AmapErr", "定位成功");
                }
            }
        }
    }

    private List<LatLng> getPointList() {
        List<LatLng> pointList = new ArrayList<LatLng>();
        pointList.add(new LatLng(39.993755, 116.467987));
        pointList.add(new LatLng(39.985589, 116.469306));
        pointList.add(new LatLng(39.990946, 116.48439));
        pointList.add(new LatLng(40.000466, 116.463384));
        pointList.add(new LatLng(39.975426, 116.490079));
        pointList.add(new LatLng(40.016392, 116.464343));
        pointList.add(new LatLng(39.959215, 116.464882));
        pointList.add(new LatLng(39.962136, 116.495418));
        pointList.add(new LatLng(39.994012, 116.426363));
        pointList.add(new LatLng(39.960666, 116.444798));
        pointList.add(new LatLng(39.972976, 116.424517));
        pointList.add(new LatLng(39.951329, 116.455913));
        return pointList;
    }


    /**
     * 必须重写以下方法
     */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (locationClient != null) {
            locationClient.onDestroy();
        }
    }

    @Override
    public void onMapLoaded() {
        //添加MarkerOnerlay

        isMapLoaded = true;
        load();

    }

    private void load() {

        lo = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());

        doSearchQuery(lo);

        if (isMapLoaded && isLocation) {
            if (loadingDialog != null) {
                loadingDialog.dismiss();
            }
        }
    }

    protected void doSearchQuery(LatLng lp) {
        query = new PoiSearch.Query("超市", "", "北京市");
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(0);// 设置查第一页

        if (lp != null) {
            poiSearch = new PoiSearch(getActivity(), query);
            poiSearch.setOnPoiSearchListener(this);
            LatLonPoint llp = new LatLonPoint(lp.latitude, lp.longitude);
            poiSearch.setBound(new PoiSearch.SearchBound(llp, 5000, true));
            poiSearch.searchPOIAsyn();// 异步搜索
        }
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        if (poiResult != null) {
            ArrayList<LatLng> list = new ArrayList<>();
            for (PoiItem pois : poiResult.getPois()) {
                LatLonPoint latLonPoint = pois.getLatLonPoint();
                list.add(new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude()));
            }

            markerOverlay = new MarkerOverlay(aMap, list, lo);
            markerOverlay.addToMap();
            markerOverlay.zoomToSpanWithCenter();
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1 && data != null) {
            Tip tip = data.getParcelableExtra("tip");
            String address = tip.getAddress();
            tvAddress.setText(tip.getName() + " " + address);
            toSkipList();
        }
    }
}