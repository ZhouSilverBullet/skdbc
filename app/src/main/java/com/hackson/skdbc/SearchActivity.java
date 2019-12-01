package com.hackson.skdbc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.hackson.skdbc.adapter.InputTipsAdapter;
import com.hackson.skdbc.utils.Constants;
import com.hackson.skdbc.utils.StatusUtil;
import com.hackson.skdbc.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, AdapterView.OnItemClickListener, Inputtips.InputtipsListener {

    private SearchView mSearchView;
    private ListView mInputListView;
    private InputTipsAdapter mIntipAdapter;
    private List<Tip> mCurrentTipList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        StatusUtil.setDarkStatusIcon(getWindow(), true);

        initView();
    }

    private void initView() {
        mSearchView = findViewById(R.id.sv_view);
        mSearchView.setOnQueryTextListener(this);
        //设置SearchView默认为展开显示
        mSearchView.setIconified(false);
        mSearchView.onActionViewExpanded();
        mSearchView.setIconifiedByDefault(true);
        mSearchView.setSubmitButtonEnabled(false);

        findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mInputListView = (ListView) findViewById(R.id.inputtip_list);
        mInputListView.setOnItemClickListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
//        Toast.makeText(this, newText, Toast.LENGTH_SHORT).show();
        if (!IsEmptyOrNullString(newText)) {
            InputtipsQuery inputquery = new InputtipsQuery(newText, Constants.DEFAULT_CITY);
            Inputtips inputTips = new Inputtips(this.getApplicationContext(), inputquery);
            inputTips.setInputtipsListener(this);
            inputTips.requestInputtipsAsyn();
        } else {
            if (mIntipAdapter != null && mCurrentTipList != null) {
                mCurrentTipList.clear();
                mIntipAdapter.notifyDataSetChanged();
            }
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Tip itemAtPosition = (Tip) adapterView.getItemAtPosition(position);
        Intent data = new Intent();
        data.putExtra("tip", itemAtPosition);
        setResult(1, data);
        finish();
//        Toast.makeText(this, ((Tip) adapterView.getItemAtPosition(position)).getAddress(), Toast.LENGTH_SHORT).show();

//        if (mCurrentTipList != null) {
//            Tip tip = (Tip) adapterView.getItemAtPosition(i);
//            Intent intent = new Intent();
//            intent.putExtra(Constants.EXTRA_TIP, tip);
//            setResult(MainActivity.RESULT_CODE_INPUTTIPS, intent);
//            this.finish();
//        }
    }

    public static boolean IsEmptyOrNullString(String s) {
        return (s == null) || (s.trim().length() == 0);
    }

    @Override
    public void onGetInputtips(List<Tip> tipList, int rCode) {
        if (rCode == 1000) {// 正确返回
            mCurrentTipList = tipList;
            List<String> listString = new ArrayList<String>();
            for (int i = 0; i < tipList.size(); i++) {
                listString.add(tipList.get(i).getName());
            }
            mIntipAdapter = new InputTipsAdapter(
                    getApplicationContext(),
                    mCurrentTipList);
            mInputListView.setAdapter(mIntipAdapter);
            mIntipAdapter.notifyDataSetChanged();
        } else {
            ToastUtil.showerror(this, rCode);
        }
    }
}
