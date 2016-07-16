package com.example.wizardry.pruebas.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.wizardry.pruebas.Activities.PlayListActivity;
import com.example.wizardry.pruebas.R;
import com.example.wizardry.pruebas.Retrievers.MusicRetriever;
import com.example.wizardry.pruebas.Retrievers.PlaylistRetriever;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by Quenthel on 14/04/2016.
 */
public class PlayListAdapter extends ArrayAdapter<PlaylistRetriever.Item> {
    private Activity ctx;

    public PlayListAdapter(Activity context, List<PlaylistRetriever.Item> l){
        super(context, R.layout.rowslists, l);
        this.ctx= context;
    }

    @Override
    public View getView(int posicion, View view, ViewGroup parent){
        if(view == null){
            view = ctx.getLayoutInflater().inflate(R.layout.rowslists, parent, false);

        }
        final PlaylistRetriever.Item actual = this.getItem(posicion);
        /*view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<MusicRetriever.Item> a = new PlaylistRetriever(ctx.getContentResolver()).aBlazing(actual.getId());
                Intent i = new Intent(ctx, PlayListActivity.class);
                i.putExtra("name", actual.getName());
                i.putExtra("songs", a);
                i.putExtra("playpath", actual.getWhatever());
                ctx.startActivity(i);
            }
        });*/
        setup(view, actual);

        return view;
    }
    //TODO
    public void setup(View view, final PlaylistRetriever.Item s){
        TextView name=(TextView)view.findViewById(R.id.label);
        TextView data =(TextView)view.findViewById(R.id.data);
        name.setText(s.getName());
        data.setText(s.getWhatever());
    }
}
