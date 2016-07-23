package com.example.wizardry.pruebas.Adapters;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.wizardry.pruebas.Helpers.ImageHelper;
import com.example.wizardry.pruebas.R;
import com.example.wizardry.pruebas.Retrievers.AlbumSongsRetriever;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Quenthel on 14/03/2016.
 */

public class AlbumSongListAdapterRecycler extends RecyclerView.Adapter<AlbumSongListAdapterRecycler.ViewHolder> {

    private static int[] colorsArr;
    private static boolean light;
    private static boolean useD = false;
    private ArrayList<AlbumSongsRetriever.Item> list;
    private Activity ctx;
    private ArrayList<String> albumPaths = new ArrayList<>();
    private ArrayList<String> albumPaths2 = new ArrayList<>();


    public AlbumSongListAdapterRecycler(Activity context, ArrayList<AlbumSongsRetriever.Item> l) {
        this.ctx = context;
        this.list = l;
        light = false;
    }

    public AlbumSongListAdapterRecycler(Activity context, ArrayList<AlbumSongsRetriever.Item> l, int[] colors, boolean light, boolean useD) {
        this.ctx = context;
        colorsArr = colors;
        AlbumSongListAdapterRecycler.light = light;
        AlbumSongListAdapterRecycler.useD = useD;
        this.list = l;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowalbumlist, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AlbumSongsRetriever.Item item = list.get(position);
        holder.bindView(item);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv, tv2, tv3, tv4, tv5;

        ViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.label);
            tv2 = (TextView) itemView.findViewById(R.id.grupo);
            tv3 = (TextView) itemView.findViewById(R.id.tDuration);
            tv4 = (TextView) itemView.findViewById(R.id.songpath);
            tv5 = (TextView) itemView.findViewById(R.id.icono);
            if (colorsArr != null) {
           /* if (!light) {
                vi.setBackgroundColor(colorsArr[1]);
                holder.tv.setTextColor(colorsArr[0]);
                holder.tv2.setTextColor(colorsArr[2]);
                holder.tv3.setTextColor(colorsArr[2]);
           // } else */
                if (!useD) {
                    // vi.setBackgroundColor(colorsArr[2]);
                    tv.setTextColor(colorsArr[1]);
                    tv2.setTextColor(colorsArr[3]);
                    tv3.setTextColor(colorsArr[3]);
                    tv5.setTextColor(Color.DKGRAY);

                } else {
                    //   vi.setBackgroundColor(colorsArr[1]);
                    tv.setTextColor(colorsArr[0]);
                    tv2.setTextColor(colorsArr[2]);
                    tv3.setTextColor(colorsArr[2]);
                    tv5.setTextColor(Color.WHITE);
                }
                itemView.setBackground(ImageHelper.makeSelector(colorsArr[0], Color.TRANSPARENT, 100));
            }
        }

        void bindView(AlbumSongsRetriever.Item actual) {
            long i = actual.getDuration();
            String dura = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(i), TimeUnit.MILLISECONDS.toSeconds(i) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(i)));
            tv.setText(actual.getTitle());
            tv2.setText(actual.getArtist());
            tv3.setText(dura);
            tv4.setText(actual.getPath());
            tv5.setText(actual.getPosition());
        }
    }
}