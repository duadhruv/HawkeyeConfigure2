package com.example.hawkeyeconfigure;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class Step3 extends Fragment {
    View Fragview;
    MainActivity main;
    ProgressBar progressBar;

    HashTextView macname,productioncenter;


    public Step3(MainActivity main) {
        this.main=main;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Fragview = inflater.inflate(R.layout.step3, container, false);
        macname=Fragview.findViewById(R.id.macname);
        productioncenter=Fragview.findViewById(R.id.ProductionCenter);
        progressBar=Fragview.findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);



        macname.getTextInputEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                macname.setLoading();
                main.Send_Query("EmsNet_Proc_MachineMasterV1_AndroidProc_MacName_GetList","MacName",122,"SPG");
            }
        });

        productioncenter.getTextInputEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productioncenter.setLoading();
                main.Send_Query("EmsNet_Proc_MachineMasterV1_AndroidProc_ProductionCenter_GetList","ProductionCenter",122,"SPG");
            }
        });

        return Fragview;
    }





    public void onDoneClick()
    {
        boolean valid=true;

        if(macname.getValue().length()==0)
        {
            valid=false;
            macname.setError("Empty field");
        }
        else
        {
            macname.removeError();
        }
        if(productioncenter.getValue().length()==0)
        {
            valid=false;
            productioncenter.setError("Empty field");
        }
        else
        {
            productioncenter.removeError();
        }

        if(valid) {
            progressBar.setVisibility(View.VISIBLE);
            String query = "EmsNet_Proc_MachineMasterV1_AndroidProc_MduRegistration @MacName='" +macname.getValue().toString()+"',@MDUMacAddress='"+main.MacAdd+"',@ProductionCenter='"+productioncenter.getValue()+"'";
            main.Send_Query(query,"Submit",3,"Submit");
        }
        else
        {
            Toast.makeText(getContext(),"Invalid Entries",Toast.LENGTH_SHORT).show();
        }
    }

}
