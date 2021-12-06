package com.example.hawkeyeconfigure;

import android.Manifest;
import android.animation.LayoutTransition;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;


public class Step2 extends Fragment {
    View Fragview;
    Spinner mdumode,sensor;
    TextInputEditText ssid,password;
    //Button next,prev;
    ImageButton qrcode,result;
    MainActivity main;
    ProgressBar connection,configure;
    ImageView connectionstatus,configurestatus;
    boolean communicating=false;
    ImageButton refresh;
    ConstraintLayout layout;

    public Step2(MainActivity main) {
        this.main=main;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(Fragview==null) {
            Fragview = inflater.inflate(R.layout.step2, container, false);
            layout=Fragview.findViewById(R.id.layout);
            ViewGroup viewGroup = (ViewGroup)Fragview.findViewById(R.id.ll);
            viewGroup.getLayoutTransition()
                    .enableTransitionType(LayoutTransition.CHANGING);
            layout.setVisibility(View.GONE);
            main.connectMacNearby();
            main.prev.setEnabled(false);
            main.next.setEnabled(false);
            connection = Fragview.findViewById(R.id.connection);
            connectionstatus = Fragview.findViewById(R.id.connectionstatus);
            configure = Fragview.findViewById(R.id.configure);
            configurestatus = Fragview.findViewById(R.id.configurestatus);
            refresh = Fragview.findViewById(R.id.refresh);
            refresh.setVisibility(View.GONE);
        }
        return Fragview;
    }

    public void NearByConnected()
    {
        connection.setVisibility(View.INVISIBLE);
        connectionstatus.setVisibility(View.VISIBLE);
        layout.setVisibility(View.VISIBLE);
        connectionstatus.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.tick2));
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        main.sendAllSetting();
                    }
                });
                //Thread.sleep(500);
            }
        }).start();

    }

    public void vibrate(int millis)
    {
        Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(millis, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(millis);
        }
    }

    public void onPayloadSent()
    {
        configure.setVisibility(View.INVISIBLE);
        vibrate(1000);
        configurestatus.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.tick2));
        configurestatus.setVisibility(View.VISIBLE);
        main.prev.setEnabled(true);
        main.next.setEnabled(true);

    }


    public void onMearbyError()
    {
        connection.setVisibility(View.INVISIBLE);
        connectionstatus.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.cross2));
        connectionstatus.setVisibility(View.VISIBLE);
        connectionstatus.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        configure.setVisibility(View.INVISIBLE);
        configurestatus.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.cross2));
        configurestatus.setVisibility(View.VISIBLE);
        configurestatus.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        main.prev.setEnabled(true);
        refresh.setVisibility(View.VISIBLE);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                main.connectMacNearby();
                configure.setVisibility(View.VISIBLE);
                configurestatus.setVisibility(View.INVISIBLE);
                connection.setVisibility(View.VISIBLE);
                connectionstatus.setVisibility(View.INVISIBLE);
                refresh.setVisibility(View.GONE);
            }
        });
    }



}
