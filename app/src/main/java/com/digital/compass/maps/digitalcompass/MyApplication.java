package com.digital.compass.maps.digitalcompass;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.multidex.MultiDexApplication;

import com.digital.compass.maps.digitalcompass.Activity.SplashActivity;
import com.digital.compass.maps.digitalcompass.Utils.AppOpenManager;
import com.vapp.admoblibrary.ads.AdmodUtils;

public class MyApplication extends MultiDexApplication {
    AppOpenManager appOpenManager;
    boolean isShowAds = true;
    boolean isShowAdsResume = true;
    public void onCreate() {
        super.onCreate();

        this.appOpenManager = new AppOpenManager(this);

        AdmodUtils.getInstance().initAdmob(this, 20000, false, isShowAds);

        if (isShowAdsResume){
            com.vapp.admoblibrary.ads.AppOpenManager.getInstance().init(this, getString(R.string.app_open_resume));
            com.vapp.admoblibrary.ads.AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity.class);
        }
    }

    public static int getCountOpenApp(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return pre.getInt("counts", 1);
    }
    public static void increaseCountOpenApp(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putInt("counts", pre.getInt("counts", 1) + 1);
        editor.commit();
    }
}
