package com.example.wizardry.wPlayer.Activities;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wizardry.wPlayer.Helpers.ImageHelper;
import com.example.wizardry.wPlayer.MetadataSingle;
import com.example.wizardry.wPlayer.R;

import java.util.concurrent.TimeUnit;

import uk.co.senab.photoview.PhotoViewAttacher;

public class DataActivity extends AppCompatActivity {
    PhotoViewAttacher mAttacher;
    private boolean light = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        // getWindow().setExitTransition(new Explode());
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        light = sharedPref.getBoolean("light", false);
        setTheme(!light ? R.style.AppTheme : R.style.AppThemeWhite);
        setContentView(R.layout.new_info);
        letThereBeLight(getIntent().getStringExtra("path"));
    }

    private void letThereBeLight(String path) {
        MetadataSingle.INSTANCE.retrieve(path);
        // final MetadataHelper mh = new MetadataHelper(path);
        //  Bitmap art = mh.getFullEmbedded();
        Bitmap art = MetadataSingle.INSTANCE.fullEmbedded;

        if (art == null)
            art = BitmapFactory.decodeResource(getResources(), R.drawable.nodata);
        //  long d = mh.getDuration();
        long d = MetadataSingle.INSTANCE.duration;

        final String tim = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(d), TimeUnit.MILLISECONDS.toSeconds(d) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(d)));
        final ImageView i = (ImageView) findViewById(R.id.imageView);
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
       /* t.setText(mh.getLirycs());
        t1.setText(mh.getNombre());
        t2.setText(mh.getAlbum());
        t3.setText(mh.getGenre());
        String x = mh.getBitrate();
        t4.setText(mh.getYear());*/

        String x = MetadataSingle.INSTANCE.bitrate;
        t.setText(MetadataSingle.INSTANCE.lyrics);
        t1.setText(MetadataSingle.INSTANCE.nombre);
        t2.setText(MetadataSingle.INSTANCE.album);
        t3.setText(MetadataSingle.INSTANCE.genre);
        t4.setText(MetadataSingle.INSTANCE.year);
        if (x != null) {
            if (x.length() > 3) {
                //t5.setText(mh.getBitrate().substring(0, 3) + " kpbs");
                t5.setText(x.substring(0, 3) + " kpbs");
            }
        } else {
            t5.setText(0 + " kpbs");

        }
      /*  t6.setText(mh.getArtist());
        t7.setText(tim);
        t9.setText(mh.getSampleRate() + " kHz");
        t10.setText(mh.getExt());
        c.setChecked(mh.isCompilation());
        c1.setChecked(mh.getVBR());*/
        t6.setText(MetadataSingle.INSTANCE.artist);
        t7.setText(tim);
        t9.setText(MetadataSingle.INSTANCE.sampleRate + " kHz");
        t10.setText(MetadataSingle.INSTANCE.ext);
        c.setChecked(MetadataSingle.INSTANCE.isCompilation);
        c1.setChecked(MetadataSingle.INSTANCE.isVBR);
        mAttacher = new PhotoViewAttacher(i, true);
        mAttacher.setScaleType(ImageView.ScaleType.FIT_XY);


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
            getWindow().setStatusBarColor(s[5]);
        }
        final Toolbar tol = (Toolbar) findViewById(R.id.toolbar);
        tol.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAttacher.cleanup();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mAttacher.cleanup();
    }
}