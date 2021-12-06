package com.example.hawkeyeconfigure;

import android.content.Context;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.tuyenmonkey.mkloader.MKLoader;

import androidx.constraintlayout.widget.ConstraintLayout;

public class HexProgress extends ConstraintLayout {
    Context context;
    View view;
    ImageView round,h;
    MKLoader mkLoader;
    Animation fadein,fadeout;
    boolean visible=false;
    public HexProgress(Context context) {
        super(context);
    }

    public HexProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.hex_progress,this, true);
        view.setBackgroundColor(getResources().getColor(R.color.progressbg));
        view.setVisibility(GONE);
        round=view.findViewById(R.id.round);
        h=view.findViewById(R.id.h);
        mkLoader=findViewById(R.id.loader);
        fadein=AnimationUtils.loadAnimation(context,R.anim.fadein);
        fadeout=AnimationUtils.loadAnimation(context,R.anim.fadeout);
        //mkLoader.setVisibility(INVISIBLE);
    }



    public void setProgressing()
    {

        //final Animation animation = AnimationUtils.loadAnimation(context,R.anim.rotate);
        //final Animation animation1 = AnimationUtils.loadAnimation(context,R.anim.progresszoominout);
        //round.startAnimation(animation);
        //h.startAnimation(animation1);
        //mkLoader.setVisibility(VISIBLE);
        //view.setVisibility(VISIBLE);
        if(!visible) {
            view.setVisibility(INVISIBLE);
            view.startAnimation(fadein);
            view.setClickable(true);
            visible = true;
        }
    }

    public void stopProgressing()
    {
        if(visible)
        {
            view.startAnimation(fadeout);
            view.setClickable(false);
            visible=false;
        }


        //view.setVisibility(INVISIBLE);
        //round.clearAnimation();
        //h.clearAnimation();
    }
}
