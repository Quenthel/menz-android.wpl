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
import com.example.wizardry.wPlayer.Retrievers.AlbumRetriever;

import java.util.List;

/**
 * Created by Wizardry on 23/08/2016.
 */

public class AlbumListAdapterRec extends RecyclerView.Adapter<AlbumListAdapterRec.ViewHolder> {

    private List<AlbumRetriever.ItemAlbum> mContacts;
    // Store the context for easy access
    private Context mContext;


    public AlbumListAdapterRec(Context context, List<AlbumRetriever.ItemAlbum> contacts) {
        mContacts = contacts;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View contactView = inflater.inflate(R.layout.rowalbum, parent, false);
        // Return a new holder instance
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AlbumRetriever.ItemAlbum i = mContacts.get(position);
        //
        if (i.getAlbumArt() != null) {
            Glide.with(mContext).load(i.getAlbumArt()).crossFade().into(holder.img);
            holder.tv4.setText(i.getAlbumArt());

        } else {
            Glide.with(mContext).load(R.drawable.nodata).crossFade().into(holder.img);
            holder.tv4.setText("vac");
        }

        holder.tv.setText(i.getTitle());
        holder.tv2.setText(i.getArtist());
        holder.tv3.setText(Integer.toString(i.getDuration()));
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
        TextView tv3;
        TextView tv4;
        ImageView img;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        ViewHolder(View itemView) {

            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tvItemAlbName);
            tv2 = (TextView) itemView.findViewById(R.id.chArtist);
            tv3 = (TextView) itemView.findViewById(R.id.chYear);
            tv4 = (TextView) itemView.findViewById(R.id.chPath);
            img = (ImageView) itemView.findViewById(R.id.imItemAlb);
        }
    }
}
