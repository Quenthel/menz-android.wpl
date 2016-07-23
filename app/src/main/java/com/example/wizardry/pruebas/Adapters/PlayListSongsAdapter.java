package com.example.wizardry.pruebas.Adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.wizardry.pruebas.Async.ImageAsyncTask;
import com.example.wizardry.pruebas.R;
import com.example.wizardry.pruebas.Retrievers.MusicRetriever;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Quenthel on 14/03/2016.
 */
public class PlayListSongsAdapter extends ArrayAdapter<MusicRetriever.Item> {
    private Activity ctx;
    private Bitmap anterior = null;
    private String previousPath = ";";
    private ArrayList<String> albumPaths = new ArrayList<>();
    private ArrayList<String> albumPaths2 = new ArrayList<>();

    public PlayListSongsAdapter(Activity context, List<MusicRetriever.Item> l) {
        super(context, R.layout.rows, l);
        this.ctx = context;
    }

    @Override
    public View getView(int posicion, View view, ViewGroup parent) {
        if (view == null) {
            view = ctx.getLayoutInflater().inflate(R.layout.rows, parent, false);
        }
        final MusicRetriever.Item actual = this.getItem(posicion);
       /* view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // v.showContextMenu();
                return false;
            }
        });*/

        ImageView album = (ImageView) view.findViewById(R.id.icono);
        Glide.with(ctx).load(actual.getAlbumArtPath()).asBitmap().into(album);
        setup(view, actual);
        albumPaths.add(actual.getPath());

        return view;
    }

    //TODO
    public void setup(View view, MusicRetriever.Item s) {
        TextView tv = (TextView) view.findViewById(R.id.label);
        TextView tv2 = (TextView) view.findViewById(R.id.grupo);
        tv.setText(s.getTitle());
        tv2.setText(s.getAlbum());
        TextView tv3 = (TextView) view.findViewById(R.id.tDuration);
        long i = s.getDuration();
        String dura = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(i),
                TimeUnit.MILLISECONDS.toSeconds(i) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(i))
        );

        tv3.setText(dura);
        TextView tv4 = (TextView) view.findViewById(R.id.songpath);
        tv4.setText(s.getPath());
    }

    public void loadBitmap(String path, ImageView imageView) {
        ImageAsyncTask task = new ImageAsyncTask(getContext(), imageView);
        task.execute(path);
    }

}
