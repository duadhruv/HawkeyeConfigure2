package com.example.hawkeyeconfigure;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;


public class Step1 extends Fragment {
    View Fragview;
    Spinner mdumode,sensor;
    TextInputEditText ssid,password;
    //Button next;
    ImageButton qrcode, qrcoderesult;
    MainActivity main;
    boolean qrcodescanned;
    TextInputLayout ssidlayout,passwordlayout;
    private static SharedPreferences sharedPref;
    String mdumodeno,sensormode;
    TextView apnsettings;
    int REQUEST_APN_SETTINGS = 0;

    public Step1(MainActivity main) {
        this.main=main;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(Fragview==null) {
            Fragview = inflater.inflate(R.layout.step1, container, false);

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.mdu_modes_string, R.layout.spinner);
            adapter.setDropDownViewResource(R.layout.drop_box);

            ssid = Fragview.findViewById(R.id.ssid);
            password = Fragview.findViewById(R.id.password);

            qrcoderesult = Fragview.findViewById(R.id.result);
            qrcode = Fragview.findViewById(R.id.qrcode);
            qrcoderesult.setVisibility(View.GONE);
            ssidlayout = Fragview.findViewById(R.id.ssidlayout);
            passwordlayout = Fragview.findViewById(R.id.passwordlayout);
            apnsettings = Fragview.findViewById(R.id.changesettings);

            //main.next.setEnabled(false);

            ssid.clearFocus();
            password.clearFocus();
            mdumode = Fragview.findViewById(R.id.mdumode);
            mdumode.setAdapter(adapter);

            ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(),
                    R.array.sensor_modes, R.layout.spinner);
            adapter2.setDropDownViewResource(R.layout.drop_box);

            sensor = Fragview.findViewById(R.id.sensor);
            sensor.setAdapter(adapter2);



            sharedPref = getActivity().getSharedPreferences(getResources().getString(R.string.dbcache), MODE_PRIVATE);

            ssid.setText(((MainActivity)getActivity()).APNName);
            password.setText(((MainActivity)getActivity()).APNPassword);
            mdumode.setSelection(sharedPref.getInt("mdumode",0));


            apnsettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    main.openAPNSettings();
                }
            });


            mdumode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String[] arr = getResources().getStringArray(R.array.mdu_modes_values);
                    LinearLayout linearLayout = Fragview.findViewById(R.id.linearLayout4);
                    if(mdumode.getSelectedItemPosition()==0)
                    {
                        //sensor.setEnabled(false);
                        sensor.setSelection(0);
                        //sensor.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.GONE);
                    }
                    else
                    {
                        linearLayout.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });



            qrcode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED) {

                        Intent intent = new Intent(getActivity(), ScanActivity.class);
                        startActivityForResult(intent, 999);

                    } else {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 111);
                    }
                }
            });

            qrcoderesult.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED) {

                        Intent intent = new Intent(getActivity(), ScanActivity.class);
                        startActivityForResult(intent, 999);

                    } else {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 111);
                    }
                }
            });
        }
        return Fragview;
    }




    public boolean CheckData()
    {
        boolean valid=true;
        Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
        //mdumodeno=mdumode.getSelectedItem().toString();
        String[] arr = getResources().getStringArray(R.array.mdu_modes_values);
        mdumodeno=arr[mdumode.getSelectedItemPosition()];
        sensormode=sensor.getSelectedItem().toString();
        if(ssid.length()==0)
        {
            valid=false;
            ssidlayout.setError("Empty Field");
            ssidlayout.startAnimation(shake);

        }
        else
        {
            ssidlayout.setErrorEnabled(false);
        }
        if(password.length()==0)
        {
            valid=false;
            passwordlayout.setError("Empty Field");
            passwordlayout.startAnimation(shake);
        }else
        {
            passwordlayout.setErrorEnabled(false);
        }
        if(!qrcodescanned)
        {
            valid=false;
            qrcode.startAnimation(shake);
            qrcoderesult.setVisibility(View.VISIBLE);
            qrcoderesult.setImageDrawable(getResources().getDrawable(R.drawable.cross));

        }

        if (valid)
        {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("ssid",ssid.getText().toString());
            editor.putString("wifipass",password.getText().toString());
            editor.putInt("mdumode",mdumode.getSelectedItemPosition());

            editor.commit();
        }
        return valid;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==999&&resultCode==RESULT_OK)
        {
            String qr = data.getStringExtra("data");
            //Toast.makeText(getContext(),qr,Toast.LENGTH_LONG).show();
            if(qr.contains("MDUQRID")&&qr.contains("SERCON")&&qr.contains("MDUMAC")&&qr.contains("JOBRUNNING"))
            {

                if(qr.contains("JOBRUNNING=%&1"))
                {
                    final androidx.appcompat.app.AlertDialog verifyalert;
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
                    builder.setPositiveButton("OK",null);
                    verifyalert=builder.create();
                    verifyalert.setOnShowListener( new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface arg0) {
                            verifyalert.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                            verifyalert.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                        }
                    });
                    verifyalert.setMessage("Job running on MDU\n\nPlease stop the job and scan QRCode again!");
                    verifyalert.show();
                }
                else
                {
                    qrcoderesult.setVisibility(View.VISIBLE);
                    qrcoderesult.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.tick));
                    qrcodescanned=true;
                    main.next.setEnabled(true);
                    main.ScannedQrCode(qr);
                }
            }
            else
            {
                Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
                qrcoderesult.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.cross));
                Toast.makeText(getContext(),"Invalid QRCode",Toast.LENGTH_SHORT).show();
                qrcoderesult.setVisibility(View.VISIBLE);
                qrcode.startAnimation(shake);
                qrcodescanned=false;
            }


        }
    }



}
