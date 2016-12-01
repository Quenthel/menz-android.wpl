package com.example.wizardry.wPlayer.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.wizardry.wPlayer.R;
import com.example.wizardry.wPlayer.Retrievers.PlaylistRetriever;

import java.util.List;

/**
 * Created by Wizardry on 22/08/2016.
 */
public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.ViewHolder> {
    private List<PlaylistRetriever.Item> mContacts;
    // Store the context for easy access
    private Context mContext;

    public PlayListAdapter(Context context, List<PlaylistRetriever.Item> contacts) {
        mContacts = contacts;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.rowslists, parent, false);

        // Return a new holder instance
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get the data model based on position
        PlaylistRetriever.Item contact = mContacts.get(position);

        // Set item views based on your views and data model
        TextView textView = holder.nameTextView;
        TextView textView2 = holder.nameTextView2;

        textView.setText(contact.getName());
        textView2.setText(contact.getWhatever());
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  implements View.OnCreateContextMenuListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView, nameTextView2;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.label);
            nameTextView2 = (TextView) itemView.findViewById(R.id.data);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            Log.e("sd","sd");

        }
    }

    public static class RecyclerViewContextMenuInfo implements ContextMenu.ContextMenuInfo {

        final public int position;
        final public long id;
        public RecyclerViewContextMenuInfo(int position, long id) {
            this.position = position;
            this.id = id;
        }
    }
}
