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

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Quenthel on 14/03/2016.
 */
public class SongListAdapter extends ArrayAdapter<MusicRetriever.Item> {
    private Activity ctx;

    public SongListAdapter(Activity context, List<MusicRetriever.Item> l) {
        super(context, R.layout.rows, l);
        this.ctx = context;
    }

    @Override
    public View getView(int posicion, View view, ViewGroup parent) {
        final MusicRetriever.Item actual = this.getItem(posicion);
        View vi = view;
        if (vi == null) {
            vi = ctx.getLayoutInflater().inflate(R.layout.rows, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.tv = (TextView) vi.findViewById(R.id.label);
            holder.tv2 = (TextView) vi.findViewById(R.id.grupo);
            holder.tv3 = (TextView) vi.findViewById(R.id.tDuration);
            holder.tv4 = (TextView) vi.findViewById(R.id.songpath);
            holder.img = (ImageView) vi.findViewById(R.id.icono);
            vi.setTag(holder);
        }
        long i = actual.getDuration();
        String dura = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(i), TimeUnit.MILLISECONDS.toSeconds(i) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(i)));
        ViewHolder holder = (ViewHolder) vi.getTag();
        holder.tv.setText(actual.getTitle());
        holder.tv2.setText(actual.getArtist());
        holder.tv3.setText(dura);
        holder.tv4.setText(actual.getPath());
        if (actual.getAlbumArtPath() != null) {
            Glide.with(ctx).load(actual.getAlbumArtPath()).asBitmap().into(holder.img);
        } else {
            Glide.with(ctx).load(R.drawable.nodata).asBitmap().into(holder.img);
        }
        // setup(view, actual);
        return vi;
    }

    //TODO
    public void setup(View view, MusicRetriever.Item s) {
        TextView tv = (TextView) view.findViewById(R.id.label);
        tv.setText(s.getTitle());
        final TextView tv2 = (TextView) view.findViewById(R.id.grupo);
        tv2.setText(s.getArtist());
        Bitmap bit = null;
        ImageView album = (ImageView) view.findViewById(R.id.icono);
        //----------
        /*Palette.from(ImageHelper.decodeSampledBitmapFromResource(25, 25, s.getAlbumArtPath())).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated( Palette palette ) {
                //work with the palette here
                tv2.setTextColor(palette.getDarkVibrantColor(Color.RED));
            }
        });*/
        //----------
        // Bitmap bit=null;
        // ImageView album = (ImageView)view.findViewById(R.id.icono);
        //  loadBitmap(s.getPath(), album);
      /*  if(s.getAlbum()==previousPath){
            previousPath=s.getAlbum();
            bit=anterior;
        }
        else{
            bit =ImageHelper.decodeSampledBitmapFromResource(50, 50, s.getAlbumArtPath());
            anterior=bit;
            previousPath=s.getAlbum();
        }*/

        //album.setImageBitmap(bit);
        //   loadBitmap(s.getAlbumArtPath(), album);
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

    static class ViewHolder {
        TextView tv;
        TextView tv2;
        TextView tv3;
        TextView tv4;
        ImageView img;
    }
}
