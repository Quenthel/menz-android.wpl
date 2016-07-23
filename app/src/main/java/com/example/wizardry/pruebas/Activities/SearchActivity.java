package com.example.wizardry.pruebas.Activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wizardry.pruebas.Adapters.SongListAdapter;
import com.example.wizardry.pruebas.Helpers.ContextHelper;
import com.example.wizardry.pruebas.R;
import com.example.wizardry.pruebas.Retrievers.MusicRetriever;
import com.example.wizardry.pruebas.Retrievers.RetrieverHelper;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    String s = "";
    String currentSelectedSong = "";
    String selectedSong = "";
    private ContextHelper ch;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean light = sharedPref.getBoolean("light", false);
        setTheme(!light ? R.style.AppTheme : R.style.AppThemeWhite);
        setContentView(R.layout.activity_search);
        Toolbar tol = (Toolbar) findViewById(R.id.toolbar);
        tol.setNavigationIcon(R.drawable.nav);
        tol.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        String search = getIntent().getStringExtra("search");
        String type = getIntent().getStringExtra("type");
        Boolean b = getIntent().getBooleanExtra("b", false);
        tol.setTitle(search);
        RetrieverHelper rh = new RetrieverHelper(getContentResolver(), getApplicationContext(), type, search, b);
        ArrayAdapter<MusicRetriever.Item> adapterSon;
        ContentResolver cr = this.getContentResolver();
        final List<MusicRetriever.Item> l = rh.getResults();
        ListView lv2 = (ListView) findViewById(R.id.listViewSongs);

        adapterSon = new SongListAdapter(this, new ArrayList<MusicRetriever.Item>());
        lv2.setAdapter(adapterSon);
        lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), PlayerActivity.class);
                MusicRetriever.Item is = l.get(position);
                String path = is.getPath();
                i.putExtra("path", path);
                ArrayList<String> albumPaths = new ArrayList<>();
                for (int x = position, y = 0; x < l.size() && y < 100; x++, y++) {
                    albumPaths.add(l.get(x).getPath());
                }
                startNewPlayer(path, albumPaths);
            }
        });
        int cnt = 0;
        for (MusicRetriever.Item i : l) {
            adapterSon.add(i);
            cnt++;
        }
        TextView t = (TextView) findViewById(R.id.totaal);
        t.setText("Total: " + cnt);
        registerForContextMenu(lv2);
    }

    private void startNewPlayer(String s, ArrayList<String> albumPaths) {
        Intent i = new Intent(this, PlayerActivity.class);
        i.putExtra("path", s);
        i.putStringArrayListExtra("albumpaths", albumPaths);
        startActivity(i);
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        ch = new ContextHelper(this, getContentResolver());
        MenuInflater mi = new MenuInflater(this);
        menu.setHeaderIcon(R.drawable.ic_action_edit);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        TextView tx = (TextView) info.targetView.findViewById(R.id.label);
        TextView tx2 = (TextView) info.targetView.findViewById(R.id.songpath);
        ListView currentView = (ListView) v.findViewById(R.id.listViewSongs);
        currentSelectedSong = tx2.getText().toString();
        selectedSong = tx.getText().toString();
        long selectedWordId = info.id;
        menu.setHeaderTitle(selectedSong);
        s = selectedSong;
        mi.inflate(R.menu.menu_options, menu);
    }
}
