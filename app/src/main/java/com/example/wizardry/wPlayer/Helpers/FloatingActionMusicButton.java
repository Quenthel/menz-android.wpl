package com.example.wizardry.wPlayer.Helpers;

import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.example.wizardry.wPlayer.R;

/**
 * Created by Wizardry on 23/10/2016.
 */


public class FloatingActionMusicButton extends FloatingActionButton {
    AnimatedVectorDrawable paTpl = (AnimatedVectorDrawable) ContextCompat.getDrawable(this.getContext(), R.drawable.pauseplay);
    AnimatedVectorDrawable plTpa = (AnimatedVectorDrawable) ContextCompat.getDrawable(this.getContext(), R.drawable.playpause);

    public FloatingActionMusicButton(Context context) {
        super(context);
    }

    public FloatingActionMusicButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatingActionMusicButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void playAnimPlay() {
        this.setImageDrawable(paTpl);
        paTpl.start();
    }

    public void playAnimPause() {
        this.setImageDrawable(plTpa);
        plTpa.start();
    }
}
