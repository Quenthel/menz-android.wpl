package com.example.wizardry.wPlayer.Fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.wizardry.wPlayer.Activities.PlayerActivity;
import com.example.wizardry.wPlayer.Adapters.SongListAdapter;
import com.example.wizardry.wPlayer.Helpers.ItemClickSupport;
import com.example.wizardry.wPlayer.MusicService;
import com.example.wizardry.wPlayer.R;
import com.example.wizardry.wPlayer.Retrievers.MusicRetriever;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wizardry on 19/04/2016.
 */

public class FragmentSongList extends Fragment {
    //  private OnItemSelectedListener listener;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        setRetainInstance(true);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        final List<MusicRetriever.Item> l = MusicRetriever.loadingSongs(getActivity().getContentResolver(), sharedPref.getBoolean("order", true));
        //    SongListAdapter adapter;
        RecyclerView rvContacts = (RecyclerView) view.findViewById(R.id.listViewSongsL);

        // adapter = new SongListAdapter(getContext(), l);
        rvContacts.setAdapter(new SongListAdapter(getActivity().getApplicationContext(), l));
        rvContacts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvContacts.setHasFixedSize(true);

        ItemClickSupport.addTo(rvContacts).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                MusicRetriever.Item is = l.get(position);
                ArrayList<String> albumPaths = new ArrayList<>();
                for (int x = position, y = 0; x < l.size() && y < 100; x++, y++) {
                    albumPaths.add(l.get(x).getPath());
                }
                startNewPlayer(is.getPath(), albumPaths, v);
            }
        });

       /* ItemClickSupport.addTo(rvContacts).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
                return false;
            }
        });*/

    /*   adapterSon = new SongListAdapter(getActivity(), new ArrayList<MusicRetriever.Item>());
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
        registerForContextMenu(lv3);*/

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Intent intent = new Intent(getActivity(), MusicService.class);
        intent.putExtra("s", false);
        //  intent.putExtra("nottype", PreferenceManager.getDefaultSharedPreferences(getContext()).getString("nottype", "2"));
        getActivity().getApplicationContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    public void onDestroy() {
        getActivity().getApplicationContext().unbindService(mConnection);
        super.onDestroy();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

  /*  @Override
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
    }*/
}