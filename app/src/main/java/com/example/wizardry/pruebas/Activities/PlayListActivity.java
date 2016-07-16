package com.example.wizardry.pruebas.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wizardry.pruebas.Adapters.PlayListSongsAdapter;
import com.example.wizardry.pruebas.Fragments.FragmentPlaylist;
import com.example.wizardry.pruebas.Helpers.ContextHelper;
import com.example.wizardry.pruebas.R;
import com.example.wizardry.pruebas.Retrievers.MusicRetriever;
import com.example.wizardry.pruebas.Retrievers.RetrieverHelper;

import java.util.ArrayList;

public class PlayListActivity extends AppCompatActivity {
    final String TAG = "Playlist";
    String currentSelectedSong, selectedSong, currentPlaylist, playpath, s= "";
    int currentSelectedSongi;
    ArrayList<String> albumPaths = new ArrayList<>();
    private ContextHelper ch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean light = sharedPref.getBoolean("light", false);
        setTheme(!light ? R.style.AppTheme : R.style.AppThemeWhite);
      //  getWindow().setStatusBarColor(Color.DKGRAY);

        setContentView(R.layout.activity_play_list);
        ArrayAdapter<MusicRetriever.Item> adapterSon;
        ListView lv2 = (ListView) findViewById(R.id.listViewP);
        final ArrayList<MusicRetriever.Item> songs = getIntent().getParcelableArrayListExtra("songs");
        String name = getIntent().getStringExtra("name");
        currentPlaylist = name;
        playpath = getIntent().getStringExtra("playpath");
        adapterSon = new PlayListSongsAdapter(this, new ArrayList<MusicRetriever.Item>());
        lv2.setAdapter(adapterSon);
        lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getBaseContext(), PlayerActivity.class);
                TextView rt = (TextView) view.findViewById(R.id.songpath);
                String path = rt.getText().toString();
                i.putExtra("path", path);
                i.putStringArrayListExtra("albumpaths", albumPaths);
                startActivity(i);
            }
        });
        TextView rt = new TextView(getBaseContext());
        rt.setText("Total: " + songs.size());
        if (light) rt.setTextColor(Color.BLACK);
        rt.setPadding(10, 10, 10, 10);
        rt.setGravity(Gravity.CENTER_HORIZONTAL);
        lv2.addHeaderView(rt);
        for (MusicRetriever.Item i : songs) {
            Log.w(TAG, i.getTitle());
            albumPaths.add(i.getPath());
            adapterSon.add(i);
        }

        registerForContextMenu(lv2);
        Toolbar t = (Toolbar) findViewById(R.id.toolbar);
        t.setTitle(name);

        t.setNavigationIcon(R.drawable.nav);
        t.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        ch = new ContextHelper(this, getContentResolver());
        MenuInflater mi = new MenuInflater(this);
        menu.setHeaderIcon(R.drawable.ic_action_edit);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        TextView tx = (TextView) info.targetView.findViewById(R.id.label);
        TextView tx2 = (TextView) info.targetView.findViewById(R.id.songpath);
        currentSelectedSong = tx2.getText().toString();
        selectedSong = tx.getText().toString();
        menu.setHeaderTitle(selectedSong);
        s = selectedSong;
        mi.inflate(R.menu.menu_options_play, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Delete:
                Log.e("OPCION", "DELETE");
                if (ch.deleteSong(currentSelectedSong)) {
                    Toast.makeText(this, "Ok", Toast.LENGTH_LONG).show();
                }
            case R.id.deletefrompl:
                Log.e("OPCION", "DELETE Playlist: " + playpath + " Song: " + currentSelectedSong);
                //ch.deleteFromPla(currentSelectedSong, currentPlaylist);
                FragmentPlaylist flp = new FragmentPlaylist();
                int audioid =(int) RetrieverHelper.getSongIdFromMediaStore(currentSelectedSong,getContentResolver());
                //Toast.makeText(this,Long.toString(RetrieverHelper.getSongIdFromMediaStore(currentSelectedSong,getContentResolver())),Toast.LENGTH_SHORT).show();
                RetrieverHelper.deleteSongFromPlaylist(this.getContentResolver(),audioid ,currentPlaylist);
                this.recreate();
                break;
            case R.id.EditInfo:
                Log.e("OPCION", "Editando " + currentSelectedSong);
                ch.editInfo(currentSelectedSong);
                break;
            case R.id.ring:
                Log.e("OPCION", "Ring");
                ch.setRingRing(currentSelectedSong, selectedSong);
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }
}
