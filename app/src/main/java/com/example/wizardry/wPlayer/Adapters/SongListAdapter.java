package com.example.wizardry.wPlayer.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.wizardry.wPlayer.R;
import com.example.wizardry.wPlayer.Retrievers.MusicRetriever;

import java.util.List;

/**
 * Created by Wizardry on 23/08/2016.
 */

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.ViewHolder> {

    private List<MusicRetriever.Item> mContacts;
    private Context mContext;

    public SongListAdapter(Context context, List<MusicRetriever.Item> contacts) {
        mContacts = contacts;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
      //  View contactView = inflater.inflate(R.layout.rows, parent, false);
        return new ViewHolder(inflater.inflate(R.layout.rows, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MusicRetriever.Item actual = mContacts.get(position);

        //  long i = actual.getDuration();
        holder.tv.setText(actual.getTitle());
        holder.tv2.setText(actual.getArtist());
        // holder.tv.setTypeface(MetadataSingle.INSTANCE.type);
        //  holder.tv3.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(i), TimeUnit.MILLISECONDS.toSeconds(i) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(i))));
        holder.tv4.setText(actual.getPath());
        if (actual.getAlbumArtPath() != null) {
            Glide.with(mContext).load(actual.getAlbumArtPath()).into(holder.img);

        } else {
            Glide.with(mContext).load(R.drawable.nodata).into(holder.img);
        }
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        TextView tv;
        TextView tv2;
        //      TextView tv3;
        TextView tv4;
        ImageView img;

        ViewHolder(View vi) {
            super(vi);
            tv = (TextView) vi.findViewById(R.id.label);
            tv2 = (TextView) vi.findViewById(R.id.grupo);
            // tv3 = (TextView) vi.findViewById(R.id.tDuration);
            tv4 = (TextView) vi.findViewById(R.id.songpath);
            img = (ImageView) vi.findViewById(R.id.icono);
        }
    }
}
