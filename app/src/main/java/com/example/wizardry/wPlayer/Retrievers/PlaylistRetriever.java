package com.example.wizardry.wPlayer.Retrievers;

/**
 * Created by Witchery on 4/16/2016.
 */

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlaylistRetriever {
    static final String TAG = "PlayList";
    ContentResolver mContentResolver;
    List<Item> mItems = new ArrayList<>();
    Random mRandom = new Random();

    public PlaylistRetriever(ContentResolver cr) {
        mContentResolver = cr;
    }

    public static List<Item> loadingSongs(ContentResolver cr2) {
        PlaylistRetriever mr = new PlaylistRetriever(cr2);
        mr.prepare();
        // getPlaylistTask task = new getPlaylistTask(cr2);

        List<PlaylistRetriever.Item> mItems = mr.mItems;
        //for(MusicRetriever.Item i : mItems){
        //   adapterSon.add(new Song(i.getTitle(), "2:38", i.getArtist(), i.getPath(), null));
        //   }
       /* try {
            return task.execute("").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;*/
        return mr.mItems;
    }

    public void prepare() {
        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        Log.i(TAG, "Querying Playlist...");
        Log.i(TAG, "URI: " + uri.toString());

        Cursor cur = mContentResolver.query(uri, null,
                null, null, null);
        Log.i(TAG, "Query finished. " + (cur == null ? "Returned NULL." : "Returned a cursor."));
        if (cur == null) {
            // Query failed...
            Log.e(TAG, "Failed to retrieve playlist: cursor is null :-(");
            return;
        }
        if (!cur.moveToFirst()) {
            // Nothing to query. There is no music on the device. How boring.
            Log.e(TAG, "Failed to move cursor to first row (no query results).");
            return;
        }
        //  Log.i(TAG, "Listing...");
        // retrieve the indices of the columns where the ID, title, etc. of the song are
        int name = cur.getColumnIndex(MediaStore.Audio.Playlists.NAME);
        int id = cur.getColumnIndex(MediaStore.Audio.Playlists._ID);
        int whatever = cur.getColumnIndex(MediaStore.Audio.Playlists.DATA);

        // Log.i(TAG, "Title column index: " + String.valueOf(name));
        // Log.i(TAG, "ID column index: " + String.valueOf(id));
        // add each song to mItems
        do {
            //   Log.i(TAG, "ID: " + cur.getString(id) + " Title: " + cur.getString(name) + " " + cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA)));
            mItems.add(new Item(
                    cur.getLong(id),
                    cur.getString(name),
                    cur.getString(whatever),
                    "Esto Sobra"
                    //   aBlazing(cur.getLong(id))
            ));

        } while (cur.moveToNext());
        Log.i(TAG, "Done querying media. PlayList is ready.");
        cur.close();
    }


    //Metodo que devuelve los items pertenecientes a una playlist
    public ArrayList<MusicRetriever.Item> aBlazing(long playlistID) {
        ArrayList<MusicRetriever.Item> blaze = new ArrayList<>();
        Cursor c = getContentResolver().query(MediaStore.Audio.Playlists.Members.getContentUri("external", playlistID),
                null,
                null,
                null,
                null);
        if (c == null) {
            Log.e(TAG, "Failed to retrieve playlist: cursor is null :-(");
        }
        if (!c.moveToFirst()) {
            Log.e(TAG, "Failed to move cursor to first row (no query results).");
        }
        //    Log.i(TAG, "Listing...");
        //
        int artistColumn = c.getColumnIndex(MediaStore.Audio.Playlists.Members.ARTIST);
        int titleColumn = c.getColumnIndex(MediaStore.Audio.Playlists.Members.TITLE);
        int albumColumn = c.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM);
        int durationColumn = c.getColumnIndex(MediaStore.Audio.Playlists.Members.DURATION);
        int idColumn = c.getColumnIndex(MediaStore.Audio.Playlists.Members._ID);
        int albumKey = c.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM_KEY);
        AlbumRetriever albr = new AlbumRetriever(mContentResolver);

        do {
            //    Log.i(TAG, "Ablazing : " + " Title: " + c.getString(titleColumn) + " ");
            if (c.getCount() > 0) {
                blaze.add(new MusicRetriever.Item(
                        c.getLong(idColumn),
                        c.getString(artistColumn),
                        c.getString(titleColumn),
                        c.getString(albumColumn),
                        c.getLong(durationColumn),
                        c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA)),
                        albr.getAlbumArtForSongId(c.getString(albumKey))
                ));
            }
        } while (c.moveToNext());
        //  Log.i(TAG, "Ablazed");
        c.close();
        return blaze;
    }

    public ContentResolver getContentResolver() {
        return mContentResolver;
    }

    public Item getRandomItem() {
        if (mItems.size() <= 0) return null;
        return mItems.get(mRandom.nextInt(mItems.size()));
    }

    public static class Item {
        long id;
        String name;
        String whatever;
        String path;
        //  ArrayList<MusicRetriever.Item> songs;

        public Item(long id, String name, String whatever, String path) {
            this.id = id;
            this.name = name;
            this.whatever = whatever;
            this.path = path;
            //   this.songs = lista;
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        // public ArrayList<MusicRetriever.Item> getSongs() {
        ////  }

        public String getWhatever() {
            return whatever;
        }

        public String getPath() {
            return path;
        }

        public Uri getURI() {
            return ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
        }
    }
}