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

public class AlbumSongsRetriever {
    final String TAG = "AlbumSongsRetriever";
    ContentResolver mContentResolver;
    List<Item> mItems = new ArrayList<>();
    Random mRandom = new Random();
    String album = "";
    String sortOrder;

    public AlbumSongsRetriever(ContentResolver cr, String album) {
        mContentResolver = cr;
        this.album = album;
    }

    public void prepare() {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        sortOrder = MediaStore.Audio.Media.TRACK + " ASC";
   /*     if(album.contains("'")){
            Log.w("","");
            album = album.replace("'", "\'");
        }*/
        Log.i(TAG, "Querying media...");
        Log.i(TAG, "URI: " + uri.toString());
        //Cursor cur = mContentResolver.query(uri, null,
        //       MediaStore.Audio.Media.IS_MUSIC + " = 1", null, sortOrder);
        Cursor cur = mContentResolver.query(uri, null, MediaStore.Audio.Media.ALBUM + " = " + album + "", null, sortOrder);
        Log.i(TAG, "Query finished. " + (cur == null ? "Returned NULL." : "Returned a cursor."));
        if (cur == null) {
            Log.e(TAG, "Failed to retrieve music: cursor is null");
            return;
        }
        if (!cur.moveToFirst()) {
            return;
        }
        Log.i(TAG, "Listing...");
        int artistColumn = cur.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        int titleColumn = cur.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int albumColumn = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM);
        int durationColumn = cur.getColumnIndex(MediaStore.Audio.Media.DURATION);
        int songPos = cur.getColumnIndex(MediaStore.Audio.Media.TRACK);
        int idColumn = cur.getColumnIndex(MediaStore.Audio.Media._ID);

        do {
            //   if(cur.getString(albumColumn).equalsIgnoreCase(album)) {
            Log.i(TAG, "ID: " + cur.getString(idColumn) + " Title: " + cur.getString(titleColumn) + " " + cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA)));
            mItems.add(new Item(
                    cur.getLong(idColumn),
                    cur.getString(artistColumn),
                    cur.getString(titleColumn),
                    cur.getString(albumColumn),
                    cur.getLong(durationColumn),
                    cur.getString(songPos),
                    cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA))
            ));
            //  }
        } while (cur.moveToNext());
        Log.i(TAG, "Done querying media.");
        cur.close();
    }

    public ContentResolver getContentResolver() {
        return mContentResolver;
    }

    /**
     * Returns a random Item. If there are no items available, returns null.
     */
    public Item getRandomItem() {
        if (mItems.size() <= 0) return null;
        return mItems.get(mRandom.nextInt(mItems.size()));
    }

    public ArrayList<Item> loadingSongs() {
        ContentResolver c2r = this.getContentResolver();
        AlbumSongsRetriever mr = new AlbumSongsRetriever(c2r, album);
        mr.prepare();
        List<AlbumSongsRetriever.Item> mItems = mr.mItems;
        //for(MusicRetriever.Item i : mItems){
        //   adapterSon.add(new Song(i.getTitle(), "2:38", i.getArtist(), i.getPath(), null));
        //   }
        return (ArrayList<Item>) mItems;
    }

    public final static class Item {
        final long id;
        final String artist;
        final String title;
        final String album;
        final long duration;
        final String pa;
        String position;

        public Item(long id, String artist, String title, String album, long duration, String position, String a) {
            this.id = id;
            this.artist = artist;
            this.title = title;
            this.album = album;
            this.duration = duration;
            pa = a;
            this.position = position;
        }

        public long getId() {
            return id;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getArtist() {
            return artist;
        }

        public String getTitle() {
            return title;
        }

        public String getAlbum() {
            return album;
        }

        public long getDuration() {
            return duration;
        }

        public String getPath() {
            return pa;
        }

        public Uri getURI() {
            return ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
        }
    }
}

