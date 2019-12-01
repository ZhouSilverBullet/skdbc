package com.hackson.skdbc.utils;

import android.os.Build;
import android.view.View;
import android.view.Window;

/**
 * Email: zhousaito@163.com
 * Created by zhousaito 2019-11-30 21:53
 * Version: 1.0
 * Description:
 */
public class StatusUtil {
    public static void setDarkStatusIcon(Window window, boolean bDark) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            View decorView = window.getDecorView();
            if(decorView != null){
                int vis = decorView.getSystemUiVisibility();
                if(bDark){
                    vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                } else{
                    vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
                decorView.setSystemUiVisibility(vis);
            }
        }
    }
}
