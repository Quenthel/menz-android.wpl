package com.example.wizardry.wPlayer.Adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.wizardry.wPlayer.Async.BitmapWorkerTask;
import com.example.wizardry.wPlayer.R;
import com.example.wizardry.wPlayer.Retrievers.AlbumRetriever;

import java.util.List;

/**
 * Created by Quenthel on 14/03/2016.
 */
public class AlbumListAdapter extends ArrayAdapter<AlbumRetriever.ItemAlbum> {
    private Activity ctx;

    public AlbumListAdapter(Activity context, List<AlbumRetriever.ItemAlbum> l) {
        super(context, R.layout.rows, l);
        this.ctx = context;
    }

    @Override
    public View getView(int posicion, View view, ViewGroup parent) {
        View vi = view;
        ViewHolder holder;
        if (vi == null) {
            vi = ctx.getLayoutInflater().inflate(R.layout.rowalbum, parent, false);
            holder = new ViewHolder();
            holder.tv = (TextView) vi.findViewById(R.id.tvItemAlbName);
            holder.tv2 = (TextView) vi.findViewById(R.id.chArtist);
            holder.tv3 = (TextView) vi.findViewById(R.id.chYear);
            holder.tv4 = (TextView) vi.findViewById(R.id.chPath);
            holder.img = (ImageView) vi.findViewById(R.id.imItemAlb);
            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }
        AlbumRetriever.ItemAlbum i = getItem(posicion);
        //
        if (i.getAlbumArt() != null) {
            Glide.with(ctx).load(i.getAlbumArt()).asBitmap().into(holder.img);
            holder.tv4.setText(i.getAlbumArt());

        } else {
            Glide.with(ctx).load(R.drawable.nodata).asBitmap().into(holder.img);
            holder.tv4.setText("vac");
        }

        holder.tv.setText(i.getTitle());
        holder.tv2.setText(i.getArtist());
        holder.tv3.setText(Integer.toString(i.getDuration()));
        return vi;
    }

    @Override
    public int getPosition(AlbumRetriever.ItemAlbum item) {
        return super.getPosition(item);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }


    public void loadBitmap(String path, ImageView imageView) {
        BitmapWorkerTask task = new BitmapWorkerTask(imageView);
        task.execute(path);
    }

    public static class ViewHolder {
        TextView tv;
        TextView tv2;
        TextView tv3;
        TextView tv4;
        ImageView img;
    }
}

