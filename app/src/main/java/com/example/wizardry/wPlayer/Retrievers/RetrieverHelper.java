package com.example.wizardry.wPlayer.Retrievers;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.wizardry.wPlayer.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Quenthel on 9/5/16.
 * Clase para gestionar historias relacionadas a los datos del Content Resolver
 */
public class RetrieverHelper {
    public final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
    private final String tag = "RetrieverHelper";
    ContentResolver mContentResolver;
    private String type;
    private String search;
    private Boolean isGroupedByAlbum;
    private Context c;
    private ArrayList<MusicRetriever.Item> li;

    public RetrieverHelper(ContentResolver cr, Context c, String type, String search, Boolean isGroupedByAlbum) {
        mContentResolver = cr;
        this.type = type;
        this.search = search;
        this.isGroupedByAlbum = isGroupedByAlbum;
        this.c = c;
        li = searchingContent();
    }

    public static void addToPlaylist(ContentResolver resolver, int audioId, String name) {
        String[] cols = new String[]{"count(*)"};
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", getPlaylist(resolver, name));
        Cursor cur = resolver.query(uri, cols, null, null, null);
        cur.moveToFirst();
        final int base = cur.getInt(0);
        cur.close();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, Integer.valueOf(base + audioId));
        values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, audioId);
        resolver.insert(uri, values);
    }

    public static void deleteSongFromPlaylist(ContentResolver resolver, int audioId, String name) {
        //int audioId = (int) getSongIdFromMediaStore(song,resolver);
        final Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", getPlaylist(resolver, name));
        int del = resolver.delete(uri, MediaStore.Audio.Playlists.Members.AUDIO_ID + " = ? ", new String[]{
                Integer.toString(audioId)
        });
        Log.e("DeleteFromPlaylist", "" + del);
    }

    public static long getSongIdFromMediaStore(String songPath, ContentResolver resolver) {
        long id = 0;
        ContentResolver cr = resolver;


        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.DATA;
        String[] selectionArgs = {songPath};
        String[] projection = {MediaStore.Audio.Media._ID};
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

        Cursor cursor = cr.query(uri, projection, selection + "=?", selectionArgs, sortOrder);

        //  Log.d("TAG", songPath);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int idIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                id = Long.parseLong(cursor.getString(idIndex));
            }
            cursor.close();
        }
        return id;
    }

    public static long createPlayList(ContentResolver resolver, String name) {
        long id = getPlaylist(resolver, name);

        if (id == -1) {
            // We need to create a new playlist.
            ContentValues values = new ContentValues(1);
            values.put(MediaStore.Audio.Playlists.NAME, name);
            Uri uri = resolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values);
            id = Long.parseLong(uri.getLastPathSegment());
        } else {
            // We are overwriting an existing playlist. Clear existing songs.
            Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", id);
            resolver.delete(uri, null, null);
        }

        return id;
    }

    public static void renamePlaylist(ContentResolver resolver, long id, String newName) {
        long existingId = getPlaylist(resolver, newName);
        // We are already called the requested name; nothing to do.
        if (existingId == id)
            return;
        // There is already a playlist with this name. Kill it.
        if (existingId != -1)
            deletePlayList(resolver, existingId);

        ContentValues values = new ContentValues(1);
        values.put(MediaStore.Audio.Playlists.NAME, newName);
        resolver.update(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values, "_id=" + id, null);
    }

    public static long getPlaylist(ContentResolver resolver, String name) {
        long id = -1;

        Cursor cursor = resolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Playlists._ID},
                MediaStore.Audio.Playlists.NAME + "=?",
                new String[]{name}, null);

        if (cursor != null) {
            if (cursor.moveToNext())
                id = cursor.getLong(0);
        }
        cursor.close();
        return id;
    }

    public static void deletePlayList(ContentResolver resolver, long id) {
        Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, id);
        resolver.delete(uri, null, null);
    }

    public static void setRingtone(Context context, String path, @Nullable String name, boolean a) {
        File newSoundFile = new File(path, "mainRing.oog");
        ContentResolver mCr = context.getContentResolver();

        Uri mUri = Uri.parse("android.resource://com.your.package/R.raw.your_resource_id");
        AssetFileDescriptor soundFile;
        try {
            soundFile = mCr.openAssetFileDescriptor(mUri, "r");
        } catch (FileNotFoundException e) {
            soundFile = null;
        }

        try {
            byte[] readData = new byte[1024];
            FileInputStream fis = soundFile.createInputStream();
            FileOutputStream fos = new FileOutputStream(newSoundFile);
            int i = fis.read(readData);

            while (i != -1) {
                fos.write(readData, 0, i);
                i = fis.read(readData);
            }
            fos.close();
        } catch (IOException io) {
        }

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, newSoundFile.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, name);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "mp3");
        values.put(MediaStore.MediaColumns.SIZE, newSoundFile.length());
        values.put(MediaStore.Audio.Media.ARTIST, R.string.app_name);
        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
        values.put(MediaStore.Audio.Media.IS_ALARM, true);
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);

        Uri uri = MediaStore.Audio.Media.getContentUriForPath(newSoundFile.getAbsolutePath());
        Uri newUri = mCr.insert(uri, values);

        try {
            RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, newUri);
        } catch (Throwable t) {
            Log.d("AAA", t.getMessage());
        }
    }

    public static void setRingtone(Context context, String path, @Nullable String name) {

        final File ringtoneFile = new File(path);
        ContentValues content = new ContentValues();
        content.put(MediaStore.MediaColumns.DATA, ringtoneFile.getAbsolutePath());
        content.put(MediaStore.MediaColumns.TITLE, ringtoneFile.getName());
        content.put(MediaStore.MediaColumns.SIZE, ringtoneFile.length());
        content.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
        //content.put(MediaStore.Audio.Media.DURATION, ringtoneFile.length());
        content.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        content.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
        content.put(MediaStore.Audio.Media.IS_ALARM, false);
        content.put(MediaStore.Audio.Media.IS_MUSIC, false);

        Uri Ringtone1 = Uri.parse("current song file path");
        //Insert it into the database
        Log.i("TAG", "the absolute path of the file is :" +
                ringtoneFile.getAbsolutePath());
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(
                ringtoneFile.getAbsolutePath());
        context.getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + ringtoneFile.getAbsolutePath() + "\"", null);
        Uri newUri = context.getContentResolver().insert(uri, content);
        System.out.println("uri==" + uri);
        Log.i("TAG", "the ringtone uri is :" + newUri);
        RingtoneManager.setActualDefaultRingtoneUri(
                context.getApplicationContext(), RingtoneManager.TYPE_RINGTONE,
                newUri);
    }

    public static Boolean deleteFromMediaStore(File f, ContentResolver cr) {
        String[] projection = {MediaStore.Audio.Media._ID};
        Boolean deleted = false;
        String selection = MediaStore.Audio.Media.DATA + " = ?";
        String[] selectionArgs = new String[]{f.getAbsolutePath()};
        Uri queryUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = cr;
        Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
        if (c.moveToFirst()) {
            long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
            Uri deleteUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
            contentResolver.delete(deleteUri, null, null);
            deleted = true;
            f.delete();
        }

        c.close();
        return deleted;
    }

    private ArrayList<MusicRetriever.Item> searchingContent() {
        ArrayList<MusicRetriever.Item> l = new ArrayList<>();
        if (type.equalsIgnoreCase(c.getResources().getString(R.string.Song))) {
            l = findSongs(search, true);
        } else if (type.equalsIgnoreCase(c.getResources().getString(R.string.Artist))) {
            l = findSongs(search, false);
        } else if (type.equalsIgnoreCase(c.getResources().getString(R.string.Genre))) {
            Log.e(type, c.getResources().getString(R.string.Song));
            l = (ArrayList<MusicRetriever.Item>) findSongByGenre(search);
        } else if (type.equalsIgnoreCase(c.getResources().getString(R.string.Album))) {
            Log.e(type, c.getResources().getString(R.string.Album));
            l = (ArrayList<MusicRetriever.Item>) findSongByAlbum(search);
        }
        return l;
    }

    public ArrayList<MusicRetriever.Item> getResults() {
        return li;
    }

    private ArrayList<MusicRetriever.Item> findSongs(String search, boolean s) {
        MusicRetriever ms = new MusicRetriever(mContentResolver);
        ArrayList<MusicRetriever.Item> l = ms.getSearch(search, s);
        for (MusicRetriever.Item a : l) {
            Log.w(tag, a.getTitle());
        }
        return l;
    }

    private List<MusicRetriever.Item> findSongByGenre(String search) {
        MusicRetriever ms = new MusicRetriever(mContentResolver);
        List<MusicRetriever.Item> l = ms.getGenreSearch(search);
        for (MusicRetriever.Item a : l) {
            Log.w(tag, a.getTitle());
        }
        return l;
    }

    private List<MusicRetriever.Item> findSongByAlbum(String search) {
        MusicRetriever ms = new MusicRetriever(mContentResolver);
        List<MusicRetriever.Item> l = ms.getAlbumSearch(search);
        for (MusicRetriever.Item a : l) {
            Log.w(tag, a.getTitle());
        }
        return l;
    }
    /*private int[] getAlbumIds(ContentResolver contentResolver) {
        List<Integer> result = new ArrayList<Integer>();
        Cursor cursor = contentResolver.query(MediaStore.Audio.Media.getContentUri("external"), new String[]{MediaStore.Audio.Media.ALBUM_ID}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int albumId = cursor.getInt(0);
                if (!result.contains(albumId))
                    result.add(albumId);
            } while (cursor.moveToNext());
        }

        int[] resultArray = new int[result.size()];
        for (int i = 0; i < result.size(); i++)
            resultArray[i] = result.get(i);

        return resultArray;
    }
    public void caching(){
        int[]a = getAlbumIds(mContentResolver);
        for(int i =0;i<a.length;i++){

        }
    }

    private Shader getAlbumArt(ContentResolver contentResolver, int albumId, int width, int height) {
        Uri uri = ContentUris.withAppendedId(sArtworkUri, albumId);
        InputStream input = null;
        try {
            input = contentResolver.openInputStream(uri);
            if (input == null)
                return null;

            Bitmap artwork = BitmapFactory.decodeStream(input);
            input.close();
            if (artwork == null)
                return null;

            Bitmap scaled = Bitmap.createScaledBitmap(artwork, width, height, true);
            if (scaled == null)
                return null;

            if (scaled != artwork)
                artwork.recycle();
            artwork = scaled;

            return new BitmapShader(artwork, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }*/
    //}
}