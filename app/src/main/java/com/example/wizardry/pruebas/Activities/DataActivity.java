package com.example.wizardry.pruebas.Activities;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wizardry.pruebas.Helpers.ImageHelper;
import com.example.wizardry.pruebas.Helpers.MetadataHelper;
import com.example.wizardry.pruebas.R;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import uk.co.senab.photoview.PhotoViewAttacher;

public class DataActivity extends AppCompatActivity {
    private boolean light = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
// set an enter transition
        getWindow().setExitTransition(new Explode());
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        light = sharedPref.getBoolean("light", false);
        setTheme(!light ? R.style.AppTheme : R.style.AppThemeWhite);
        setContentView(R.layout.new_info);
        letThereBeLight(getIntent().getStringExtra("path"));

// set an exit transition
    }

    private void letThereBeLight(String path) {
        final MetadataHelper mh = new MetadataHelper(path);
        Bitmap art = mh.getFullEmbedded();
        if (art == null)
            art = BitmapFactory.decodeResource(getResources(), R.drawable.nodata);
        long d = mh.getDuration();
        final String tim = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(d), TimeUnit.MILLISECONDS.toSeconds(d) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(d)));
        final ImageView i = (ImageView) findViewById(R.id.imageView);
        final PhotoViewAttacher mAttacher;
        final TextView t = (TextView) findViewById(R.id.txlir);
        final TextView t1 = (TextView) findViewById(R.id.textViewName);
        final TextView t2 = (TextView) findViewById(R.id.TextViewAlbum);
        final TextView t3 = (TextView) findViewById(R.id.textViewGenre);
        final TextView t4 = (TextView) findViewById(R.id.textViewYear);
        final TextView t5 = (TextView) findViewById(R.id.textViewBitrate);
        final TextView t6 = (TextView) findViewById(R.id.textViewArtist);
        final TextView t7 = (TextView) findViewById(R.id.textViewDur);
        final TextView t9 = (TextView) findViewById(R.id.textViewSample);
        final TextView t10 = (TextView) findViewById(R.id.textViewExt);
        final CheckBox c = (CheckBox) findViewById(R.id.checkBox);
        final CheckBox c1 = (CheckBox) findViewById(R.id.checkBox2);

        i.setImageBitmap(art);
        t.setText(mh.getLirycs());
        t1.setText(mh.getNombre());
        t2.setText(mh.getAlbum());
        t3.setText(mh.getGenre());
        t4.setText(mh.getYear());
        t5.setText(mh.getBitrate().substring(0, 3) + " kpbs");
        t6.setText(mh.getArtist());
        t7.setText(tim);
        t9.setText(mh.getSampleRate() + " kHz");
        t10.setText(mh.getExt());
        c.setChecked(mh.isCompilation());
        c1.setChecked(mh.getVBR());
        mAttacher = new PhotoViewAttacher(i, true);
        mAttacher.setScaleType(ImageView.ScaleType.FIT_CENTER);


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPref.getBoolean("usepalette", true)) {
            final int[] s = ImageHelper.getColors(art);
            final int cs = !light ? s[0] : s[3];
            //    Palette.Swatch[] sw = ImageHelper.getSwatch(art);
            t.setTextColor(cs);
            t1.setTextColor(cs);
            t2.setTextColor(cs);
            t3.setTextColor(cs);
            t4.setTextColor(cs);
            t5.setTextColor(cs);
            t6.setTextColor(cs);
            t7.setTextColor(cs);
            t9.setTextColor(cs);
            t10.setTextColor(cs);
        }
        final Toolbar tol = (Toolbar) findViewById(R.id.toolbar);
        tol.setNavigationIcon(R.drawable.nav);
        tol.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}