package com.example.hawkeyeconfigure;

import androidx.fragment.app.FragmentManager;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import sql_classes.ColumnWiseResultHashMap;
import sql_classes.SQLQueryResult;
import sql_classes.SQLService;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;



import com.shuhart.stepview.StepView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SQLService.Listener {

    StepView stepView;
    FrameLayout frame;
    Step1 step1;
    Step2 step2;
    Step3 step3;
    Spinner mdumode;
    int steppos=1;
    Button prev,next;
    public static String SERVICE_UUID;
    private NearbyDsvManager dsvManager;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean connected = false;
    public String MacQrCode;
    String MacAdd,ServerConnMode;
    private static SharedPreferences sharedPref;
    int completedstep=0;
    done done;
    public String IP,DBName,Username, DBPassword,APNName,APNPassword,IPMode;
    SQLService sqlService;
    boolean sqlServiceBound = false;
    int REQUEST_APN_SETTINGS = 3;

    public static final int REQUEST_LOCATION_PERMISSION = 99;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if(EasyPermissions.hasPermissions(this, perms)) {
            //Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
            fireGPSIntent();
        }
        else {
            EasyPermissions.requestPermissions(this, "Please grant the location permission", REQUEST_LOCATION_PERMISSION, perms);
        }
    }



    public void fireGPSIntent()
    {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if( !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
            new AlertDialog.Builder(this)
                    .setTitle("GPS Settings")  // GPS not found
                    .setMessage("Turn On GPS for app to work ") // Want to enable?
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),REQUEST_LOCATION_PERMISSION);
                        }
                    })
                    .setCancelable(false)
                    //.setNegativeButton(R.string.no, null)
                    .show();
        }
    }



    public static Boolean isLocationEnabled(Context context)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
// This is new method provided in API 28
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return lm.isLocationEnabled();
        } else {
// This is Deprecated in API 28
            int mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF);
            return  (mode != Settings.Secure.LOCATION_MODE_OFF);

        }
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
            sqlService.setListener(MainActivity.this,MainActivity.this);
            Log.e("Service", "Bound");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            sqlServiceBound = false;
            Log.e("Service", "UnBound");
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);



        stepView=findViewById(R.id.step_view);
        frame=findViewById(R.id.frame);
        mdumode=findViewById(R.id.mdumode);
        next=findViewById(R.id.next);
        prev=findViewById(R.id.prev);

        requestLocationPermission();

        ColumnWiseResultHashMap validColumns = (ColumnWiseResultHashMap) getIntent().getSerializableExtra("columns");
        IP = validColumns.getColumnValue("IP",0);
        DBName = validColumns.getColumnValue("DBName",0);
        Username = validColumns.getColumnValue("Username",0);
        DBPassword = validColumns.getColumnValue("DBPassword",0);
        APNName=validColumns.getColumnValue("APNName",0);
        APNPassword = validColumns.getColumnValue("APNPassword",0);
        IPMode=validColumns.getColumnValue("IPMode",0);


        Intent sqlintent = new Intent(getApplicationContext(), SQLService.class);
        bindService(sqlintent, sqlConnection, Context.BIND_AUTO_CREATE);



        List<String> list = new ArrayList<>();
        list.add("Step 1");
        list.add("Step 2");
        list.add("Step 3");

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        step1 = new Step1(this);
        step2=new Step2(this);
        step3=new Step3(this);
        done=new done(this);
        t.replace(R.id.frame,step1);
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        t.commit();
        prev.setVisibility(View.GONE);
        next.setVisibility(View.VISIBLE);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNextClick();
            }
        });


        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPreviousClick();
            }
        });


        stepView.setSteps(list);


    }

    private String dateKey() {

        String hkey = "-727595";
        //String year  = String.valueOf(Calendar.get(Calendar.YEAR));
        //String day = String.valueOf(rtcCalender.get(Calendar.DAY_OF_YEAR));
        //String key = ;
        //String key = day + year;
        return hkey;
    }


    int sendCount = 0;
    public void connectMacNearby()
    {

        Toast.makeText(getApplicationContext(),SERVICE_UUID,Toast.LENGTH_LONG).show();
        dsvManager = new NearbyDsvManager(new EventListener()  {
            @Override
            public void onDiscovered() {
                Log.e("Nearby Main","Discovering");

            }

            @Override
            public void onDisconnected() {
                Log.e("NEarby Main","Disconnected");
                connected = false;
            }

            @Override
            public void startDiscovering() {
                Log.e("NEarby Main","Strted Disc");
                checkConnection();
            }

            @Override
            public void onConnected() {
                Log.e("NEarby Main","Con Done");
                sendCount=0;
                connected = true;
                step2.NearByConnected();
            }

            @Override
            public void onError() {
                //step2.onMearbyError();
            }

            @Override
            public void onPayloadSuccess() {
                Log.e("NEarby Main","Payload success");
                sendCount++;
                if(sendCount==4) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dsvManager.endConnection();
                            step2.onPayloadSent();
                            stepView.go(2, true);
                            completedstep = 2;
                        }
                    }, 2000);
                }
                else
                {
                    step2.NearByConnected();
                }

            }



            @Override
            public void onPayloadFailure() {
                //dsvManager.endConnection();
                Log.e("NEarby Main","Payload success");
            }
        }, getApplicationContext());
    }

    public void openAPNSettings()
    {
        Intent intent = new Intent(this,APNSettings.class);
        intent.putExtra("APNName",APNName);
        intent.putExtra("APNPassword",APNPassword);
        startActivityForResult(intent,REQUEST_APN_SETTINGS);
    }

    public void checkConnection()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int t=0;
                sqlService.stopDBCheck();
                while (true)
                {
                    Log.i("NEarby Main","checking connection "+String.valueOf(connected));
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    t+=100;
                    if(connected)
                    { Log.w("NEarby Main","Connection Sucess");
                        break; }
                    if(t==60000&&!connected)
                    {
                        //Log.i("NEarby Main","Connection timeout");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                step2.onMearbyError();
                                dsvManager.endConnection();

                            }
                        });
                        Log.w("NEarby Main","Ending connection");
                        break;
                    }

                }
                sqlService.startDBCHeck();
            }
        }).start();
    }



    public void sendAllSetting()
    {
        //sharedPref = getSharedPreferences(getResources().getString(R.string.dbcache), MODE_PRIVATE);
        String dbSettings = IP+","+DBName+","+Username+","+ DBPassword +","+IPMode;
        String wifiSettings = step1.ssid.getText().toString()+","+step1.password.getText().toString();//"PuneHouseWifi,RaveHouseC5/3";
        //String wifiSettings = "PuneHouseWifi,RaveHouseC5/3";
        String data="$DBSet="+dbSettings+"#$APNSet="+wifiSettings+"#"+"$ModeSet="+step1.mdumodeno+"#"+"$SensorSet="+step1.sensormode+"#";
        Log.w("SendDATA",data);
        dsvManager.sendData(data);
    }


    public void ScannedQrCode(String contents)
    {
        Log.e("Activity Result",contents);
        MacQrCode=contents;
        if(contents.contains("MDUMAC=%&") && contents.contains("#")) {
            int start = contents.indexOf("MDUMAC=%&") + 9;
            int end = contents.indexOf("#", start);
            final String content=contents.substring(start,end);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MacAdd=content;
                }
            });
        }
        if(contents.contains("SERCON=%&") && contents.contains("#")) {
            int start = contents.indexOf("SERCON=%&") + 9;
            int end = contents.indexOf("#", start);
            final String content=contents.substring(start,end);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ServerConnMode=content;
                }
            });
        }
        if(contents.contains("MDUQRID=%&") && contents.contains("#")) {
            int start = contents.indexOf("MDUQRID=%&")+10;
            int end = contents.indexOf("#",start);
            String content=contents.substring(start,end);
            SERVICE_UUID = content;
            SERVICE_UUID = SERVICE_UUID + dateKey();

        }
    }


    public void onNextClick()
    {
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        if(steppos==1)
        {
            if(step1.CheckData()) {
                t.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                step2=new Step2(this);
                t.replace(R.id.frame, step2);
                //t.show(step2);
                //t.hide(step1);
                t.addToBackStack(null);
                t.commit();
                prev.setVisibility(View.VISIBLE);
                steppos++;
                if(completedstep==0)
                {
                    //stepView.done(true);
                    stepView.go(1,true);
                    completedstep=1;
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Invalid Entries",Toast.LENGTH_SHORT).show();
            }
        }
        else if(steppos==2)
        {
            next.setText("Done");
            t.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
            t.replace(R.id.frame, step3);
            //t.show(step2);
            //t.hide(step1);
            t.addToBackStack(null);
            t.commit();
            prev.setVisibility(View.VISIBLE);
            steppos++;

        }
        else if(steppos==3)
        {
            step3.onDoneClick();

        }
    }




    public void Send_Query(final String query, final String labelName, final int FragID, final String type) {
        if(sqlServiceBound)
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sqlService.execSql(query,FragID,labelName,type);
                }
            }).start();
        }
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


    public void onPreviousClick()
    {
        Log.w("changefrag","prevclick");
        if(steppos==2)
        {
            Log.w("changefrag","prevclick");
            FragmentTransaction t = getSupportFragmentManager().beginTransaction();
            t.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            t.replace(R.id.frame,step1);
            //getSupportFragmentManager().popBackStack();
            t.commit();
            prev.setVisibility(View.GONE);
            next.setEnabled(true);
            steppos--;
            stepView.go(0,true);
            completedstep=0;
        }
        if(steppos==3)
        {
            FragmentTransaction t = getSupportFragmentManager().beginTransaction();
            t.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
            t.replace(R.id.frame,step2);
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            //getSupportFragmentManager().popBackStack();
            t.commit();
            //prev.setVisibility(View.GONE);
            next.setText("Next");
            steppos--;
        }
    }

    @Override
    public void OnDBResult(final ColumnWiseResultHashMap columns, int requestcode, final SQLQueryResult sqlQueryResult, final String labelname, String type) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {


                if(sqlQueryResult.getErrorCode().get()==0)
                {
                    if(labelname.equals("Submit")) {
                        Toast.makeText(getApplicationContext(), "Device Configured", Toast.LENGTH_SHORT).show();

                        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
                        t.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                        t.replace(R.id.frame, done);
                        t.commit();
                        stepView.done(true);

                    }
                    else
                    {
                        if(labelname.equalsIgnoreCase("MacName")) {
                            popHashListDialog2(columns, labelname);
                            step3.macname.removeLoading();
                        }
                        else if(labelname.equalsIgnoreCase("ProductionCenter"))
                        {
                            popHashListDialog2(columns, labelname);
                            step3.productioncenter.removeLoading();
                        }
                    }

                }
                else
                {
                    prev.setEnabled(true);
                    step3.progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==111&&resultCode==RESULT_OK)
        {
            String label = data.getStringExtra("label");
            ColumnWiseResultHashMap columnWiseResultHashMap = (ColumnWiseResultHashMap) data.getSerializableExtra("data");
            if(label.equalsIgnoreCase("MacName"))
            {
                step3.macname.setText(columnWiseResultHashMap.getColumnValue(label,0),true);
                step3.macname.removeLoading();

            }
            else
            {
                step3.productioncenter.setText(columnWiseResultHashMap.getColumnValue(label,0),true);
                step3.productioncenter.removeLoading();

            }
        }
        if(REQUEST_APN_SETTINGS==requestCode)
        {
            if(resultCode==RESULT_OK)
            {
                APNName=data.getStringExtra("APNName");
                APNPassword = data.getStringExtra("APNPassword");
                step1.ssid.setText(APNName);
                step1.password.setText(APNPassword);
            }
        }
        if(requestCode==REQUEST_LOCATION_PERMISSION)
        {
            if(isLocationEnabled(this))
                {
                    Toast.makeText(getApplicationContext(),"GPS Mandatory",Toast.LENGTH_SHORT).show();
                    fireGPSIntent();
                }
        }
    }

    void popHashListDialog2(final ColumnWiseResultHashMap columns, String label)
    {

            Intent intent = new Intent(MainActivity.this, HashGridList.class);
            intent.putExtra("data",(Serializable) columns);
            intent.putExtra("label",label);
            startActivityForResult(intent, 111);


    }

    public void addNewDev()
    {
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        step1 = new Step1(MainActivity.this);
        step2=new Step2(MainActivity.this);
        step3=new Step3(MainActivity.this);
        t.replace(R.id.frame,step1);
        t.commit();
        prev.setVisibility(View.GONE);
        next.setVisibility(View.VISIBLE);
        next.setText("Next");
        steppos=1;
        completedstep=0;
        stepView.go(0,false);

    }

    @Override
    public void DBCheck(boolean connected) {

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
