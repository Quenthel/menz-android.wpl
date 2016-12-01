package com.example.wizardry.wPlayer.Retrievers;

/**
 * Created by Witchery on 4/16/2016.
 */

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MusicRetriever {
    static final String TAG = "MusicRetriever";
    static final Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    ContentResolver mContentResolver;
    List<Item> mItems = new ArrayList<>();
    Random mRandom = new Random();
    String sortOrder;

    public MusicRetriever(ContentResolver cr) {
        mContentResolver = cr;
        sortOrder = MediaStore.Audio.Media.ALBUM + " ASC, " + MediaStore.Audio.Media.ARTIST + " ASC";
    }

    public MusicRetriever(ContentResolver cr, Boolean order) {
        mContentResolver = cr;
        if (order)
            sortOrder = MediaStore.Audio.Media.ALBUM + " ASC, " + MediaStore.Audio.Media.ARTIST + " ASC";
        else {
            sortOrder = MediaStore.Audio.Media.ARTIST + " ASC, " + MediaStore.Audio.Media.ALBUM + " ASC";
        }
    }

    public static List<Item> loadingSongs(ContentResolver cr2, boolean otd) {
        MusicRetriever mr = new MusicRetriever(cr2, otd);
        mr.prepare();
        return mr.mItems;
    }

    public static List<Item> loadingSongs(ContentResolver cr2) {
        MusicRetriever mr = new MusicRetriever(cr2);
        mr.prepare();
        return mr.mItems;
    }

    public static Item loadingRandomSong(ContentResolver cr2) {
        MusicRetriever mr = new MusicRetriever(cr2);
        mr.prepare();
        List<MusicRetriever.Item> mItems = mr.mItems;
        Random mRandom = new Random();
        if (mItems.size() <= 0) return null;
        return mItems.get(mRandom.nextInt(mItems.size()));
    }

    public void prepare() {
        Cursor cur = mContentResolver.query(uri, null, MediaStore.Audio.Media.IS_MUSIC + " = 1", null, sortOrder);
        //    Log.i(TAG, "Query finished. " + (cur == null ? " NULL." : "Returned a cursor."));
        if (cur == null) {
            Log.e(TAG, "null");
            return;
        }
        if (!cur.moveToFirst()) {
            Log.e(TAG, "Fail");
            return;
        }
        //   Log.i(TAG, "Listing...");
        final int artistColumn = cur.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        final int titleColumn = cur.getColumnIndex(MediaStore.Audio.Media.TITLE);
        final int albumColumn = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM);
        final int durationColumn = cur.getColumnIndex(MediaStore.Audio.Media.DURATION);
        final int idColumn = cur.getColumnIndex(MediaStore.Audio.Media._ID);
        final int albumKey = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_KEY);
        //Para coger la imagen cacheada del album y ahorrarse el decode.
        AlbumRetriever albr = new AlbumRetriever(mContentResolver);

        do {
            mItems.add(new Item(
                    cur.getLong(idColumn),
                    cur.getString(artistColumn),
                    cur.getString(titleColumn),
                    cur.getString(albumColumn),
                    cur.getLong(durationColumn),
                    cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA)),
                    albr.getAlbumArtForSongId(cur.getString(albumKey))
            ));

        } while (cur.moveToNext());
        Log.i(TAG, "MusicRetriever OK.");
        cur.close();
    }

    public ArrayList<Item> getSearch(String son, Boolean type) {
        Cursor cur = mContentResolver.query(uri, null, MediaStore.Audio.Media.IS_MUSIC + " = 1", null, null);
        if (cur == null)
            return null;

        if (!cur.moveToFirst())
            return null;

        Log.i(TAG, "Listing...");
        final int artistColumn = cur.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        final int titleColumn = cur.getColumnIndex(MediaStore.Audio.Media.TITLE);
        final int albumColumn = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM);
        final int durationColumn = cur.getColumnIndex(MediaStore.Audio.Media.DURATION);
        final int idColumn = cur.getColumnIndex(MediaStore.Audio.Media._ID);
        final int albumKey = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_KEY);
        AlbumRetriever albr = new AlbumRetriever(mContentResolver);

        do {
            if (type & cur.getString(titleColumn).toLowerCase().contains(son.toLowerCase())) {
                //Log.i(TAG, "ID: " + cur.getString(idColumn) + " Title: " + cur.getString(titleColumn) + " " + cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA)));
                mItems.add(new Item(
                        cur.getLong(idColumn),
                        cur.getString(artistColumn),
                        cur.getString(titleColumn),
                        cur.getString(albumColumn),
                        cur.getLong(durationColumn),
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA)),
                        albr.getAlbumArtForSongId(cur.getString(albumKey))
                ));
            } else if (!type & cur.getString(artistColumn).toLowerCase().contains(son.toLowerCase())) {
                //  Log.i(TAG, "ID: " + cur.getString(idColumn) + " Title: " + cur.getString(titleColumn) + " " + cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA)));
                mItems.add(new Item(
                        cur.getLong(idColumn),
                        cur.getString(artistColumn),
                        cur.getString(titleColumn),
                        cur.getString(albumColumn),
                        cur.getLong(durationColumn),
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA)),
                        albr.getAlbumArtForSongId(cur.getString(albumKey))
                ));
            }

        } while (cur.moveToNext());
        cur.close();
        return (ArrayList<Item>) mItems;
    }

    //Busca por genero
    public ArrayList<Item> getGenreSearch(String son) {
        String[] genresProjection = {
                MediaStore.Audio.Genres.NAME,
                MediaStore.Audio.Genres._ID
        };

        Cursor genresCursor;
        Log.i(TAG, "Listing...");
        Cursor cur = mContentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);

        int id_column_index = cur.getColumnIndex(MediaStore.Audio.Media._ID);
        final int artistColumn = cur.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        final int titleColumn = cur.getColumnIndex(MediaStore.Audio.Media.TITLE);
        final int albumColumn = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM);
        final int durationColumn = cur.getColumnIndex(MediaStore.Audio.Media.DURATION);
        final int idColumn = cur.getColumnIndex(MediaStore.Audio.Media._ID);
        final int albumKey = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_KEY);
        AlbumRetriever albr = new AlbumRetriever(mContentResolver);
        if (cur.moveToFirst()) {
            do {
                int musicId = Integer.parseInt(cur.getString(id_column_index));
                Uri uri = MediaStore.Audio.Genres.getContentUriForAudioId("external", musicId);
                genresCursor = mContentResolver.query(uri, genresProjection, null, null, null);
                int genre_column_index = genresCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME);
                String g;
                if (genresCursor.moveToFirst()) {
                    if (genresCursor.getString(genre_column_index) == null) {
                        g = "404";
                    } else {
                        g = genresCursor.getString(genre_column_index);
                    }
                    do {
                        if (g.toLowerCase().contains(son.toLowerCase())) {
                            Log.e("sss", son);
                            mItems.add(new Item(
                                    cur.getLong(idColumn),
                                    cur.getString(artistColumn),
                                    cur.getString(titleColumn),
                                    cur.getString(albumColumn),
                                    cur.getLong(durationColumn),
                                    cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA)),
                                    albr.getAlbumArtForSongId(cur.getString(albumKey))
                            ));
                        }

                    } while (genresCursor.moveToNext());
                }
                genresCursor.close();
            } while (cur.moveToNext());
        }
        cur.close();
        return (ArrayList<Item>) mItems;
    }

    public int getId(String son) {
        String[] genresProjection = {
                MediaStore.Audio.Genres.NAME,
                MediaStore.Audio.Genres._ID
        };

        Cursor genresCursor;
        Log.i(TAG, "Listing...");
        Cursor cur = mContentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);

        int id_column_index = cur.getColumnIndex(MediaStore.Audio.Media._ID);
        final int artistColumn = cur.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        final int titleColumn = cur.getColumnIndex(MediaStore.Audio.Media.TITLE);
        final int albumColumn = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM);
        final int durationColumn = cur.getColumnIndex(MediaStore.Audio.Media.DURATION);
        final int idColumn = cur.getColumnIndex(MediaStore.Audio.Media._ID);
        final int albumKey = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_KEY);
        AlbumRetriever albr = new AlbumRetriever(mContentResolver);
        cur.close();
        return id_column_index;
    }

    public ArrayList<Item> getAlbumSearch(String album) {
        Cursor cur = mContentResolver.query(uri, null, MediaStore.Audio.Media.IS_MUSIC + " = 1", null, null);
        if (cur == null)
            return null;

        if (!cur.moveToFirst())
            return null;

        Log.i(TAG, "Listing...");
        final int artistColumn = cur.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        final int titleColumn = cur.getColumnIndex(MediaStore.Audio.Media.TITLE);
        final int albumColumn = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM);
        final int durationColumn = cur.getColumnIndex(MediaStore.Audio.Media.DURATION);
        final int idColumn = cur.getColumnIndex(MediaStore.Audio.Media._ID);
        final int albumKey = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_KEY);
        AlbumRetriever albr = new AlbumRetriever(mContentResolver);

        do {
            if (cur.getString(albumColumn).toLowerCase().contains(album.toLowerCase())) {
                //Log.i(TAG, "ID: " + cur.getString(idColumn) + " Title: " + cur.getString(titleColumn) + " " + cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA)));
                mItems.add(new Item(
                        cur.getLong(idColumn),
                        cur.getString(artistColumn),
                        cur.getString(titleColumn),
                        cur.getString(albumColumn),
                        cur.getLong(durationColumn),
                        cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA)),
                        albr.getAlbumArtForSongId(cur.getString(albumKey))
                ));
            }
        } while (cur.moveToNext());
        cur.close();
        return (ArrayList<Item>) mItems;
    }

    public ContentResolver getContentResolver() {
        return mContentResolver;
    }

    public Item getRandomItem() {
        if (mItems.size() <= 0) return null;
        return mItems.get(mRandom.nextInt(mItems.size()));
    }

    public static class Item implements Parcelable {
        public static final Creator<Item> CREATOR = new Creator<Item>() {
            @Override
            public Item createFromParcel(Parcel in) {
                return new Item(in);
            }

            @Override
            public Item[] newArray(int size) {
                return new Item[size];
            }
        };
        final long id;
        final String artist;
        final String title;
        final String album;
        final long duration;
        final String songpath;
        final String albumS;

        public Item(long id, String artist, String title, String album, long duration, String a, String albumArt) {
            this.id = id;
            this.artist = artist;
            this.title = title;
            this.album = album;
            this.duration = duration;
            songpath = a;
            albumS = albumArt;
        }

        protected Item(Parcel in) {
            id = in.readLong();
            artist = in.readString();
            title = in.readString();
            album = in.readString();
            duration = in.readLong();
            songpath = in.readString();
            albumS = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(id);
            dest.writeString(artist);
            dest.writeString(title);
            dest.writeString(album);
            dest.writeLong(duration);
            dest.writeString(songpath);
            dest.writeString(albumS);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public long getId() {
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

        public long getDuration() {
            return duration;
        }

        public String getPath() {
            return songpath;
        }

        public String getAlbumArtPath() {
            return albumS;
        }

        public Uri getURI() {
            return ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
        }
    }
}

