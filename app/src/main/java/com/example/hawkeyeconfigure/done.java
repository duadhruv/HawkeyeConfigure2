package com.example.hawkeyeconfigure;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class done extends Fragment {
    View Fragview;
    MainActivity main;
    ImageView circlecircle,circle;
    TextView heading;
    Button newdev;


    public done(MainActivity main) {
        this.main=main;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(Fragview==null) {
            final Animation fadein = AnimationUtils.loadAnimation(getContext(), R.anim.zoomin2);
            final Animation zoominout = AnimationUtils.loadAnimation(getContext(), R.anim.zoominout);
            final Animation done_animation = AnimationUtils.loadAnimation(getContext(), R.anim.faderotate);
            Fragview = inflater.inflate(R.layout.done, container, false);
            circle=Fragview.findViewById(R.id.circle);
            circlecircle=Fragview.findViewById(R.id.circlecircle);
            circlecircle.setVisibility(View.INVISIBLE);
            newdev=Fragview.findViewById(R.id.newdev);
            circle.startAnimation(fadein);
            heading=Fragview.findViewById(R.id.heading);
            heading.setVisibility(View.INVISIBLE);
            main.next.setVisibility(View.GONE);
            main.prev.setVisibility(View.GONE);
            heading.setText(main.step3.macname.getValue().toString()+"\nAdded!");

            newdev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    main.addNewDev();
                }
            });
            fadein.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    circlecircle.startAnimation(done_animation);
                    //circlecircle.startAnimation(zoominout);
                    heading.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            done_animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    circlecircle.startAnimation(fadein);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });



        }
        return Fragview;
    }




}
