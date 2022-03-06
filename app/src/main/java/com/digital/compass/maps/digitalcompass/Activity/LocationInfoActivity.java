package com.digital.compass.maps.digitalcompass.Activity;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.internal.view.SupportMenu;
import androidx.core.view.InputDeviceCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.ActivityNavigator;

import com.bumptech.glide.Glide;
import com.digital.compass.maps.digitalcompass.R;
import com.digital.compass.maps.digitalcompass.Sensor.SensorView.SensorListener;
import com.digital.compass.maps.digitalcompass.Sensor.SensorView.SensorUtil;
import com.digital.compass.maps.digitalcompass.Utils.AppConstant;
import com.digital.compass.maps.digitalcompass.Utils.RotationGestureDetector;
import com.digital.compass.maps.digitalcompass.View.AccelerometerView;
import com.digital.compass.maps.digitalcompass.View.CompassView;
import com.digital.compass.maps.digitalcompass.View.CompassViewLocationInfo;
import com.digital.compass.maps.digitalcompass.fragment.CustomFours;
import com.digital.compass.maps.digitalcompass.fragment.CustomOnes;
import com.digital.compass.maps.digitalcompass.fragment.CustomThrees;
import com.digital.compass.maps.digitalcompass.fragment.CustomTwos;
import com.digital.compass.maps.digitalcompass.fragment.MapFragment;
import com.github.florent37.runtimepermission.PermissionResult;
import com.github.florent37.runtimepermission.RuntimePermission;
import com.github.florent37.runtimepermission.callbacks.AcceptedCallback;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.android.datatransport.runtime.Destination;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.vapp.admoblibrary.ads.AdmodUtils;
import com.vapp.admoblibrary.ads.admobnative.enumclass.GoogleEBanner;

/*import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;*/

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.OnClick;

public class LocationInfoActivity extends AppCompatActivity implements SensorListener.OnValueChangedListener, SensorEventListener, LocationListener/*, RotationGestureDetector.OnRotationGestureListener*/ {

    private Dialog dialog;
    private boolean internet_avalible = false;
    TextView tvAddress;
    TextView tvAddressDetail;
    TextView tvLatitude;
    TextView tvLatitudeDetail;
    TextView tvLongitudeDetail;
    TextView tvLongtitude;
    TextView tvMagnetic;
    TextView tvMagneticDetail;
    TextView tvTitle;
    ImageView btnCopyAddress;
    ImageView btnCopyLon;
    ImageView btnCopyLat;
    ImageView imageHome, imageCompass, imageLocation, imageView_Setting;
    Intent intent;
    ImageView imgNoInternet;
    ImageView iv_back;
    private LinearLayout banner;
    //Compass
    AccelerometerView accelerometerView;
    CompassViewLocationInfo compassDrawer;
    private SensorListener sensorListener;
    SensorListener sensorListener2;
    ImageView imageCompassNew;
    ImageView imageAzi;

    ImageView imgCapture, imgLock;
    int countLockCompass = 0;
    Bitmap bitmapMap, bitmapCompassInfo;
    ImageView imgMyLocation;

    TextView textViewBigNumber;
    TextView textViewBigSW;

    private RotationGestureDetector mRotationDetector;
    GeomagneticField geoField;
    float mDeclination = 0.0f;
    private float[] mRotationMatrix = new float[16];
    LocationListener locationListener;
    SensorManager sensorManager;
    private Sensor mLight;

    private SensorManager mSensorManager;
    Sensor sensor;
    boolean isCompassOn = false;
    Location location;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_info);
        hideStatusAndNavigationBar();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        banner = findViewById(R.id.banner);
        AdmodUtils.getInstance().loadAdBanner(LocationInfoActivity.this, getString(R.string.banner), banner, GoogleEBanner.SIZE_SMALL);
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

        //Check Internet
        if (AppConstant.isNetworkAvailable(LocationInfoActivity.this)) {
            //cardView.setVisibility(View.VISIBLE);
            imgNoInternet.setVisibility(View.GONE);
        } else {
            //cardView.setVisibility(View.GONE);
            imgNoInternet.setVisibility(View.VISIBLE);
        }

        //
        setOnClick();

        AppConstant.showOpenAppAds = false;
        this.tvTitle.setText(R.string.loc_info);
        setInfo();

        if (!checkConnection(getBaseContext())) {
            //findViewById(R.id.frameAds).setVisibility(4);
        }
        AppConstant.trackKing = "IAP_GetlocationAd_Sc_Show";
        if (AppConstant.gotoRemoveAds) {
            AppConstant.gotoRemoveAds = false;
            //Navigation.findNavController(view).navigate(R.id.action_fragmentLocationInfo_to_removeAdFragment);
        }

        //Set map
        Fragment mapFragment = MapFragment.newInstance(Double.valueOf(preferences.getString("latDouble", "21.04088715582817")), Double.valueOf(preferences.getString("lonDouble", "105.76411645538103")));
        getSupportFragmentManager().beginTransaction().replace(R.id.container, mapFragment).commit();

        //Compass
        if (hasSensor()) {
            this.compassDrawer.setVisibility(View.INVISIBLE);
            this.accelerometerView.setVisibility(View.INVISIBLE);
            Glide.with(this).load(Integer.valueOf(R.drawable.ic_compass_new)).into(this.imageCompassNew);
            Glide.with(this).load(Integer.valueOf(R.drawable.ic_azi_new)).into(this.imageAzi);
            sensorListener2 = new SensorListener(getBaseContext());
            this.sensorListener = sensorListener2;
            sensorListener2.setOnValueChangedListener(this);
        }

        //Gesture rotation
        //mRotationDetector = new RotationGestureDetector(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (countLockCompass % 2 == 0) {
            GeomagneticField field = new GeomagneticField(
                    (float) location.getLatitude(),
                    (float) location.getLongitude(),
                    (float) location.getAltitude(),
                    System.currentTimeMillis()
            );

            mDeclination = field.getDeclination();
            this.location = location;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (countLockCompass % 2 == 0) {
            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                SensorManager.getRotationMatrixFromVector(
                        mRotationMatrix, event.values);
                float[] orientation = new float[3];
                SensorManager.getOrientation(mRotationMatrix, orientation);
                float bearing = (float) Math.toDegrees(orientation[0]) + mDeclination;
                updateCamera(bearing);
            }
        }
    }

    private void updateCamera(float bearing) {
        if (countLockCompass % 2 == 0) {
            if (MapFragment.map != null) {
                CameraPosition oldPos = MapFragment.map.getCameraPosition();
                CameraPosition pos = CameraPosition.builder(oldPos).bearing(bearing).build();
                GoogleMap.CancelableCallback callback = new GoogleMap.CancelableCallback() {
                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onCancel() {

                    }
                };
                MapFragment.map.animateCamera(CameraUpdateFactory.newCameraPosition(pos), 45, callback);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /*private Bitmap makeBlackTransparent(Bitmap image) {
        // convert image to matrix
        Mat src = new Mat(image.getWidth(), image.getHeight(), CvType.CV_8UC4);
        Utils.bitmapToMat(image, src);

        // init new matrices
        Mat dst = new Mat(image.getWidth(), image.getHeight(), CvType.CV_8UC4);
        Mat tmp = new Mat(image.getWidth(), image.getHeight(), CvType.CV_8UC4);
        Mat alpha = new Mat(image.getWidth(), image.getHeight(), CvType.CV_8UC4);

        // convert image to grayscale
        Imgproc.cvtColor(src, tmp, Imgproc.COLOR_BGR2GRAY);

        // threshold the image to create alpha channel with complete transparency in black background region and zero transparency in foreground object region.
        Imgproc.threshold(tmp, alpha, 100, 255, Imgproc.THRESH_BINARY);

        // split the original image into three single channel.
        List<Mat> rgb = new ArrayList<Mat>(3);
        Core.split(src, rgb);

        // Create the final result by merging three single channel and alpha(BGRA order)
        List<Mat> rgba = new ArrayList<Mat>(4);
        rgba.add(rgb.get(0));
        rgba.add(rgb.get(1));
        rgba.add(rgb.get(2));
        rgba.add(alpha);
        Core.merge(rgba, dst);

        // convert matrix to output bitmap
        Bitmap output = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(dst, output);
        return output;
    }*/

    private void captureFullMapScreen() {
        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyhhmmss");
        String dateNow = sdf.format(now);
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Download/CompassCapture");
        myDir.mkdirs();

        String fname = dateNow + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            int quality = 100;
            /*bitmapCompassInfo = makeBlackTransparent(getScreenShot(rootView));*/

            combineImages(bitmapMap, bitmapCompassInfo).compress(Bitmap.CompressFormat.PNG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            Toast.makeText(getBaseContext(), file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            Log.d("ImageCapture", "FileNotFoundException");
            Log.d("ImageCapture", e.getMessage());
        } catch (IOException e) {
            Log.d("ImageCapture", "IOException");
            Log.d("ImageCapture", e.getMessage());
        }

    }

    public void captureMapScreen() {
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmapSnapshot) {
                bitmapMap = bitmapSnapshot;
            }
        };
        MapFragment.map.snapshot(callback);
    }

    public Bitmap combineImages(Bitmap c, Bitmap s) {
        Bitmap cs = null;

       /* int width, height = 0;

        if (c.getWidth() > s.getWidth()) {
            width = c.getWidth() + s.getWidth();
        } else {
            width = s.getWidth() + s.getWidth();
        }
        height = c.getHeight();*/

        cs = Bitmap.createBitmap(s.getWidth(), s.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(cs);

        comboImage.drawBitmap(c, 0f, 0f, null);
        comboImage.drawBitmap(s, 0f, 0f, null);
        return cs;
    }

    private void setOnClick() {
        imgCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureMapScreen();
                new Handler().postDelayed(() -> {
                    captureFullMapScreen();
                }, 500);
            }
        });

        imgLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countLockCompass++;
                if (countLockCompass % 2 != 0) {
                    imgLock.setImageResource(R.drawable.ic_unlock_location_info);
                    sensorListener2.stop();
                    sensorListener.stop();
                    MapFragment.mUiSettings.setZoomGesturesEnabled(true);
                    MapFragment.mUiSettings.setScrollGesturesEnabled(true);
                    MapFragment.mUiSettings.setRotateGesturesEnabled(false);
                    MapFragment.mUiSettings.setZoomControlsEnabled(true);
                    Toast.makeText(getBaseContext(), "Lock compass", Toast.LENGTH_SHORT).show();
                } else {
                    imgLock.setImageResource(R.drawable.ic_lock_location_info);
                    sensorListener2.start();
                    sensorListener.start();
                    MapFragment.mUiSettings.setZoomGesturesEnabled(false);
                    MapFragment.mUiSettings.setScrollGesturesEnabled(false);
                    MapFragment.mUiSettings.setRotateGesturesEnabled(false);
                    MapFragment.mUiSettings.setZoomControlsEnabled(false);
                    Toast.makeText(getBaseContext(), "UnLock compass", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCopyAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyText(tvAddress.getText().toString() + tvAddressDetail.getText().toString());
                //Toast
                Toast toast = Toast.makeText(getBaseContext(), "Address copied", Toast.LENGTH_LONG);
                /*View view1 = toast.getView();
                view1.setBackgroundResource(R.drawable.bg_toast);
                TextView text = (TextView) view1.findViewById(android.R.id.message);
                text.setTextColor(Color.parseColor("#EEB850"));*/
                toast.show();
            }
        });
        btnCopyLat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getSharedPreferences("MY_PRE", MODE_PRIVATE);
                copyText(tvLatitude.getText().toString() + preferences.getString("lat", ""));
                //Toast
                Toast toast = Toast.makeText(getBaseContext(), "Latitude copied", Toast.LENGTH_LONG);
                /*View view2 = toast.getView();
                view2.setBackgroundResource(R.drawable.bg_toast);
                TextView text = (TextView) view2.findViewById(android.R.id.message);
                text.setTextColor(Color.parseColor("#0CBDF4"));*/
                toast.show();
            }
        });
        btnCopyLon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences2 = getSharedPreferences("MY_PRE", MODE_PRIVATE);
                copyText(tvLongtitude.getText().toString() + preferences2.getString("lon", ""));
                //Toast
                Toast toast = Toast.makeText(getBaseContext(), "Longitude copied", Toast.LENGTH_LONG);
                /*View view3 = toast.getView();
                view3.setBackgroundResource(R.drawable.bg_toast);
                TextView text = (TextView) view3.findViewById(android.R.id.message);
                text.setTextColor(Color.parseColor("#4BE981"));*/
                toast.show();
            }
        });
        findViewById(R.id.ic_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //
    }

    public void hideStatusAndNavigationBar() {
        getWindow().getDecorView().setSystemUiVisibility(Build.VERSION.SDK_INT >= 19 ? 3334 : 1798);
        getWindow().addFlags(1024);
        getWindow().addFlags(128);
    }

    private void initLayout() {
        textViewBigNumber = this.findViewById(R.id.textView_Big_number);
        textViewBigSW = this.findViewById(R.id.textView_Big_SW);

        imgMyLocation = findViewById(R.id.img_my_location);
        imgCapture = findViewById(R.id.img_capture);
        imgLock = findViewById(R.id.img_lock);

        accelerometerView = this.findViewById(R.id.accelerometer_view);
        compassDrawer = this.findViewById(R.id.compass_drawer);
        imageAzi = this.findViewById(R.id.image_azi);
        imageCompassNew = this.findViewById(R.id.image_compass_new);

        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(LocationInfoActivity.this, MainCompassActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        imgNoInternet = findViewById(R.id.img_no_internet);
        imgNoInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(LocationInfoActivity.this, MainCompassActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        imageHome = findViewById(R.id.imageHome);
        imageHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(LocationInfoActivity.this, MainCompassActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        imageCompass = findViewById(R.id.imageCompass);
        imageCompass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(LocationInfoActivity.this, ChangeCompassActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        imageLocation = findViewById(R.id.imageLocation);
        imageView_Setting = findViewById(R.id.imageView_Setting);
        imageView_Setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(LocationInfoActivity.this, SettingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        btnCopyAddress = findViewById(R.id.button_coppy_address);
        btnCopyLon = findViewById(R.id.coppyLongitude);
        btnCopyLat = findViewById(R.id.coppyLatitude);
        tvAddress = findViewById(R.id.tv_Address);
        tvAddressDetail = findViewById(R.id.tv_address_detail);
        tvLatitude = findViewById(R.id.tv_latitude);
        tvLatitudeDetail = findViewById(R.id.tv_latitude_detail);
        tvLongitudeDetail = findViewById(R.id.tv_longitude_detail);
        tvLongtitude = findViewById(R.id.tv_longtitude);
        tvMagnetic = findViewById(R.id.tv_magnetic);
        tvMagneticDetail = findViewById(R.id.tv_magnetic_detail);
        tvTitle = findViewById(R.id.tv_title);
    }

    @Override
    public void onResume() {
        super.onResume();
        AppConstant.showOpenAppAds = false;
        //Keep screen on
        SharedPreferences preferences = getSharedPreferences("MY_PRE", MODE_PRIVATE);
        boolean screenOn = preferences.getBoolean("screenOn", false);
        if (screenOn == true) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        /*if (sensorManager != null)
            sensorManager.registerListener((SensorEventListener) this,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                    SensorManager.SENSOR_DELAY_GAME);*/
        sensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);

        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);

        if (countLockCompass % 2 != 0) {
            if (MapFragment.mUiSettings != null) {
                imgLock.setImageResource(R.drawable.ic_unlock_location_info);
                sensorListener2.stop();
                sensorListener.stop();
                MapFragment.mUiSettings.setZoomGesturesEnabled(true);
                MapFragment.mUiSettings.setScrollGesturesEnabled(true);
                MapFragment.mUiSettings.setRotateGesturesEnabled(false);
                MapFragment.mUiSettings.setZoomControlsEnabled(true);
            }
        } else {
            if (MapFragment.mUiSettings != null) {
                imgLock.setImageResource(R.drawable.ic_lock_location_info);
                sensorListener2.start();
                sensorListener.start();
                MapFragment.mUiSettings.setZoomGesturesEnabled(false);
                MapFragment.mUiSettings.setScrollGesturesEnabled(false);
                MapFragment.mUiSettings.setRotateGesturesEnabled(false);
                MapFragment.mUiSettings.setZoomControlsEnabled(false);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(LocationInfoActivity.this);

        mSensorManager.unregisterListener(this);
    }

    private void setInfo() {
        SharedPreferences preferences = getSharedPreferences("MY_PRE", MODE_PRIVATE);
        this.tvAddressDetail.setText(preferences.getString("add", ""));
        this.tvLongitudeDetail.setText(preferences.getString("lon", ""));
        this.tvLatitudeDetail.setText(preferences.getString("lat", ""));
        this.tvMagneticDetail.setText(preferences.getString("mag", ""));
    }

    @SuppressLint({"UseRequireInsteadOfGet", "WrongConstant"})
    private void copyText(String str) {
        ((ClipboardManager) ((Context) Objects.requireNonNull(LocationInfoActivity.this)).getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText(null, str));
    }

    private void checkPermissionLocation() {
        new RuntimePermission(LocationInfoActivity.this).request("android.permission.ACCESS_FINE_LOCATION").onAccepted(new AcceptedCallback() {

            @Override
            public final void onAccepted(PermissionResult permissionResult) {
                LocationInfoActivity.this.lambda$checkPermissionLocation$0$FragmentLocationInfo(permissionResult);
            }
        }).onDenied(CustomTwos.INSTANCE).onForeverDenied(CustomFours.INSTANCE).ask();
    }

    public void lambda$checkPermissionLocation$0$FragmentLocationInfo(PermissionResult permissionResult) {
        runToCheckInternet();
    }

    @SuppressLint("MissingPermission")
    private void setLocation() {
        LocationServices.getFusedLocationProviderClient(LocationInfoActivity.this).getLastLocation().addOnSuccessListener(LocationInfoActivity.this, new OnSuccessListener() {

            @Override
            public final void onSuccess(Object obj) {
                LocationInfoActivity.this.lambda$setLocation$3$FragmentLocationInfo((Location) obj);
            }
        });
    }

    public void lambda$setLocation$3$FragmentLocationInfo(Location location) {
        String str;
        String str2;
        String str3;
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            try {
                List<Address> fromLocation = new Geocoder(LocationInfoActivity.this, Locale.getDefault()).getFromLocation(latitude, longitude, 1);
                fromLocation.get(0).getCountryName();
                double round = (double) ((Math.round(latitude * 1000.0d) / 1000) % 1);
                double round2 = (double) ((Math.round(1000.0d * longitude) / 1000) % 1);
                TextView textView = this.tvLatitudeDetail;
                StringBuilder sb = new StringBuilder();
                int i = (int) latitude;
                sb.append(i);
                sb.append("°");
                sb.append(round);
                sb.append("'");
                textView.setText(sb.toString());
                TextView textView2 = this.tvLongitudeDetail;
                StringBuilder sb2 = new StringBuilder();
                int i2 = (int) longitude;
                sb2.append(i2);
                sb2.append("°");
                sb2.append(round2);
                sb2.append("'");
                textView2.setText(sb2.toString());
                Address address = fromLocation.get(0);
                String str4 = "";
                if (address.getThoroughfare() != null) {
                    str = address.getThoroughfare() + ",";
                } else {
                    str = str4;
                }
                StringBuilder sb3 = new StringBuilder();
                sb3.append(str);
                if (address.getSubAdminArea() != null) {
                    str2 = address.getSubAdminArea() + ",";
                } else {
                    str2 = str4;
                }
                sb3.append(str2);
                String sb4 = sb3.toString();
                StringBuilder sb5 = new StringBuilder();
                sb5.append(sb4);
                if (address.getAdminArea() != null) {
                    str3 = address.getAdminArea() + ",";
                } else {
                    str3 = str4;
                }
                sb5.append(str3);
                String sb6 = sb5.toString();
                StringBuilder sb7 = new StringBuilder();
                sb7.append(sb6);
                if (address.getCountryName() != null) {
                    str4 = address.getCountryName() + ",";
                }
                sb7.append(str4);
                String sb8 = sb7.toString();
                this.tvAddressDetail.setText(sb8);
                SharedPreferences.Editor edit = getSharedPreferences("MY_PRE", MODE_PRIVATE).edit();
                edit.putString("add", sb8);
                edit.putString("lat", i + "°" + round + "'");
                edit.putString("lon", i2 + "°" + round2 + "'");
                edit.apply();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void runToCheckInternet() {
        HandlerThread handlerThread = new HandlerThread("check_network");
        handlerThread.start();
        new Handler(handlerThread.getLooper()).post(new Runnable() {

            public final void run() {
                LocationInfoActivity.this.lambda$runToCheckInternet$4$FragmentLocationInfo();
            }
        });
    }

    @SuppressLint("WrongConstant")
    public void lambda$runToCheckInternet$4$FragmentLocationInfo() {
        boolean isOnline2 = isOnline2();
        this.internet_avalible = isOnline2;
        if (!isOnline2) {
            Toast.makeText(getBaseContext(), getString(R.string.turn_on_network), 0).show();
        }
    }

    public boolean checkConnection(Context context) {
        NetworkInfo activeNetworkInfo;
        @SuppressLint("WrongConstant") ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        if (connectivityManager == null || (activeNetworkInfo = connectivityManager.getActiveNetworkInfo()) == null) {
            return false;
        }
        if (activeNetworkInfo.getType() == 1 || activeNetworkInfo.getType() == 0) {
            return true;
        }
        return false;
    }

    private boolean isOnline2() {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("8.8.8.8", 53), ConnectionResult.DRIVE_EXTERNAL_STORAGE_REQUIRED);
            socket.close();
            return true;
        } catch (IOException unused) {
            return false;
        }
    }

    @OnClick({R.id.btn_get_detail})
    public void onViewClicked() {
        if (SystemClock.elapsedRealtime() - MainCompassActivity.lastClickTime >= 1000) {
            MainCompassActivity.lastClickTime = SystemClock.elapsedRealtime();
            showDialogWaitAD();
        }
    }

    private void showDialogWaitAD() {
        RuntimePermission.askPermission(this, new String[0]).request("android.permission.ACCESS_FINE_LOCATION").onAccepted(new AcceptedCallback() {

            @Override
            public final void onAccepted(PermissionResult permissionResult) {
                LocationInfoActivity.this.lambda$showDialogWaitAD$5$FragmentLocationInfo(permissionResult);
            }
        }).onDenied(CustomThrees.INSTANCE).onForeverDenied(CustomOnes.INSTANCE).ask();
    }

    public void lambda$showDialogWaitAD$5$FragmentLocationInfo(PermissionResult permissionResult) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
        View inflate = getLayoutInflater().inflate(R.layout.dialog_wait_ad, (ViewGroup) null);
        builder.setView(inflate).setCancelable(false);
        FadingCircle fadingCircle = new FadingCircle();
        fadingCircle.setColor(Color.parseColor("#E6007E"));
        Glide.with(inflate).load((Drawable) fadingCircle).into((ImageView) inflate.findViewById(R.id.image_loading));
        AlertDialog create = builder.create();
        this.dialog = create;
        ((Window) Objects.requireNonNull(create.getWindow())).setBackgroundDrawable(new ColorDrawable(0));
        this.dialog.show();
        setLocation();
        showAds();
    }

    private void showAds() {
        LocationInfoActivity.this.dialog.dismiss();
        LocationInfoActivity.this.checkPermissionLocation();
    }

    @Override
    public void onMagneticFieldChanged(float f) {
        this.compassDrawer.getSensorValue().setMagneticField(f);
    }

    @Override
    public void onRotationChanged(float f, float f2, float f3) {
        this.compassDrawer.getSensorValue().setRotation(f, f2, f3);
        this.accelerometerView.getSensorValue().setRotation(f, f2, f3);
        this.textViewBigNumber.setText(this.compassDrawer.getSW_Value()[0]);
        this.textViewBigSW.setText(this.compassDrawer.getSW_Value()[1]);
        this.tvMagneticDetail.setText(this.compassDrawer.getMagneticValue());
        imageRotate(-f);
    }

    private void imageRotate(float f) {
        this.imageCompassNew.animate().rotation(f).setDuration(0).setInterpolator(new LinearInterpolator()).setListener(new Animator.AnimatorListener() {

            public void onAnimationCancel(Animator animator) {
            }

            public void onAnimationEnd(Animator animator) {
            }

            public void onAnimationRepeat(Animator animator) {
            }

            public void onAnimationStart(Animator animator) {
            }
        }).start();
        /*findViewById(R.id.container).animate().rotation(f).setDuration(0).setInterpolator(new LinearInterpolator()).setListener(new Animator.AnimatorListener() {

            public void onAnimationCancel(Animator animator) {
            }

            public void onAnimationEnd(Animator animator) {
            }

            public void onAnimationRepeat(Animator animator) {
            }

            public void onAnimationStart(Animator animator) {
            }
        }).start();*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        SensorListener sensorListener2 = this.sensorListener;
        if (sensorListener2 != null) {
            sensorListener2.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SensorListener sensorListener2 = this.sensorListener;
        if (sensorListener2 != null) {
            sensorListener2.stop();
        }
    }

    private boolean hasSensor() {
        return SensorUtil.hasAccelerometer(getBaseContext()) && SensorUtil.hasMagnetometer(getBaseContext());
    }

    /*@Override
    public boolean onTouchEvent(MotionEvent event) {
        mRotationDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public void OnRotation(RotationGestureDetector rotationDetector) {
        float angle = rotationDetector.getAngle();
        Log.d("RotationGestureDetector", "Rotation: " + Float.toString(angle));
        imageCompassNew.setRotation(imageCompassNew.getRotation() + (-angle));
        imgCapture.setRotation(imgCapture.getRotation() + (-angle));
    }*/
}