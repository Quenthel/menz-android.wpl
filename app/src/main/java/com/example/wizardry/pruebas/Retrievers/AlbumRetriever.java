package com.example.wizardry.pruebas.Retrievers;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

public class AlbumRetriever {
    final String TAG = "AlbumRetriever";
    ContentResolver mContentResolver;
    ArrayList<ItemAlbum> mItems = new ArrayList<>();
    Hashtable<String, String> mapa = new Hashtable<>();
    Random mRandom = new Random();
    String sortOrder;

    public AlbumRetriever(ContentResolver cr) {
        mContentResolver = cr;
    }

    public ArrayList<ItemAlbum> getAlbumList() {
        sortOrder = MediaStore.Audio.Albums.ARTIST + " ASC";
        ContentResolver musicResolver = mContentResolver;
        Uri musicUri = android.provider.MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, sortOrder);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
            int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Albums._ID);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST);
            int albumArt = musicCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);
            int albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
            int durationColumn = musicCursor.getColumnIndex((MediaStore.Audio.Albums.FIRST_YEAR));
            do {
                mItems.add(new ItemAlbum(
                        musicCursor.getString(idColumn),
                        musicCursor.getString(artistColumn),
                        musicCursor.getString(titleColumn),
                        musicCursor.getString(albumColumn),
                        musicCursor.getInt(durationColumn),
                        musicCursor.getString(albumArt))
                );
            } while (musicCursor.moveToNext());
            //  Log.i(TAG, "Done querying albums. AlbumRetriever is ready.");
            musicCursor.close();
        }
        return mItems;
    }

    public ArrayList<ItemAlbum> getAlbums(String alb) {
        sortOrder = MediaStore.Audio.Albums.ARTIST + " ASC";
        ContentResolver musicResolver = mContentResolver;
        Uri musicUri = android.provider.MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, sortOrder);
        if (musicCursor != null && musicCursor.moveToFirst()) {

            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
            int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Albums._ID);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST);
            int albumArt = musicCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);
            int albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
            int durationColumn = musicCursor.getColumnIndex((MediaStore.Audio.Albums.FIRST_YEAR));

            do {
                // Log.i(TAG, "ID: " + musicCursor.getString(titleColumn) + " Artist" + musicCursor.getString(artistColumn));
                // Log.i(TAG, "ID: " + cur.getString(idColumn) + " Title: " + cur.getString(titleColumn) + " " + cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA)));
                if (musicCursor.getString(titleColumn).contains(alb)) {
                    mItems.add(new ItemAlbum(
                            musicCursor.getString(idColumn),
                            musicCursor.getString(artistColumn),
                            musicCursor.getString(titleColumn),
                            musicCursor.getString(albumColumn),
                            musicCursor.getInt(durationColumn),
                            musicCursor.getString(albumArt))
                    );
                    //    public ItemAlbum(String id, String artist, String title, String album, String duration, String albumArt) {

                }
            } while (musicCursor.moveToNext());
            // Log.i(TAG, "Done querying albums. AlbumRetriever is ready.");
            musicCursor.close();
        }
        return mItems;
    }

    public ContentResolver getContentResolver() {
        return mContentResolver;
    }

    /**
     * Returns a random Item. If there are no items available, returns null.
     */
    public ItemAlbum getRandomItem() {
        if (mItems.size() <= 0) return null;
        return mItems.get(mRandom.nextInt(mItems.size()));
    }

    //Metodo que devuelve el path en cache para un determinado album
    public String getAlbumArtForSongId(String id) {
        if (mapa.isEmpty()) {
            createHast();
        }
        return mapa.get(id);
    }

    //Metodo auxiliar para mapear las keys y paths para la lista principal
    private void createHast() {
        ContentResolver musicResolver = mContentResolver;
        Uri musicUri = android.provider.MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            int albumKey = musicCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_KEY);
            int albumArt = musicCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);
            int albbb = musicCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
            do {
                String key = musicCursor.getString(albumKey);
                String art = musicCursor.getString(albumArt);

                if (art != null) {
                    mapa.put(key, art);
                } else {
                    Log.e(TAG, "Sin art: " + musicCursor.getString(albbb));
                }

            } while (musicCursor.moveToNext());
            //   Log.i(TAG, "Done querying albums. AlbumRetriever is ready.");
            musicCursor.close();
        }
    }

    public final static class ItemAlbum {
        final String id;
        final String artist;
        final String title;
        final String album;
        final int duration;
        final String albumArt;

        public ItemAlbum(String id, String artist, String title, String album, int duration, String albumArt) {
            this.id = id;
            this.artist = artist;
            this.title = title;
            this.album = album;
            this.duration = duration;
            this.albumArt = albumArt;
        }

        public String getAlbumArt() {
            return albumArt;
        }

        public String getId() {
            return id;
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

        public int getDuration() {
            return duration;
        }

        public Uri getURI() {
            return ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.parseLong(id));
        }
    }

}


