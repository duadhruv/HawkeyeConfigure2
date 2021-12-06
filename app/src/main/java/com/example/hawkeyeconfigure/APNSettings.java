package com.example.hawkeyeconfigure;

import androidx.appcompat.app.AppCompatActivity;
import sql_classes.ColumnWiseResultHashMap;
import sql_classes.SQLQueryResult;
import sql_classes.SQLService;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class APNSettings extends AppCompatActivity implements SQLService.Listener{

    HashTextView apnname,apnpassword;
    Button submit;
    SQLService sqlService;
    boolean sqlServiceBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_apnsettings);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Intent sqlintent = new Intent(getApplicationContext(), SQLService.class);
        bindService(sqlintent, sqlConnection, Context.BIND_AUTO_CREATE);

        apnname=findViewById(R.id.APNName);
        apnpassword=findViewById(R.id.APNPassword);
        submit=findViewById(R.id.submit);

        apnname.setText(getIntent().getStringExtra("APNName"),true);
        apnpassword.setText(getIntent().getStringExtra("APNPassword"),true);



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = true;
                if(apnname.getValue().length()==0)
                {
                    valid=false;
                    apnname.setError("Empty Field");
                }
                else
                {
                    apnname.removeError();
                }

                if(apnpassword.getValue().length()==0)
                {
                    valid=false;
                    apnpassword.setError("Empty Field");
                }
                else
                {
                    apnpassword.removeError();
                }

                if(valid)
                {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String query = String.format("HawkeyeV2_Proc_Machine_MDUServer_APNSetup @APNName='%s' , @APNPassword = '%s'",apnname.getValue().trim(),apnpassword.getValue().trim());
                            sqlService.execSql(query,0,"Edit","Edit");
                        }
                    }).start();
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
            sqlService.setListener(APNSettings.this,APNSettings.this);
            Log.e("Service", "Bound to history activity");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            sqlServiceBound = false;
            Log.e("Service", "UnBound");
        }
    };

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

    @Override
    public void OnDBResult(ColumnWiseResultHashMap columnWiseResultHashMap, int requestcode, SQLQueryResult sqlQueryResult, String labelname, String type) {
        if(requestcode==0)
        {
            if(sqlQueryResult.getErrorCode().get()==0)
            {
                if(columnWiseResultHashMap.getCommand())
                {
                    Intent intent = new Intent();
                    intent.putExtra("APNName",columnWiseResultHashMap.getColumnValue("APNName",0));
                    intent.putExtra("APNPassword",columnWiseResultHashMap.getColumnValue("APNPassword",0));
                    setResult(RESULT_OK,intent);
                    finish();
                }
            }
            else
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"ERROR",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    @Override
    public void DBCheck(boolean connected) {

    }
}
