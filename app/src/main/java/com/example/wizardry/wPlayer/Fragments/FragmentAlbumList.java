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
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.wizardry.wPlayer.Activities.AlbumActivity;
import com.example.wizardry.wPlayer.Adapters.AlbumListAdapterRec;
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
        // final GridView g = (GridView) view.findViewById(R.id.gridView);
        final AlbumRetriever ab = new AlbumRetriever(getActivity().getContentResolver());
        final ArrayList<AlbumRetriever.ItemAlbum> l = ab.getAlbumList();
        final ArrayAdapter<AlbumRetriever.ItemAlbum> adapterSon;
        //  ArrayList<AlbumRetriever.ItemAlbum> l2 = new ArrayList<>();

        RecyclerView rvContacts = (RecyclerView) view.findViewById(R.id.listViewAlbum);
        AlbumListAdapterRec adapter = new AlbumListAdapterRec(getContext(), l);

        rvContacts.setAdapter(adapter);
        rvContacts.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rvContacts.setHasFixedSize(true);
        ItemClickSupport.addTo(rvContacts).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent i = new Intent(getContext(), AlbumActivity.class);
                String[] data = new String[4];
                data[0] = ((TextView) v.findViewById(R.id.tvItemAlbName)).getText().toString();
                data[1] = ((TextView) v.findViewById(R.id.chPath)).getText().toString();
                data[2] = ((TextView) v.findViewById(R.id.chYear)).getText().toString();
                data[3] = ((TextView) v.findViewById(R.id.chArtist)).getText().toString();

                i.putExtra("data", data);
                // i.putStringArrayListExtra("datos", datos);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), v, "alb");
                ActivityCompat.startActivity(getActivity(), i, options.toBundle());
            }
        });


     /*   adapterSon = new AlbumListAdapter(getActivity(), new ArrayList<AlbumRetriever.ItemAlbum>());
        g.setAdapter(adapterSon);
        g.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View cxz = view.findViewById(R.id.cxz);
                Log.e("Click", position + " " + id);
                Intent i = new Intent(getContext(), AlbumActivity.class);

                TextView t = (TextView) view.findViewById(R.id.tvItemAlbName);
                TextView t2 = (TextView) view.findViewById(R.id.chPath);
                TextView t3 = (TextView) view.findViewById(R.id.chYear);
                TextView t4 = (TextView) view.findViewById(R.id.chArtist);

                String s = t.getText().toString();
                String s2 = t2.getText().toString();
                String s3 = t3.getText().toString();
                String s4 = t4.getText().toString();
                String[] d = new String[4];
                d[0] = s;
                d[1] = s2;
                d[2] = s3;
                d[3] = s4;

                //------------
                i.putExtra("data", d);
                // i.putStringArrayListExtra("datos", datos);
                View v = view.findViewById(R.id.imItemAlb);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), v, "alb");
                ActivityCompat.startActivity(getActivity(), i, options.toBundle());
                //    startActivity(i);
            }
        });
        for (AlbumRetriever.ItemAlbum a : l) {
            adapterSon.add(new AlbumRetriever.ItemAlbum(a.getId(), a.getArtist(), a.getTitle(), "", a.getDuration(), a.getAlbumArt()));
        }*/
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