package com.example.wizardry.wPlayer.Async;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.example.wizardry.wPlayer.Retrievers.PlaylistRetriever;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wizardry on 01/06/2016.
 */
public class getPlaylistTask extends AsyncTask<String, Integer, List<PlaylistRetriever.Item>> {
    ContentResolver ctx;

    public getPlaylistTask(ContentResolver context) {
        ctx = context;
    }

    @Override
    protected List<PlaylistRetriever.Item> doInBackground(String... params) {
        List<PlaylistRetriever.Item> items = new ArrayList<>();
        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        Cursor cur = ctx.query(uri, null, null, null, null);

        int name = cur.getColumnIndex(MediaStore.Audio.Playlists.NAME);
        int id = cur.getColumnIndex(MediaStore.Audio.Playlists._ID);
        int whatever = cur.getColumnIndex(MediaStore.Audio.Playlists.DATA);

        do {
            items.add(new PlaylistRetriever.Item(
                    cur.getLong(id),
                    cur.getString(name),
                    cur.getString(whatever),
                    "Esto Sobra"
            ));

        } while (cur.moveToNext());
        cur.close();
        return items;
    }

    @Override
    protected void onPostExecute(List<PlaylistRetriever.Item> x) {
        super.onPostExecute(x);
    }
}