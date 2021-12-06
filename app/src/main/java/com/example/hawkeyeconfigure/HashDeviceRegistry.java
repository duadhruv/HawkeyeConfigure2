package com.example.hawkeyeconfigure;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import sql_classes.ColumnWiseResultHashMap;
import sql_classes.SQLQueryResult;
import sql_classes.SQLService;

public class HashDeviceRegistry extends AppCompatActivity implements SQLService.Listener{

    SQLService sqlService;
    boolean sqlServiceBound = false;
    AlertDialog alertDialog;
    ProgressDialog progressDialog;
    private SharedPreferences sharedPref;

    SurfaceView cameraView;
    BarcodeDetector barcode;
    CameraSource cameraSource;
    SurfaceHolder holder;
    String label;
    boolean qrcodescanned = false;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        cameraView = (SurfaceView) findViewById(R.id.cameraView);
        cameraView.setZOrderMediaOverlay(true);
        holder = cameraView.getHolder();
        label = getIntent().getStringExtra("label");

        progressDialog=new ProgressDialog(HashDeviceRegistry.this);
        progressDialog.setCancelable(false);


        Intent sqlintent = new Intent(getApplicationContext(), SQLService.class);
        bindService(sqlintent, sqlConnection, Context.BIND_AUTO_CREATE);







        AlertDialog.Builder builder = new AlertDialog.Builder(HashDeviceRegistry.this);
        builder.setPositiveButton("OK",null);
        alertDialog = builder.create();
        alertDialog.setCancelable(false);



        sharedPref = getSharedPreferences(getResources().getString(R.string.dbcache), MODE_PRIVATE);

    }

    public void initCameraScanner()
    {
        qrcodescanned=false;
        barcode = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        if(!barcode.isOperational()){
            Toast.makeText(getApplicationContext(), "Sorry, Couldn't setup the detector", Toast.LENGTH_LONG).show();
            this.finish();
        }

        cameraSource = new CameraSource.Builder(this, barcode)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(10)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1920,1024)
                .build();
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try{
                    if(ContextCompat.checkSelfPermission(HashDeviceRegistry.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                        cameraSource.start(cameraView.getHolder());
                    }
                }
                catch (IOException e){
                    e.printStackTrace();
                }
                catch (OutOfMemoryError e)
                {
                    Toast.makeText(getApplicationContext(),"Please try again",Toast.LENGTH_SHORT).show();
                    finish();
                }


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });


        barcode.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes =  detections.getDetectedItems();
                if(barcodes.size() > 0){
                    if(!qrcodescanned) {
                        handleResult(String.valueOf(barcodes.valueAt(0).rawValue));
                        qrcodescanned=true;
                    }
                }
            }
        });
    }






    private ServiceConnection sqlConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            SQLService.SqlServiceBinder binder = (SQLService.SqlServiceBinder) service;
            sqlService = binder.getService();
            sqlServiceBound = true;
            sqlService.setListener(HashDeviceRegistry.this,HashDeviceRegistry.this);
            Log.e("Service", "Bound to history activity");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            sqlServiceBound = false;
            Log.e("Service", "UnBound");
        }
    };


    public void vibrate(int millis)
    {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(millis, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(millis);
        }
    }


    public void handleResult(String result) {
        String string = result;
        vibrate(500);
        //cameraSource.stop();
        Log.w("decoder",string);
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                progressDialog.setMessage("Loading...");
//                progressDialog.show();
//            }
//        });


        try {
            byte[] data = Base64.decode(string, Base64.DEFAULT);
            String text = new String(data, "UTF-8");
            String[] args = null;
            Log.w("decoder",text);
            if(text.contains(";"))
                {
                args=text.split(";");
                if(args.length==4)
                {
                    setData(args);
                }
                else
                {
                    showError();
                }
            }
            else
            {
                showError();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            showError();
        }
        catch (IllegalArgumentException i )
        {
            i.printStackTrace();
            showError();
        }


    }

    public void showError()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.setMessage("Incorrect QrCode");
                alertDialog.setTitle("Error");
                //alertDialog.setIcon(R.drawable.error_32);
                alertDialog.show();
                Button ok = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.cancel();
                        initCameraScanner();
                    }
                });
            }
        });





    }


    public void setData(String[] args)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.setMessage("Loading...");
                progressDialog.show();
            }
        });


        DBSettings.setOrgDB(getApplicationContext(),args[2],args[3],args[1],args[0]);



        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.cancel();
            }
        });


        //finish();
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), Splash_Screen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }




    @Override
    public void OnDBResult(ColumnWiseResultHashMap columns, int requestcode, SQLQueryResult sqlQueryResult, String labelname, String type) {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void DBCheck(boolean connected) {
        //setDbConnection(connected);
    }

    @Override
    protected void onPause() {
        Log.e("activity","on Pause");
        if(sqlServiceBound) {
            sqlService.stopDBCheck();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.e("activity","on Resume");
        if(!sqlServiceBound)
        {
            Intent sqlintent = new Intent(getApplicationContext(), SQLService.class);
            bindService(sqlintent, sqlConnection, Context.BIND_AUTO_CREATE);
        }
        if(sqlServiceBound) {
            sqlService.startDBCHeck();
        }

        initCameraScanner();




        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.e("activity","on Stop");
        if (sqlServiceBound) {
            unbindService(sqlConnection);
            sqlServiceBound = false;
        }
        super.onStop();
    }
}
