package com.hackson.skdbc.timerselect;


import java.util.ArrayList;
import java.util.List;

import cn.addapp.pickers.adapter.WheelAdapter;

/**
 * @author :  lwb
 *         Date: 2018/10/22
 *         Desc:
 */

public class HourAdapter implements WheelAdapter<String> {

    List<String> minList = new ArrayList<>();

    public HourAdapter() {
        for (int i = 0; i < 24; i++) {
            if (i < 10) {
                minList.add("0" + i + "");
            } else {
                minList.add(i + "");
            }
        }
    }

    @Override
    public int getItemsCount() {
        return minList.size();
    }

    @Override
    public String getItem(int index) {
        return minList.get(index);
    }

    @Override
    public int indexOf(String o) {
        for (int i = 0; i < minList.size(); i++) {
            String s = minList.get(i);
            if (s.equals(o)) {
                return i;
            }
        }
        return 0;
    }

}
