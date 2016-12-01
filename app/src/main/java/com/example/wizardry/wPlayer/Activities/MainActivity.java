package com.example.wizardry.wPlayer.Activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wizardry.wPlayer.Fragments.FragmentAlbumList;
import com.example.wizardry.wPlayer.Fragments.FragmentPlaylist;
import com.example.wizardry.wPlayer.Fragments.FragmentSongList;
import com.example.wizardry.wPlayer.Helpers.ImageHelper;
import com.example.wizardry.wPlayer.MetadataSingle;
import com.example.wizardry.wPlayer.MusicService;
import com.example.wizardry.wPlayer.R;
import com.example.wizardry.wPlayer.Retrievers.MusicRetriever;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public String current = null;
    private SharedPreferences sharedPref;
    private boolean light, hasPermission = false;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            current = intent.getStringExtra("path");
            setupCurrent(current);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        checkPermissions();
        checkPreferences();

        setTheme(!light ? R.style.AppTheme : R.style.AppThemeWhite);
        setContentView(R.layout.activity_main);

        if (light) {
            findViewById(R.id.appbar).setBackgroundColor(getColor(R.color.colorAccent));
            // getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        getWindow().setStatusBarColor(getColor(R.color.colorPrimary));
        if (hasPermission) {
            SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
            ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);
            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);

            mViewPager.setOffscreenPageLimit(1);

            createButtonMenu();
            setupBottom();
            // MetadataSingle.INSTANCE.init();
            //  View vi = findViewById(R.id.rlt);
            //  vi.setBackground(ImageHelper.makeSelector(Color.parseColor("#d72c66"), Color.parseColor("#95000000"), 255));

            if (light) {
                tabLayout.setBackgroundColor(Color.WHITE);
                tabLayout.setTabTextColors(Color.BLACK, R.color.colorAccent);
            }
        }//  else {
        //  Toast.makeText(this, "Can't query without permission", Toast.LENGTH_LONG).show();
        //  }
        // Intent intent = new Intent(this, MusicService.class);
        // intent.putExtra("s", false);
        //    bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (MetadataSingle.INSTANCE.path != null) {
            IntentFilter ix = new IntentFilter(MusicService.action);
            registerReceiver(receiver, ix);
            setupCurrent(current);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (MetadataSingle.INSTANCE.path != null) {
            unregisterReceiver(receiver);
        }
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1337);
        } else {
            hasPermission = true;
        }
        return true;
    }


   /* private void check() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1349);
        }
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1337: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    hasPermission = true;

                    Log.i("Permission", requestCode + " OK");
                    this.recreate();
                } else {
                    Log.i("Permission", requestCode + "  NO OK");

                }
                break;
            }
            case 1349: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    hasPermission = true;

                    Log.i("Permission", requestCode + " OK");
                    this.recreate();
                } else {
                    Log.i("Permission", requestCode + "  NO OK");

                }
                break;
            }
        }
    }

    private void scan() {
        List<MusicRetriever.Item> l = MusicRetriever.loadingSongs(getContentResolver());
        for (MusicRetriever.Item i : l) {
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

            intent.setData(i.getURI());
            sendBroadcast(intent);
            Log.i(":", "Running...");
        }
    }

    private void checkPreferences() {
        boolean scan = sharedPref.getBoolean("scanner", false);
        light = sharedPref.getBoolean("light", true);
        if (scan) {
            Log.i("Main:", "Running scanner");
            scan();
        }
        //if(true) {
        //  MetadataSingle.INSTANCE.type =Typeface.createFromAsset(getAssets(), "fonts/ss.ttf");
        //}
    }

    private void createButtonMenu() {
        ImageView icon = new ImageView(this);
        // icon.setImageResource(R.mipmap.circma);
        icon.setImageResource(R.drawable.ic_add);
        icon.setColorFilter(getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        // CoordinatorLayout c = (CoordinatorLayout) findViewById(R.id.main_content);

        FloatingActionButton actionButton;
        SubActionButton button1;
        SubActionButton button2;

        SubActionButton button3;
        if (!light) {
            actionButton = new FloatingActionButton.Builder(this)
                    .setContentView(icon)
                    .setTheme(FloatingActionButton.THEME_DARK)
                    .build();
        } else {
            actionButton = new FloatingActionButton.Builder(this)
                    .setContentView(icon)
                    .setTheme(FloatingActionButton.THEME_LIGHT)
                    .build();
        }
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        ImageView itemIcon1 = new ImageView(this);
        ImageView itemIcon2 = new ImageView(this);
        ImageView itemIcon3 = new ImageView(this);
        itemIcon1.setImageResource(R.drawable.ic_shuffle);
        itemIcon2.setImageResource(R.drawable.ic_settings);
        itemIcon3.setImageResource(R.drawable.ic_search);
        itemIcon1.setColorFilter(getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        itemIcon2.setColorFilter(getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        itemIcon3.setColorFilter(getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);

        if (light) {
            button1 = itemBuilder.setContentView(itemIcon1).setTheme(FloatingActionButton.THEME_LIGHT).build();
            button2 = itemBuilder.setContentView(itemIcon2).setTheme(FloatingActionButton.THEME_LIGHT).build();
            button3 = itemBuilder.setContentView(itemIcon3).setTheme(FloatingActionButton.THEME_LIGHT).build();
        } else {
            button1 = itemBuilder.setContentView(itemIcon1).setTheme(FloatingActionButton.THEME_DARK).build();
            button2 = itemBuilder.setContentView(itemIcon2).setTheme(FloatingActionButton.THEME_DARK).build();
            button3 = itemBuilder.setContentView(itemIcon3).setTheme(FloatingActionButton.THEME_DARK).build();
        }
   /*     CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) actionButton.getLayoutParams();
        params.setBehavior(new  android.support.design.widget.FloatingActionButton.Behavior());
        actionButton.requestLayout();*/

        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(button1)
                .addSubActionView(button2)
                .addSubActionView(button3)
                .attachTo(actionButton)
                .build();
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRandom();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOptions();
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearch();
            }
        });

    }

    private void startRandom() {
        Intent i = new Intent(getApplicationContext(), PlayerActivity.class);
        ContentResolver cr = getContentResolver();
        MusicRetriever.Item l = MusicRetriever.loadingRandomSong(cr);
        List<MusicRetriever.Item> randomItems = MusicRetriever.loadingSongs(cr);
        ArrayList<String> randomString = new ArrayList<>();
        for (MusicRetriever.Item it : randomItems) {
            randomString.add(it.getPath());
        }
        String path = null;
        if (l != null) {
            path = l.getPath();
        }
        i.putExtra("path", path);
        i.putExtra("random", true);
        i.putStringArrayListExtra("albumpaths", randomString);
        startActivity(i);

    }

    private void startOptions() {
        //  final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        //   check();
      /*  if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Intent i = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }*/
        Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivityForResult(i, 1);
        //makeSnack();
    }

    private void showSearch() {

    }

    private void search(String s, String type, Boolean b) {

    }

    /*private void makeSnack(String text) {
        CoordinatorLayout c = (CoordinatorLayout) findViewById(R.id.main_content);
        Snackbar.make(c, text, Snackbar.LENGTH_INDEFINITE)
                .setDuration(Snackbar.LENGTH_INDEFINITE)
                .setActionTextColor(getColor(R.color.traspless))
                .show();
    }*/

   /* @Override
    public void hide(boolean a) {
        // View rl = findViewById(R.id.rlt);
        //  rl.setVisibility(View.GONE);
        if (a) {
            tabLayout.setVisibility(View.GONE);
        } else {
            tabLayout.setVisibility(View.VISIBLE);
        }
    }*/

    private void setupCurrent(String path) {
        View rl = findViewById(R.id.rlt);
        rl.setVisibility(View.VISIBLE);
        TextView tx1 = (TextView) findViewById(R.id.label2);
        //MetadataHelper mh = new MetadataHelper(path, true);
        /* tx1.setText(mh.getNombre());
        ((ImageView) findViewById(R.id.icono2)).setImageBitmap(mh.getFullEmbedded());
        int[] c = ImageHelper.getColors(mh.getFullEmbedded());*/
        tx1.setText(MetadataSingle.INSTANCE.nombre);
        ((ImageView) findViewById(R.id.icono2)).setImageBitmap(MetadataSingle.INSTANCE.fullEmbedded);
        //   int[] c = ImageHelper.getColors(MetadataSingle.INSTANCE.fullEmbedded);

        if (!light) {
            rl.setBackground(ImageHelper.makeSelector(MetadataSingle.INSTANCE.currentColors[5], Color.BLACK, 255));
            tx1.setTextColor(MetadataSingle.INSTANCE.currentColors[2]);

        } else {
            rl.setBackground(ImageHelper.makeSelector(MetadataSingle.INSTANCE.currentColors[2], Color.WHITE, 255));
            tx1.setTextColor(MetadataSingle.INSTANCE.currentColors[3]);
        }
    }

    private void setupBottom() {
        View rl = findViewById(R.id.rlt);
         /*
        BottomSheetBehavior behavior = BottomSheetBehavior.from(rl);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
                Log.i("", "onSlide: " + slideOffset);
            }

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                Log.i("", "onStateChanged: " + newState);

                // React to state change
            }

        });*/
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPlayer(v);
            }
        });
    }

    public void goToPlayer(View b) {
        Intent i = new Intent(this, PlayerActivity.class);
        i.putExtra("returning", true);
        ImageView ddd = (ImageView) b.findViewById(R.id.icono2);
        //  ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, b, "now");
        //  if (light) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, ddd, "alb");
       /* } else {
            Pair<View, String> p1 = Pair.create((View) ddd, "alb");
            Pair<View, String> p2 = Pair.create(b, "now");
            options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1, p2);
        }*/
        ActivityCompat.startActivity(this, i, options.toBundle());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 1) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new FragmentSongList();
                case 1:
                    return new FragmentAlbumList();
                case 2:
                    return new FragmentPlaylist();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.Songs);
                case 1:
                    return getResources().getString(R.string.AlbumList);
                case 2:
                    return getResources().getString(R.string.Playlist);
            }
            return null;
        }
    }
}