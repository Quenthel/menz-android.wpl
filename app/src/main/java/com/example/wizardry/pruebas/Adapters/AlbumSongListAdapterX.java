package com.example.wizardry.pruebas.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.wizardry.pruebas.Activities.PlayerActivity;
import com.example.wizardry.pruebas.Helpers.ImageHelper;
import com.example.wizardry.pruebas.R;
import com.example.wizardry.pruebas.Retrievers.AlbumSongsRetriever;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Quenthel on 14/03/2016.
 */
public class AlbumSongListAdapterX extends ArrayAdapter<AlbumSongsRetriever.Item> {
    private Activity ctx;
    private int[] colorsArr;
    private boolean light = false;
    private boolean useD = false;
    private ArrayList<String> albumPaths = new ArrayList<>();
    private ArrayList<String> albumPaths2 = new ArrayList<>();

    public AlbumSongListAdapterX(Activity context, List<AlbumSongsRetriever.Item> l) {
        super(context, R.layout.rowalbumlist, l);
        this.ctx = context;
    }

    public AlbumSongListAdapterX(Activity context, List<AlbumSongsRetriever.Item> l, int[] colors, boolean light, boolean useD ){
        super(context, R.layout.rowalbumlist, l);
        this.ctx = context;
        this.colorsArr = colors;
        this.light = light;
        this.useD = useD;
    }

    @Override
    public View getView(int posicion, View v, ViewGroup parent) {
        View vi = v;

        if (vi == null) {
            vi = ctx.getLayoutInflater().inflate(R.layout.rowalbumlist, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.tv = (TextView) vi.findViewById(R.id.label);
            holder.tv2 = (TextView) vi.findViewById(R.id.grupo);
            holder.tv3 = (TextView) vi.findViewById(R.id.tDuration);
            holder.tv4 = (TextView) vi.findViewById(R.id.songpath);
            holder.tv5 = (TextView) vi.findViewById(R.id.icono);
            vi.setTag(holder);
        }

        final AlbumSongsRetriever.Item actual = this.getItem(posicion);
        final ViewHolder holder = (ViewHolder) vi.getTag();
        long i = actual.getDuration();
        String dura = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(i), TimeUnit.MILLISECONDS.toSeconds(i) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(i)));
        holder.tv.setText(actual.getTitle());
        holder.tv2.setText(actual.getArtist());
        holder.tv3.setText(dura);
        holder.tv4.setText(actual.getPath());
        holder.tv5.setText(actual.getPosition());

        albumPaths.add(actual.getPath());
        vi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ctx, PlayerActivity.class);
                // ContentResolver cr = ctx.getContentResolver();
                String path = actual.getPath();
                i.putExtra("path", path);
                blackMagic();
                i.putStringArrayListExtra("albumpaths", albumPaths2);
                ctx.startActivity(i);
            }
        });
        if (colorsArr != null) {
           /* if (!light) {
                vi.setBackgroundColor(colorsArr[1]);
                holder.tv.setTextColor(colorsArr[0]);
                holder.tv2.setTextColor(colorsArr[2]);
                holder.tv3.setTextColor(colorsArr[2]);
           // } else */
            if (!useD) {
                // vi.setBackgroundColor(colorsArr[2]);
                holder.tv.setTextColor(colorsArr[1]);
                holder.tv2.setTextColor(colorsArr[3]);
                holder.tv3.setTextColor(colorsArr[3]);
                holder.tv5.setTextColor(Color.DKGRAY);

            } else {
                //   vi.setBackgroundColor(colorsArr[1]);
                holder.tv.setTextColor(colorsArr[0]);
                holder.tv2.setTextColor(colorsArr[2]);
                holder.tv3.setTextColor(colorsArr[2]);
                holder.tv5.setTextColor(Color.WHITE);
            }
            vi.setBackground(ImageHelper.makeSelector(colorsArr[0], Color.TRANSPARENT, 100));
        }
        return vi;
    }

    private void blackMagic() {
        for (String x : albumPaths) {
            if (!albumPaths2.contains(x)) {
                albumPaths2.add(x);
            }
        }
    }


    public void setViewSwatch(TextView view, Palette.Swatch swatch) {
        if (swatch != null) {
            view.setTextColor(swatch.getTitleTextColor());
            view.setBackgroundColor(swatch.getRgb());
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    static class ViewHolder {
        TextView tv, tv2, tv3, tv4, tv5;

    }

}