package com.digital.compass.maps.digitalcompass.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.digital.compass.maps.digitalcompass.R;
import com.vapp.admoblibrary.ads.AdCallback;
import com.vapp.admoblibrary.ads.AdmodUtils;
import com.vapp.admoblibrary.utils.Utils;

/*import org.opencv.android.OpenCVLoader;*/

public class SplashActivity extends AppCompatActivity {

    boolean guided = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        /*if (!OpenCVLoader.initDebug())
            Log.d("OpenCV", "Unable to load OpenCV!");
        else
            Log.d("OpenCV", "OpenCV loaded Successfully!");*/

        SharedPreferences prefsGuide = getSharedPreferences("MY_PREFS_GUIDE", MODE_PRIVATE);
        guided = prefsGuide.getBoolean("guided", false);
        if (!guided) {
            AdmodUtils.getInstance().loadAndShowAdInterstitialWithCallback(SplashActivity.this, getString(R.string.interstitial_splash), 0, new AdCallback() {
                @Override
                public void onAdClosed() {
                    AdmodUtils.getInstance().dismissAdDialog();
                    Utils.getInstance().replaceActivity(SplashActivity.this, TutorialActivity.class);
                }

                @Override
                public void onAdFail() {
                    onAdClosed();
                }
            }, false);
        } else {
            /*SharedPreferences sharedPreferences = getSharedPreferences("MY_PREFS_THEME", MODE_PRIVATE);
            boolean theme = sharedPreferences.getBoolean("theme", true);
            if (theme == true) {
                AdmodUtils.getInstance().loadAndShowAdInterstitialWithCallback(SplashActivity.this, getString(R.string.interstitial_splash), 0, new AdCallback() {
                    @Override
                    public void onAdClosed() {
                        AdmodUtils.getInstance().dismissAdDialog();
                        Utils.getInstance().replaceActivity(SplashActivity.this, MainCompassActivity.class);
                    }

                    @Override
                    public void onAdFail() {
                        onAdClosed();
                    }
                }, false);
            } else {
                AdmodUtils.getInstance().loadAndShowAdInterstitialWithCallback(SplashActivity.this, getString(R.string.interstitial_splash), 0, new AdCallback() {
                    @Override
                    public void onAdClosed() {
                        SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS_THEME", MODE_PRIVATE).edit();
                        editor.putBoolean("theme", true);
                        editor.apply();
                        AdmodUtils.getInstance().dismissAdDialog();
                        Utils.getInstance().replaceActivity(SplashActivity.this, ChangeCompassActivity.class);
                    }

                    @Override
                    public void onAdFail() {
                        onAdClosed();
                    }
                }, false);
            }*/
            AdmodUtils.getInstance().loadAndShowAdInterstitialWithCallback(SplashActivity.this, getString(R.string.interstitial_splash), 0, new AdCallback() {
                @Override
                public void onAdClosed() {
                    AdmodUtils.getInstance().dismissAdDialog();
                    Utils.getInstance().replaceActivity(SplashActivity.this, MainCompassActivity.class);
                }

                @Override
                public void onAdFail() {
                    onAdClosed();
                }
            }, false);
        }
    }
}