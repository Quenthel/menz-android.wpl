package com.example.wizardry.wPlayer.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wizardry.wPlayer.Activities.AlbumActivity;
import com.example.wizardry.wPlayer.Adapters.AlbumGridAdapter;
import com.example.wizardry.wPlayer.Helpers.ItemClickSupport;
import com.example.wizardry.wPlayer.R;
import com.example.wizardry.wPlayer.Retrievers.AlbumRetriever;

import java.util.ArrayList;

/**
 * Created by Wizardry on 19/04/2016.
 */
public class FragmentAlbumList extends Fragment {
    //private int numGrids = 3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_grid, container, false);
        final AlbumRetriever ab = new AlbumRetriever(getActivity().getContentResolver());
        final ArrayList<AlbumRetriever.ItemAlbum> l = ab.getAlbumList();
        RecyclerView rvContacts = (RecyclerView) view.findViewById(R.id.listViewAlbum);
        AlbumGridAdapter adapter = new AlbumGridAdapter(getContext(), l);

        rvContacts.setAdapter(adapter);
        rvContacts.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rvContacts.setHasFixedSize(true);
        ItemClickSupport.addTo(rvContacts).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent i = new Intent(getContext(), AlbumActivity.class);
                String[] data = new String[4];
                AlbumRetriever.ItemAlbum item = l.get(position);
                data[0] = item.getTitle();
                data[1] = item.getAlbumArt();
                data[2] = Integer.toString(item.getDuration());
                data[3] = item.getArtist();
                i.putExtra("data", data);
                // i.putStringArrayListExtra("datos", datos);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), v, "alb");
                       /* } else {
            Pair<View, String> p1 = Pair.create((View) ddd, "alb");
            Pair<View, String> p2 = Pair.create(b, "now");
            options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1, p2);
        }*/
                ActivityCompat.startActivity(getActivity(), i, options.toBundle());
            }
        });
        return view;
    }

   /* @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            numGrids = 4;
            //     Log.e("SD", "SD");
        } else {
            numGrids = 3;

        }
        super.onConfigurationChanged(newConfig);
    }*/
}