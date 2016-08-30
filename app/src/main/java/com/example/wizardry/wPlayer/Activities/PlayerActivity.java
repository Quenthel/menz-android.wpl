package com.example.wizardry.wPlayer.Activities;

import android.animation.Animator;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.wizardry.wPlayer.Async.MusicService;
import com.example.wizardry.wPlayer.Async.getLyrTask;
import com.example.wizardry.wPlayer.Helpers.ImageHelper;
import com.example.wizardry.wPlayer.Helpers.MetadataHelper;
import com.example.wizardry.wPlayer.Helpers.Utilities;
import com.example.wizardry.wPlayer.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

public class PlayerActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    private GestureDetector mGestureDetector;
    private boolean mBound;
    private String ly = null;
    private TextView songTotalDurationLabel, songCurrentDurationLabel;
    private MusicService mService;
    private Handler mHandler = new Handler();
    private boolean hasLi;
    private Utilities utils;
    private boolean tried = false;
    private SeekBar songProgressBar;
    private String current, currentPath, l;
    private TextView t6, tx8;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            current = intent.getStringExtra("path");
            setupData(current);
        }
    };
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            int totalDuration = mService.getTotal();
            int currentDuration = mService.getCurrentPosition();
            songTotalDurationLabel.setText(utils.milliSecondsToTimer(totalDuration));
            songCurrentDurationLabel.setText(utils.milliSecondsToTimer(currentDuration));
            songProgressBar.setProgress(utils.getProgressPercentage(currentDuration, totalDuration));
            mHandler.postDelayed(this, 1000);
        }
    };
    /**
     *
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            mService = binder.getService();
            ArrayList<String> st = getIntent().getStringArrayListExtra("albumpaths");
            if (mService.isNotNull()) {
                if (!getIntent().getBooleanExtra("b", false)) {
                    mService.setList(st, 2);
                    mService.loadNextOrPreviousSong(true);
                    mBound = true;
                    setupData(mService.getPath());
                    updateProgressBar();
                    // updateInfo();
                } else {
                    mBound = true;
                    setupData(mService.getPath());
                    updateProgressBar();
                    // updateInfo();
                }
            }
            IntentFilter ix = new IntentFilter(MusicService.action);
            registerReceiver(receiver, ix);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);
        songTotalDurationLabel = (TextView) findViewById(R.id.txcur);
        songCurrentDurationLabel = (TextView) findViewById(R.id.txtot);
        songProgressBar = (SeekBar) findViewById(R.id.seekBar);
        songProgressBar.setOnSeekBarChangeListener(this);
        android.support.v7.widget.Toolbar tool = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tool);
        utils = new Utilities();
        Intent i = getIntent();

        boolean isRandom = i.getBooleanExtra("random", false);
        String audioFile = i.getStringExtra("path");
        ArrayList<String> st = i.getStringArrayListExtra("albumpaths");
        super.onStart();

        if (st != null) {
            if (st.size() > 0) {
                if (audioFile != null) {
                    st.remove(audioFile);
                    st.add(0, audioFile);
                }
                if (isRandom) {
                    //     Log.i("Random", "Shuffleando lista...");
                    Collections.shuffle(st);
                }
            }
        }
        t6 = (TextView) findViewById(R.id.textView6);
        tx8 = (TextView) findViewById(R.id.textView8);
        Intent intent = new Intent(this, MusicService.class);
        if (!i.getBooleanExtra("b", false)) {
            intent.putStringArrayListExtra("paths", st);
        }
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        songProgressBar.setProgress(0);
        songProgressBar.setMax(100);

        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        ImageView iv = (ImageView) findViewById(R.id.imageViewPlayer);
                        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
                        iv.startAnimation(animation);
                        if (mBound) {
                            mService.loadNextOrPreviousSong(false);
                            //   setupData(mService.getPath());
                        }
                    } else {
                        ImageView iv = (ImageView) findViewById(R.id.imageViewPlayer);
                        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
                        iv.startAnimation(animation);
                        if (mBound) {
                            mService.loadNextOrPreviousSong(true);
                            //   setupData(mService.getPath());

                        }
                    }
                }
              /*  if (velocityX > 0 && mService.pastItemsPilaVersion.size() > 0) {
                    ImageView iv = (ImageView) findViewById(R.id.imageViewPlayer);
                    Animation animation = AnimationUtils.loadAnimation(
                            getApplicationContext(), R.anim.fade);
                    iv.startAnimation(animation);
                    if (mBound) {
                        mService.loadNextOrPreviousSong(false);
                        setupData(mService.getPath());
                    }
                } else {
                    ImageView iv = (ImageView) findViewById(R.id.imageViewPlayer);
                    Animation animation = AnimationUtils.loadAnimation(
                            getApplicationContext(), R.anim.fade);
                    iv.startAnimation(animation);
                    if (mBound) {
                        mService.loadNextOrPreviousSong(true);
                        setupData(mService.getPath());
                    }
                }*/

                return true;
            }
        });
    }

    private void setupData(String path) {
        MetadataHelper mh = null;
        tried = false;
        ly = null;
        current = path;
        if (mBound) {
            path = mService.getPath();
            mh = mService.getMh();
        }
    /*    }
        MetadataHelper mh = null;
        try {
            mh = new getMetadataTask().execute(path).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }*/
        ImageView i = (ImageView) findViewById(R.id.imageViewPlayer);
        android.support.v7.widget.Toolbar tool = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        Bitmap bi;
        int[] palette;
        currentPath = path;
        current = mh.getPath();
        bi = mh.getFullEmbedded();
        if (bi != null) {
            i.setImageBitmap(bi);
            palette = ImageHelper.getColors(bi);

        } else {
            i.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.nodata));
            palette = ImageHelper.getColors(BitmapFactory.decodeResource(getResources(), R.drawable.nodata));
        }
        //Textos----------------------------
        t6.setText(mh.getAlbum());
        tx8.setText(mh.getArtist());
        t6.setTextColor(palette[2]);
        tx8.setTextColor(palette[2]);

        if (l != null) {
            TextView t10 = (TextView) findViewById(R.id.txLyr);
            t10.setVisibility(View.VISIBLE);
            t10.setText(l);
            t10.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    hide(v);
                    return false;
                }
            });
            hasLi = true;
        } else {
            findViewById(R.id.scrollLyr).setVisibility(View.GONE);
            hasLi = false;
        }
        //-----------------------------------

        tool.setTitle(mh.getNombre());
        tool.setTitleTextColor(palette[2]);

        FloatingActionButton fabby1 = (FloatingActionButton) findViewById(R.id.fab);
        fabby1.setBackgroundTintList(ColorStateList.valueOf(palette[7]));
        fabby1.setRippleColor(palette[2]);
        songProgressBar.setProgressTintList(ColorStateList.valueOf(palette[0]));
        getWindow().setStatusBarColor(palette[5]);
        currentPath = path;
        mh = null;
    }

    public void hide(View v) {
        final ScrollView sc = (ScrollView) findViewById(R.id.scrollLyr);
        try {
            if (ly == null) {
                if (!hasLi && !tried) {
                    ly = new getLyrTask().execute(currentPath).get();
                    tried = true;
                }
            }
            if (ly != null)
                hasLi = true;
            if (hasLi) {
                TextView t10 = (TextView) findViewById(R.id.txLyr);
                int cx = sc.getWidth() / 2;
                int cy = sc.getHeight() / 2;
                float finalRadius = (float) Math.hypot(cx, cy);
                Animator anim = ViewAnimationUtils.createCircularReveal(sc, cx, cy, 0, finalRadius);
                //   sc.setVisibility(View.VISIBLE);

                t10.setText(ly);
                t10.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        hide(v);
                        return false;
                    }
                });
                if (sc.getVisibility() == View.VISIBLE) {
                    sc.setVisibility(View.GONE);
                    // i.setVisibility(View.VISIBLE);
                } else {

                    sc.setVisibility(View.VISIBLE);
                    anim.start();
                    //     i.setVisibility(View.GONE);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
  /*  private void white(int[] a){
        View rlc = findViewById(R.id.rlData);
        rlc.setBackgroundColor(a[2]);
        tx8.setTextColor(a[1]);
        t6.setTextColor(a[3]);
    }*/

    public void play(View v) {
        if (mBound) {
            if (mService.isPlaying()) {
                mService.start();
            } else {
                mService.start();
            }
        }
    }

    public void setRepeat(View v) {
        if (mService.isLooping()) {
            mService.setRepeat();
            ImageButton ib = (ImageButton) v;
            ib.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        } else {
            mService.setRepeat();
            ImageButton ib = (ImageButton) v;
            ib.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        }
    }

    public void setRandom(View v) {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
        v.setAnimation(animation);
        v.startAnimation(animation);
        mService.setRandom();
    }

    public void next(View v) {
        if (mBound) {
            mService.loadNextOrPreviousSong(true);
            setupData(mService.getPath());
        }
    }

    public void back(View v) {
        if (mBound) {
            mService.loadNextOrPreviousSong(false);
            setupData(mService.getPath());
        }
    }


    /**
     * EVENTOS DEL SEEKBAR
     */
    private void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 1000);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mBound) {
            mHandler.removeCallbacks(mUpdateTimeTask);
            int totalDuration = mService.getTotal();
            int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
            mService.seekTo(currentPosition);
            updateProgressBar();
        }
    }

    /**
     * MENUS & EVENTS de MAIN
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("OnDestroy", "Unbind - Killin tasks");
        mHandler.removeCallbacks(mUpdateTimeTask);
        mHandler = null;
        // mHandler2.removeCallbacks(updateInfo);
        // mHandler2 = null;
        unbindService(mConnection);
        unregisterReceiver(receiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //   if (this.isPaused) {
        //     MusicService.mediaPlayer.seekTo(seek_position);
        // }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mGestureDetector.onTouchEvent(event);
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.miCompose) {
            Intent i = new Intent(getApplicationContext(), DataActivity.class);
            i.putExtra("path", currentPath);
            ImageView iv = (ImageView) findViewById(R.id.imageViewPlayer);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, iv, "alb");
            ActivityCompat.startActivity(this, i, options.toBundle());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
