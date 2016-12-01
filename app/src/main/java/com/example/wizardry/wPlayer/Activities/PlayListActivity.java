package com.example.wizardry.wPlayer.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wizardry.wPlayer.Adapters.SongListAdapter;
import com.example.wizardry.wPlayer.Fragments.FragmentPlaylist;
import com.example.wizardry.wPlayer.Helpers.ContextHelper;
import com.example.wizardry.wPlayer.Helpers.ItemClickSupport;
import com.example.wizardry.wPlayer.R;
import com.example.wizardry.wPlayer.Retrievers.MusicRetriever;
import com.example.wizardry.wPlayer.Retrievers.RetrieverHelper;

import java.util.ArrayList;

public class PlayListActivity extends AppCompatActivity {
    final String TAG = "Playlist";
    String currentSelectedSong, selectedSong, currentPlaylist, playpath, s = "";
    int currentSelectedSongi;
    ArrayList<String> albumPaths = new ArrayList<>();
    private ContextHelper ch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean light = sharedPref.getBoolean("light", false);
        setTheme(!light ? R.style.AppTheme : R.style.AppThemeWhite);
        getWindow().setStatusBarColor(getColor(R.color.colorPrimary));

        setContentView(R.layout.activity_play_list);

        SongListAdapter adapter;
        RecyclerView rvContacts = (RecyclerView) findViewById(R.id.lust);
        final ArrayList<MusicRetriever.Item> songs = getIntent().getParcelableArrayListExtra("songs");
        String name = getIntent().getStringExtra("name");
        currentPlaylist = name;
        playpath = getIntent().getStringExtra("playpath");

        adapter = new SongListAdapter(this, songs);
        rvContacts.setAdapter(adapter);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        rvContacts.setHasFixedSize(true);
        for (MusicRetriever.Item i : songs) {
            Log.w(TAG, i.getTitle());
            albumPaths.add(i.getPath());
        }
        ItemClickSupport.addTo(rvContacts).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent i = new Intent(getBaseContext(), PlayerActivity.class);
                TextView rt = (TextView) v.findViewById(R.id.songpath);
                String path = rt.getText().toString();
                i.putExtra("path", path);
                i.putStringArrayListExtra("albumpaths", albumPaths);
                startActivity(i);
            }
        });

        TextView rt = (TextView) findViewById(R.id.nym);
        rt.setText("Total: " + songs.size());
        if (light) rt.setTextColor(Color.BLACK);
        Toolbar t = (Toolbar) findViewById(R.id.toolbar);
        t.setTitle(name);
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
                int audioid = (int) RetrieverHelper.getSongIdFromMediaStore(currentSelectedSong, getContentResolver());
                //Toast.makeText(this,Long.toString(RetrieverHelper.getSongIdFromMediaStore(currentSelectedSong,getContentResolver())),Toast.LENGTH_SHORT).show();
                RetrieverHelper.deleteSongFromPlaylist(this.getContentResolver(), audioid, currentPlaylist);
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
