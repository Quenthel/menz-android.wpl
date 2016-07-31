package com.example.wizardry.wPlayer.Helpers;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.wizardry.wPlayer.Activities.DataActivity;
import com.example.wizardry.wPlayer.Retrievers.PlaylistRetriever;
import com.example.wizardry.wPlayer.Retrievers.RetrieverHelper;

import java.io.File;
import java.util.List;

/**
 * Created by Witchery on 25/5/16.
 * Clase para organizar las distintas opciones de los menus contextuales de las activities.
 */
public class ContextHelper {
    private Context context;
    private ContentResolver content;

    public ContextHelper(Context context, ContentResolver content) {
        this.content = content;
        this.context = context;
    }


    public void editInfo(String path) {
        Intent i = new Intent(context, DataActivity.class);
        i.putExtra("path", path);
        context.startActivity(i);
    }

    public void setRingRing(String path, String selectedSong) {
        RetrieverHelper.setRingtone(context, path, selectedSong);
        Toast.makeText(context, "Ok", Toast.LENGTH_SHORT).show();
    }

    public void addToPlayList(final String path) {

        List<PlaylistRetriever.Item> mItems = PlaylistRetriever.loadingSongs(content);
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setTitle("...");


        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                context,
                android.R.layout.select_dialog_singlechoice);
        for (PlaylistRetriever.Item i : mItems) {
            arrayAdapter.add(i.getName());
        }
        builderSingle.setNegativeButton(
                "cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String strName = arrayAdapter.getItem(which);
                        AlertDialog.Builder builderInner = new AlertDialog.Builder(
                                context);
                        builderInner.setMessage(strName);
                        // builderInner.setTitle("Your Selected Item is");
                        builderInner.setPositiveButton(
                                "Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {
                                        //RetrieverHelper.addToPlaylist(resolver,path,strName);
                                        int audioid = (int) RetrieverHelper.getSongIdFromMediaStore(path, content);
                                        Toast.makeText(context, Integer.toString(audioid), Toast.LENGTH_SHORT).show();
                                        RetrieverHelper.addToPlaylist(content, audioid, strName);
                                        dialog.dismiss();
                                    }
                                });
                        builderInner.show();
                    }
                });
        builderSingle.show();
    }


   /* public void deleteFromPla(String song, String play) {
        FragmentPlaylist fpl = new FragmentPlaylist();
        RetrieverHelper.deleteSongFromPlaylist(fpl.getContentResolver(), song, play);
    }*/

    public boolean deleteSong(String path) {
        final File f = new File(path);
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Deleting")
                .setMessage("Delete " + path + " ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (f.exists()) {
                            if (RetrieverHelper.deleteFromMediaStore(f, content)) {
                                Toast.makeText(context, "File deleted", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                })
                .setNegativeButton("No", null)
                .show();
        return true;
    }

}