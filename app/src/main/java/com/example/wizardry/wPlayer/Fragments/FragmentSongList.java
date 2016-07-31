package com.example.wizardry.wPlayer.Fragments;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wizardry.wPlayer.Activities.PlayerActivity;
import com.example.wizardry.wPlayer.Adapters.SongListAdapter;
import com.example.wizardry.wPlayer.Async.MusicService;
import com.example.wizardry.wPlayer.Helpers.ContextHelper;
import com.example.wizardry.wPlayer.R;
import com.example.wizardry.wPlayer.Retrievers.MusicRetriever;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wizardry on 19/04/2016.
 */
public class FragmentSongList extends Fragment {
    String s = "";
    String currentSelectedSong = "";
    String current = ";";
    String selectedSong = "";
    MusicService mService;
    boolean mBound;

    private ContextHelper ch;
    private OnItemSelectedListener listener;
    private ServiceConnection mConnection = new ServiceConnection() {
        boolean mBound;
        MusicService mService;

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            mService = binder.getService();
            //mService.start();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
            Log.e("OnServiceDis", "");
            mService.stopSelf();
            mService = null;
        }
    };

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        ch = new ContextHelper(getContext(), getActivity().getContentResolver());
        MenuInflater mi = new MenuInflater(getContext());
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        TextView tx = (TextView) info.targetView.findViewById(R.id.label);
        TextView tx2 = (TextView) info.targetView.findViewById(R.id.songpath);
        // ListView  currentView  = (ListView)v.findViewById(R.id.listViewSongs);
        currentSelectedSong = tx2.getText().toString();
        selectedSong = tx.getText().toString();
        //  long selectedWordId = info.id;
        menu.setHeaderIcon(R.drawable.ic_action_edit);
        menu.setHeaderTitle(selectedSong);
        s = selectedSong;
        mi.inflate(R.menu.menu_options, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        setRetainInstance(true);
        ArrayAdapter<MusicRetriever.Item> adapterSon;
        ContentResolver cr = getActivity().getContentResolver();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());

        final List<MusicRetriever.Item> l = MusicRetriever.loadingSongs(cr, sharedPref.getBoolean("order", true));
        ListView lv3 = (ListView) view.findViewById(R.id.listViewSongs);
      /*  lv3.setOnScrollListener(new OnVerticalScrollDirectionListener() {
            @Override
            public void onVerticalDirectionChanged(OnVerticalScrollDirectionListener.ListenerArgs args) {
                if (args.isScrollable()) {
                    if (!args.isTopItemReached()) {
                        if (args.isScrollingUp()) {
                            listener.hide(false);
                        } else {
                           listener.hide(true);
                        }
                    }
                }
            }
        });*/
        adapterSon = new SongListAdapter(getActivity(), new ArrayList<MusicRetriever.Item>());
        lv3.setAdapter(adapterSon);
        lv3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getContext(), PlayerActivity.class);
                // ContentResolver cr = getActivity().getContentResolver();
                MusicRetriever.Item is = l.get(position);
                String path = is.getPath();
                i.putExtra("path", path);

                ArrayList<String> albumPaths = new ArrayList<>();
                for (int x = position, y = 0; x < l.size() && y < 100; x++, y++) {
                    albumPaths.add(l.get(x).getPath());
                }
                startNewPlayer(path, albumPaths, view);
            }
        });

        for (MusicRetriever.Item i : l) {
            adapterSon.add(i);
        }
        registerForContextMenu(lv3);

        return view;
    }

    private void startNewPlayer(String s, ArrayList<String> albumPaths, View v) {
        ImageView im = (ImageView) v.findViewById(R.id.icono);
        Intent i = new Intent(getContext(), PlayerActivity.class);
        i.putExtra("path", s);
        i.putStringArrayListExtra("albumpaths", albumPaths);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), im, "alb");
        ActivityCompat.startActivity(getActivity(), i, options.toBundle());
        // startActivity(i);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Intent intent = new Intent(getActivity(), MusicService.class);
        intent.putExtra("s", false);
        intent.putExtra("nottype", PreferenceManager.getDefaultSharedPreferences(getContext()).getString("nottype", "2"));
        getActivity().getApplicationContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    public void onDestroy() {
        getActivity().getApplicationContext().unbindService(mConnection);
        super.onDestroy();
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addTo:
                Log.e("OPTION", "ADD");
                ch.addToPlayList(currentSelectedSong);
                break;
            case R.id.Delete:
                Log.e("OPCION", "DELETE");
                if (ch.deleteSong(currentSelectedSong)) {
                    Toast.makeText(getContext(), "Ok", Toast.LENGTH_LONG).show();
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemSelectedListener) {
            listener = (OnItemSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement MyListFragment.OnItemSelectedListener");
        }
    }

    public void hide(boolean a) {
        listener.hide(a);
    }

    public interface OnItemSelectedListener {
        void hide(boolean a);
    }
}