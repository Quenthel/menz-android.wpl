package com.example.wizardry.wPlayer.Activities;

import android.animation.Animator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.DatabaseUtils;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wizardry.wPlayer.Adapters.AlbumSongListAdapterX;
import com.example.wizardry.wPlayer.Helpers.AdaptableLinearLayout;
import com.example.wizardry.wPlayer.Helpers.ContextHelper;
import com.example.wizardry.wPlayer.Helpers.ImageHelper;
import com.example.wizardry.wPlayer.R;
import com.example.wizardry.wPlayer.Retrievers.AlbumSongsRetriever;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class AlbumActivity extends AppCompatActivity {
    private String album;
    private String selectedSong;
    private String currentSelectedSong;
    private String year;
    private long totalDuration;
    private ContextHelper ch;
    private int total;
    private boolean isPaletable = true, light = false, useD, useTr = true;
    private ArrayList<String> albumPaths = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        //    Boolean use = sharedPref.getBoolean("usematerial", true);
        light = sharedPref.getBoolean("light", false);
        setTheme(!light ? R.style.AppTheme : R.style.AppThemeWhite);
        useD = sharedPref.getBoolean("lightB", false);
        setContentView(R.layout.activity_album);
        Intent in = getIntent();
        String[] data = in.getStringArrayExtra("data");
        album = data[0];
        String albumArt = data[1];
        year = data[2];
        String albumArtist = data[3];
        AlbumSongListAdapterX adapterSonx;

        //  AlbumSongListAdapterRecycler adapterRecycler=null;
        ImageView im = (ImageView) findViewById(R.id.imViewAlbum);
        if (!albumArt.equals("vac")) {
            im.setImageDrawable(Drawable.createFromPath(albumArt));
        } else {
            im.setImageResource(R.drawable.nodata);
        }
        final ArrayList<AlbumSongsRetriever.Item> l = new AlbumSongsRetriever(getContentResolver(), DatabaseUtils.sqlEscapeString(album)).loadingSongs();
        // isPaletable = sharedPref.getBoolean("usepalette", true);

        //  if (isPaletable) {
        int[] s = ImageHelper.getColors(BitmapFactory.decodeFile(albumArt));
        //  int[]s = ss(BitmapFactory.decodeFile(albumArt), false);
        adapterSonx = new AlbumSongListAdapterX(this, new ArrayList<AlbumSongsRetriever.Item>(), s, light, useD, im);
        //  adapterRecycler = new AlbumSongListAdapterRecycler(this, l, s, light, useD);

        //    if (use) {
        //  setSDecorationsSwatch(s);
        setDecorations(s);
        //    }
      /*  } else {
            adapterSonx = new AlbumSongListAdapterX(this, new ArrayList<AlbumSongsRetriever.Item>());
        }*/

        AdaptableLinearLayout lll = (AdaptableLinearLayout) findViewById(R.id.adapr);
        lll.setAdapter(adapterSonx);

        for (AlbumSongsRetriever.Item i : l) {
            total++;
            totalDuration += i.getDuration();
            albumPaths.add(i.getPath());
            adapterSonx.add(i);
        }
        registerForContextMenu(lll);
        //  if (use) {
        fillOtherAlbumData();
        // }
        Log.w("AlbumAct: ", albumPaths.size() + "");
  /*      RecyclerView rc = (RecyclerView)findViewById(R.id.adapr);
        rc.setAdapter(adapterRecycler);
        rc.setNestedScrollingEnabled(false);
        rc.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));*/
    }

    //Metodo que rellena los datos siempre que se usa el layout por defecto.
    private void fillOtherAlbumData() {
        TextView tv4 = (TextView) findViewById(R.id.txalbumtotal);
        TextView tv5 = (TextView) findViewById(R.id.txalbdura);
        TextView tv6 = (TextView) findViewById(R.id.txAlbumInfo);
        String t = String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes(totalDuration), TimeUnit.MILLISECONDS.toSeconds(totalDuration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalDuration)));
        if (!year.equals("0")) {
            ((TextView) findViewById(R.id.txalbyear)).setText(year);
        }
        tv4.setText(total + " songs ");
        tv5.setText("Total: " + t);
        tv6.setText(album);
    }
  /*  private int[] ss(Bitmap s, boolean dark){
        int[] sd = new int[7];
        if(!dark) {
            Palette palette = Palette.from(s).generate();
            Palette.Swatch swa = palette.getVibrantSwatch();
            Palette.Swatch swaL = palette.getLightVibrantSwatch();
            if(swa==null){
                swa= palette.getDarkVibrantSwatch();
            }
            sd[1] = swa.getRgb();
            sd[0] = swa.getTitleTextColor();
            sd[2] = swa.getBodyTextColor();
            sd[4] = swaL.getRgb();
        }else{
            Palette palette = Palette.from(s).generate();
            Palette.Swatch swa = palette.getDarkVibrantSwatch();
            Palette.Swatch swaL = palette.getVibrantSwatch();
            sd[1] = swa.getRgb();
            sd[0] = swa.getTitleTextColor();
            sd[2] = swa.getBodyTextColor();
            sd[4] = swaL.getRgb();
        }

        return  sd;
    }*/

    private void setDecorations(final int s[]) {
        final CoordinatorLayout lyx = (CoordinatorLayout) findViewById(R.id.coord);
        final AppBarLayout abl = (AppBarLayout) findViewById(R.id.app_bar_layout);
        final android.support.v7.widget.Toolbar tool = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        final CollapsingToolbarLayout ctl = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        tool.setNavigationIcon(R.drawable.nav);
        tool.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playFromTop();
            }
        });
        TextView tv = (TextView) findViewById(R.id.txAlbumInfo);
        TextView tv3 = (TextView) findViewById(R.id.txalbyear);
        TextView tv4 = (TextView) findViewById(R.id.txalbumtotal);
        TextView tv5 = (TextView) findViewById(R.id.txalbdura);
        final int backD, texD, texDl, sta;
        // fab.show();
      /*  if (!light) {
            backD = s[1];
            texD = s[0];
            texDl = s[2];
            sta = s[1];
        } */

        if (!useD) {
            backD = s[2];
            texD = s[1];
            texDl = s[3];
            sta = s[5];
        } else {
            backD = s[1];
            texD = s[0];
            texDl = s[2];
            sta = s[1];
        }
        tv3.setTextColor(texDl);
        tv.setTextColor(texD);
        tv5.setTextColor(texD);
        tv4.setTextColor(texDl);
        //   ly.setBackgroundColor(backD);
        lyx.setBackgroundColor(backD);
        ctl.setBackgroundColor(backD);

        ctl.setExpandedTitleColor(getColor(android.R.color.transparent));
        ctl.setCollapsedTitleTextColor(texD);
        ctl.setCollapsedTitleGravity(Gravity.CENTER_VERTICAL);
        ctl.setExpandedTitleGravity(Gravity.START);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        fab.setBackgroundTintList(ColorStateList.valueOf(s[7]));
        abl.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            int intColorCode = 0;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                //     int colorDrawerItemSelected = (texD & 0x00FFFFFF) | 0x40000000;
                intColorCode = -(verticalOffset);
                if (intColorCode >= 600) {
                    getWindow().setStatusBarColor(sta);

                } else {
                    getWindow().setStatusBarColor(Color.TRANSPARENT);
                }
                useTr = intColorCode <= 10;
                //  getWindow().setStatusBarColor(Color.alpha(255));
            }
        });

        fab.postDelayed(new Runnable() {
            @Override
            public void run() {
                //create your anim here
                if (fab.getVisibility() == View.INVISIBLE) {
                    int cx = fab.getWidth() / 2;
                    int cy = fab.getHeight() / 2;
                    float finalRadius = (float) Math.hypot(cx, cy);
                    Animator anim = ViewAnimationUtils.createCircularReveal(fab, cx, cy, 0, finalRadius);

                    fab.setVisibility(View.VISIBLE);
                    anim.start();
                }
            }
        }, 600);
    }

    private void playFromTop() {
        Intent i = new Intent(this, PlayerActivity.class);
        //i.putExtra("path", "vac");
        ImageView iz = (ImageView) findViewById(R.id.imViewAlbum);
        i.putStringArrayListExtra("albumpaths", albumPaths);
        if (albumPaths.size() == 1) {
            i.putExtra("one", true);
        }
        //if (true) {
        final FloatingActionButton a = (FloatingActionButton) findViewById(R.id.fab);
           /* int cx = a.getWidth() / 2;
            int cy = a.getHeight() / 2;
            float initialRadius = (float) Math.hypot(cx, cy);
            Animator anim = ViewAnimationUtils.createCircularReveal(a, cx, cy, initialRadius, 0);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    a.setVisibility(View.INVISIBLE);
                }
            });
            anim.start();*/
        a.setVisibility(View.INVISIBLE);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, iz, "alb");
        ActivityCompat.startActivity(this, i, options.toBundle());
        // } else startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (fab.getVisibility() == View.INVISIBLE) {
                    int cx = fab.getWidth() / 2;
                    int cy = fab.getHeight() / 2;
                    float finalRadius = (float) Math.hypot(cx, cy);
                    Animator anim = ViewAnimationUtils.createCircularReveal(fab, cx, cy, 0, finalRadius);
                    fab.setVisibility(View.VISIBLE);
                    anim.start();
                }
            }
        }, 600);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FloatingActionButton a = (FloatingActionButton) findViewById(R.id.fab);
        a.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FloatingActionButton a = (FloatingActionButton) findViewById(R.id.fab);
        a.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FloatingActionButton a = (FloatingActionButton) findViewById(R.id.fab);
        a.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        ch = new ContextHelper(this, this.getContentResolver());
        MenuInflater mi = new MenuInflater(this);
        //  AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        TextView tx = (TextView) v.findViewById(R.id.label);

        TextView tx2 = (TextView) v.findViewById(R.id.songpath);
        currentSelectedSong = tx2.getText().toString();
        selectedSong = tx.getText().toString();
        menu.setHeaderTitle(selectedSong);
        String s = selectedSong;
        mi.inflate(R.menu.menu_options, menu);


    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addTo:
                Log.e("OPTION", "ADD");
                ch.addToPlayList("");
                break;
            case R.id.Delete:
                Log.e("OPCION", "DELETE");
                if (ch.deleteSong(currentSelectedSong)) {
                    Toast.makeText(this, "Ok", Toast.LENGTH_LONG).show();
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                    //     this.recreate();

                }

                break;
            case R.id.EditInfo:
                Log.e("OPCION", "Editando " + currentSelectedSong);
                ch.editInfo(currentSelectedSong);
                break;
            case R.id.ring:
                Log.e("OPCION", "Ring Ring");
                ch.setRingRing(currentSelectedSong, selectedSong);
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }
}