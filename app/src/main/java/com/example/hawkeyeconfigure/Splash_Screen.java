package com.example.hawkeyeconfigure;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sql_classes.ColumnWiseResultHashMap;
import sql_classes.DBSettingsDialog;
import sql_classes.SQLQueryResult;
import sql_classes.SQLService;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class Splash_Screen extends AppCompatActivity implements SQLService.Listener{


    private static final int REQUEST_CAMERA_PERMISSION = 111 ;
    int REQUEST_DEVICE_VALIDATION=999;
    AlertDialog alertDialog;
    Button retry;
    ProgressBar progressBar;
    TextView valid;
    private static SharedPreferences sharedPref;
    boolean startLogo=false;
    public boolean developer = false;
    int clickcount=0;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK)
        {
            if(requestCode==10)
            {

                logo.startAnimation(animation1);


                ShowValidating();

            }
        }
    }
    ImageView logo;
    Animation animation1;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = Splash_Screen.this.getWindow();
        window.setNavigationBarColor(getResources().getColor(android.R.color.black));
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(Splash_Screen.this,android.R.color.black));
        setContentView(R.layout.activity_splash_screen);
        valid=findViewById(R.id.valid);
        progressBar=findViewById(R.id.progress);
        logo = findViewById(R.id.logo);
        final ImageView logo2 = findViewById(R.id.logo2);
        retry = findViewById(R.id.retry);


        Intent sqlintent = new Intent(getApplicationContext(), SQLService.class);
        bindService(sqlintent, sqlConnection, Context.BIND_AUTO_CREATE);









//        Intent intent = new Intent(this, DBSettingsDialog.class);
//        startActivityForResult(intent,10);



        AlertDialog.Builder builder = new AlertDialog.Builder(Splash_Screen.this);
        builder.setPositiveButton("OK",null);
        alertDialog = builder.create();





        animation1 = AnimationUtils.loadAnimation(getBaseContext(),R.anim.faderotate);
        final Animation animation2 = AnimationUtils.loadAnimation(getBaseContext(),R.anim.zoomin);
        final Animation animation3 = AnimationUtils.loadAnimation(getBaseContext(),R.anim.zoomout);
        animation3.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fireValidation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animation2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                logo2.startAnimation(animation3);


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animation1.setAnimationListener(new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            logo2.startAnimation(animation2);
        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    });


        if(!DBSettings.getOrgServer(getApplicationContext()).equalsIgnoreCase("0.0.0.0")) {
            logo.startAnimation(animation1);
            ShowValidating();
        }
        else
        {
            openRegistry();
        }




        ImageButton settings = findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRegistry();
            }
        });


    }


    public void openRegistry()
    {
        Dexter.withContext(Splash_Screen.this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Log.w("PermissionListener","Granted");
                        Intent intent = new Intent(Splash_Screen.this,HashDeviceRegistry.class);
                        startActivity(intent);

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Log.w("PermissionListener","Denied");
                        finish();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        Log.w("PermissionListener","Ratinale");
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }


    private int SQLSETTINGS_RESULT_CODE = 17;


    public void fireValidation()
    {
        if(sqlServiceBound)
        //HawkeyeV2_Proc_Machine_MDUServer_Validation
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Send_Query("HawkeyeV2_Proc_Machine_MDUServer_Validation","Validation",1,"SPV");
                }
            }).start();
        }
    }

    public void ShowValidating()
    {
        valid.setText("Connecting to server...");
        retry.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        valid.setVisibility(View.VISIBLE);
    }

    public void removeValidating()
    {
        progressBar.setVisibility(View.INVISIBLE);
        valid.setVisibility(View.INVISIBLE);
    }



    SQLService sqlService;
    boolean sqlServiceBound = false;





    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startMain(ColumnWiseResultHashMap columns)
    {
       runOnUiThread(new Runnable() {
           @Override
           public void run() {
               valid.setText("Validating device...");
               Intent i = new Intent(getApplicationContext(),MainActivity.class);
               startActivity(i);

           }
       });
        }

    private ServiceConnection sqlConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            SQLService.SqlServiceBinder binder = (SQLService.SqlServiceBinder) service;
            sqlService = binder.getService();
            sqlServiceBound = true;
            sqlService.startDBCHeck();
            sqlService.setListener(Splash_Screen.this,Splash_Screen.this);
            Log.e("Service", "Bound");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            sqlServiceBound = false;
            Log.e("Service", "UnBound");
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void OnDBResult(ColumnWiseResultHashMap columns, int requestcode, SQLQueryResult sqlQueryResult, String labelname, String type) {
        if(requestcode==1)
        {
            if(sqlQueryResult.getErrorCode().get()==0)
            {
                String IP,DBName,Username,ServerPassword,APNName,APNPassword;


                Intent intent = new Intent(this,MainActivity.class);
                intent.putExtra("columns",columns);
                startActivity(intent);
            }
            else if(sqlQueryResult.getErrorCode().get()==3)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        alertDialog.setMessage("Invalid Hawkeye Database!!");
                        alertDialog.show();
                        removeValidating();
                        retry.setVisibility(View.VISIBLE);
                        retry.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ShowValidating();
                                fireValidation();
                            }
                        });
                    }
                });
            }
            else
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        alertDialog.setMessage("Unable to connect to server!!");
                        alertDialog.show();
                        removeValidating();
                        retry.setVisibility(View.VISIBLE);
                        retry.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ShowValidating();
                                fireValidation();
                            }
                        });
                    }
                });
            }
        }

    }

    @Override
    public void DBCheck(boolean connected) {

    }



    int REQUEST_GENRATE_STOREDPROC = 767;



    public void Send_Query(final String query, final String labelName, final int FragID, final String type) {
        if(sqlServiceBound)
        {
            sqlService.execSql(query,FragID,labelName,type); }
        else
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(sqlServiceBound)
                    { sqlService.execSql(query,FragID,labelName,type); }

                }
            }).start();
        }
    }







    public String getMacAdress()
    {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }

                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
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
