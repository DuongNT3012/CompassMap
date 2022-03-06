package com.digital.compass.maps.digitalcompass.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.digital.compass.maps.digitalcompass.Adapter.AdapterCompass_V2;
import com.digital.compass.maps.digitalcompass.MyViewModel;
import com.digital.compass.maps.digitalcompass.R;
import com.digital.compass.maps.digitalcompass.Utils.AppConstant;
import com.digital.compass.maps.digitalcompass.model.ModelCompass;
import com.orhanobut.hawk.Hawk;
import com.vapp.admoblibrary.ads.AdCallback;
import com.vapp.admoblibrary.ads.AdmodUtils;

import java.util.ArrayList;

import butterknife.OnClick;

public class ChangeCompassActivity extends AppCompatActivity {

    private AdapterCompass_V2 adapter;
    private Boolean clickReward = false;
    private Handler handler;
    private ArrayList<ModelCompass> list;

    private long mLastClickTime = 0;
    private MyViewModel myViewModel;
    RecyclerView rcvChangeCompass;
    ImageView imageHome, imageCompass, imageLocation, imageView_Setting;
    Intent intent;
    ImageView iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_compass);
        //Keep screen on
        SharedPreferences preferences = getSharedPreferences("MY_PRE", MODE_PRIVATE);
        boolean screenOn = preferences.getBoolean("screenOn", false);
        if (screenOn == true) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        //
        initLayout();
        //
        setOnClick();

        //
        GridLayoutManager gridLayoutManager = new GridLayoutManager(ChangeCompassActivity.this, 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            /* class com.compass.compassapp.compassfree.FragmentChangeCompatV2.AnonymousClass2 */

            @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
            public int getSpanSize(int i) {
                return (i != 2 || AppConstant.removeAds) ? 1 : 2;
            }
        });
        this.list = new ArrayList<>();
        this.list.clear();
        /*ArrayList arrayList = new ArrayList((Collection) Hawk.get("list_compass", this.list));
        if (arrayList.size() > 1) {
            this.list.addAll(arrayList);
        } else {*/
        this.list.add(new ModelCompass("Golden Compass", R.drawable.ic_compass1, R.drawable.ic_azi_theme1, false, 1));
        if(AppConstant.isNetworkAvailable(ChangeCompassActivity.this)){
            this.list.add(new ModelCompass("", 0, 0, false, 2));
        }
        this.list.add(new ModelCompass("Sea Compass", R.drawable.ic_compass6, R.drawable.ic_azi_theme2, false, 1));
        this.list.add(new ModelCompass("Valentine 1 ", R.drawable.ic_compass_vlt_01, R.drawable.ic_azi_theme3, false, 1));
        if(AppConstant.isNetworkAvailable(ChangeCompassActivity.this)){
            this.list.add(new ModelCompass("", 0, 0, false, 2));
        }
        this.list.add(new ModelCompass("Valentine 5", R.drawable.ic_compass_vlt_05, R.drawable.ic_azi_theme4, false, 1));
        this.list.add(new ModelCompass("Vintage Compass", R.drawable.ic_compass4, R.drawable.ic_azi_theme5, false, 1));
        this.list.add(new ModelCompass("Technology", R.drawable.ic_compass_technology, R.drawable.v2_azi_2, false, 1));
        this.list.add(new ModelCompass("Natural", R.drawable.ic_compass_natural, R.drawable.v2_azi_6, false, 1));
        this.list.add(new ModelCompass("Jack Sparrow", R.drawable.ic_compass_jackparrow, R.drawable.v2_azi_1, false, 1));
        this.list.add(new ModelCompass("Christmas", R.drawable.ic_compass_chrismas, R.drawable.v2_azi_5, false, 1));
        this.list.add(new ModelCompass("Happy New Year", R.drawable.ic_compass_new_year, R.drawable.v2_azi_5, false, 1));

            /*this.list.add(new ModelCompass("Default", R.drawable.compass3, 0, true));
            this.list.add(new ModelCompass("Valentine 1 ", R.drawable.compass_vlt_01, R.drawable.kim_vlt1, true));
            this.list.add(new ModelCompass("Valentine 2", R.drawable.compass_vlt_02, R.drawable.kim_vlt2, true));
            this.list.add(new ModelCompass("Valentine 3", R.drawable.compass_vlt_03, R.drawable.kim_vlt3, true));
            this.list.add(new ModelCompass("Valentine 4", R.drawable.compass_vlt_04, R.drawable.kim_vlt3, true));
            this.list.add(new ModelCompass("Valentine 5", R.drawable.compass_vlt_05, R.drawable.kim_vlt5, true));
            this.list.add(new ModelCompass("Christmas", R.drawable.compass_chrismas, R.drawable.v2_azi_5, true));
            this.list.add(new ModelCompass("Happy New Year", R.drawable.compass_new_year, R.drawable.v2_azi_5, true));
            this.list.add(new ModelCompass("Technology", R.drawable.compass_technology, R.drawable.v2_azi_2, true));
            this.list.add(new ModelCompass("Army", R.drawable.compass_army, R.drawable.v2_azi_3, true));
            this.list.add(new ModelCompass("Natural", R.drawable.compass_natural, R.drawable.v2_azi_6, true));
            this.list.add(new ModelCompass("Jack Sparrow", R.drawable.compass_jackparrow, R.drawable.v2_azi_1, true));
            this.list.add(new ModelCompass("American Compass", R.drawable.compass5, R.drawable.azi2, true));
            this.list.add(new ModelCompass("Sea Compass", R.drawable.compass6, R.drawable.azi, true));
            this.list.add(new ModelCompass("Vintage Compass", R.drawable.compass4, R.drawable.azi2, true));
            this.list.add(new ModelCompass("Golden Compass", R.drawable.compass1, R.drawable.azi, true));*/
        //this.list.add(new ModelCompass("Wooden Compass", R.drawable.compass_v2, R.drawable.azi2, true));
        /*Hawk.put("list_compass", this.list);*/
        /*}*/
        this.adapter = new AdapterCompass_V2(ChangeCompassActivity.this, this.list);
        this.rcvChangeCompass.setLayoutManager(gridLayoutManager);
        this.rcvChangeCompass.setHasFixedSize(true);
        this.rcvChangeCompass.setAdapter(this.adapter);
        this.rcvChangeCompass.setItemViewCacheSize(50);
        this.adapter.setOnClickListener(new AdapterCompass_V2.OnClick() {
            @Override
            public final void Click(View view, int i) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChangeCompassActivity.this);
                builder.setTitle("Change theme");
                builder.setMessage("Are you sure to change theme?");
                builder.setNegativeButton("Cancel", (dialogInterface, i1) -> {});
                builder.setPositiveButton("Change", (dialogInterface, i1) -> {
                    AdmodUtils.getInstance().loadAndShowAdInterstitialWithCallback(ChangeCompassActivity.this, getString(R.string.interstitial_apply), 0, new AdCallback() {
                        @Override
                        public void onAdClosed() {
                            AdmodUtils.getInstance().dismissAdDialog();
                            Toast.makeText(getBaseContext(), "Theme apply", Toast.LENGTH_LONG).show();
                            ChangeCompassActivity.this.lambda$onViewCreated$0$FragmentChangeCompatV2(view, view, i);
                        }

                        @Override
                        public void onAdFail() {
                            onAdClosed();
                        }
                    }, true);
                });
                builder.show();
            }
        });

        hideStatusAndNavigationBar();
    }

    public void hideStatusAndNavigationBar() {
        getWindow().getDecorView().setSystemUiVisibility(Build.VERSION.SDK_INT >= 19 ? 3334 : 1798);
        getWindow().addFlags(1024);
        getWindow().addFlags(128);
    }

    private void setOnClick() {
        findViewById(R.id.ic_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(ChangeCompassActivity.this, MainCompassActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void initLayout() {
        iv_back = findViewById(R.id.iv_back);
        imageHome = findViewById(R.id.imageHome);
        imageHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(ChangeCompassActivity.this, MainCompassActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        imageCompass = findViewById(R.id.imageCompass);
        imageLocation = findViewById(R.id.imageLocation);
        imageLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(ChangeCompassActivity.this, LocationInfoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        imageView_Setting = findViewById(R.id.imageView_Setting);
        imageView_Setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(ChangeCompassActivity.this, SettingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        rcvChangeCompass = findViewById(R.id.rcv_change_compass);
    }

    @Override
    public void onResume() {
        super.onResume();
        Handler handler2 = new Handler();
        this.handler = handler2;
        handler2.postDelayed(new Runnable() {
            public void run() {
                AppConstant.showOpenAppAds = true;
            }
        }, 3000);
        //Keep screen on
        SharedPreferences preferences = getSharedPreferences("MY_PRE", MODE_PRIVATE);
        boolean screenOn = preferences.getBoolean("screenOn", false);
        if (screenOn == true) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        this.handler.removeCallbacksAndMessages(null);
        Log.i("vcvcvcvcvcvc", "vao2");
    }

    @Override
    public void onPause() {
        super.onPause();
        this.handler.removeCallbacksAndMessages(null);
        if (this.clickReward.booleanValue()) {
            AppConstant.showOpenAppAds = false;
        }
        Log.i("vcvcvcvcvcvc", "vao3");
    }

    @Override
    public void onBackPressed() {
        intent = new Intent(ChangeCompassActivity.this, MainCompassActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void lambda$onViewCreated$0$FragmentChangeCompatV2(View view, View view2, int i) {
        if (SystemClock.elapsedRealtime() - MainCompassActivity.lastClickTime >= 1000) {
            MainCompassActivity.lastClickTime = SystemClock.elapsedRealtime();
            if ((!this.list.get(i).isActive() || i == 2) && !AppConstant.removeAds) {
                showDialogToUnlock(i, view);
                return;
            }
            Bundle bundle = new Bundle();

            MyViewModel myViewModel2 = (MyViewModel) ViewModelProviders.of(ChangeCompassActivity.this).get(MyViewModel.class);
            this.myViewModel = myViewModel2;
            myViewModel2.setSelectedItem(this.list.get(i));
            //Edit
            SharedPreferences.Editor editor = getSharedPreferences("MY_PRE", MODE_PRIVATE).edit();
            editor.putInt("position", i);
            editor.apply();
            //Navigation.findNavController(view).navigate(R.id.action_fragmentChangeCompatV2_to_fragmentMainCompass);
            startActivity(new Intent(ChangeCompassActivity.this, MainCompassActivity.class));
            AppConstant.isShowRate = true;
            finish();
        }
    }

    private void showDialogToUnlock(int i, View view) {
        Bundle bundle = new Bundle();

        View inflate = getLayoutInflater().inflate(R.layout.dialog_watch_video, (ViewGroup) null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(ChangeCompassActivity.this);
        builder.setView(inflate);
        AlertDialog create = builder.create();
        create.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        create.show();
        Glide.with(this).load(Integer.valueOf((int) R.drawable.img_reward)).into((ImageView) inflate.findViewById(R.id.image_title_ad));
        ((ImageView) inflate.findViewById(R.id.ic_close_ad_video)).setOnClickListener(
                new View.OnClickListener() {

                    public final void onClick(View view) {
                        ChangeCompassActivity.this.customThree(create, view);
                    }
                });
        ((Button) inflate.findViewById(R.id.btn_cancel)).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChangeCompassActivity.this.customOne(i, create, view);
            }
        });
        ((Button) inflate.findViewById(R.id.btn_watchAD)).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChangeCompassActivity.this.customThree(create, view);
            }
        });
    }

    public void customOne(int i, Dialog dialog, View view) {
        if (canTouch()) {
            showReward(i, dialog);
        }
    }

    static void customThree(Dialog dialog, View view) {
        dialog.dismiss();
        //Navigation.findNavController(view).navigate(R.id.action_fragmentChangeCompatV2_to_removeAdFragment);
    }

    private boolean canTouch() {
        if (SystemClock.elapsedRealtime() - this.mLastClickTime < 2000) {
            return false;
        }
        this.mLastClickTime = SystemClock.elapsedRealtime();
        return true;
    }

    private void showReward(final int i, final Dialog dialog) {
        this.clickReward = true;
        dialog.dismiss();
        ChangeCompassActivity.this.openThisCompass(i, dialog);
    }

    private void openThisCompass(int i, Dialog dialog) {
        this.list.get(i).setActive(true);
        Hawk.put("list_compass", this.list);
        this.adapter.setList(this.list);
    }

    @OnClick({R.id.ic_back})
    public void onViewClicked() {
        onBackPressed();
    }
}