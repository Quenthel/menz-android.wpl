package com.example.wizardry.wPlayer.Helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import com.example.wizardry.wPlayer.Helpers.mp3agic.ID3v1;
import com.example.wizardry.wPlayer.Helpers.mp3agic.ID3v2;
import com.example.wizardry.wPlayer.Helpers.mp3agic.InvalidDataException;
import com.example.wizardry.wPlayer.Helpers.mp3agic.Mp3File;
import com.example.wizardry.wPlayer.Helpers.mp3agic.UnsupportedTagException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Witchery on 25/5/16.
 * Clase para recuperar metadatos, principalmente para aquellas zonas que requieran recuperar ciertos datos de distintas procedencias;
 */
public class MetadataHelper {
    private String path;
    private String nombre;
    private String album;
    private String albumArtist;
    private String lirycs;
    private String artist;
    private String genre;
    private String bitrate;
    private String year;
    private String ext;

    private Bitmap fullEmbedded;
    private boolean isVBR;
    private String trackNumber;
    private int sampleRate;

    private long duration;
    private boolean isCompilation;

    public MetadataHelper(String path) {
        this.path = path;

        retrieve();
      /*  Log.e(
                "METADATA",
                "NAME " + nombre
                        + " LIRYCS " + lirycs
                        + " BITRATE  " + bitrate
                        + " DURATION " + duration
        );*/
    }


    public MetadataHelper(String path, boolean res) {
        this.path = path;
        retrieveMin();
    }

    //
    private void retrieve() {
        MediaMetadataRetriever mt = new MediaMetadataRetriever();
        mt.setDataSource(path);
        nombre = mt.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        trackNumber = mt.extractMetadata(MediaMetadataRetriever.METADATA_KEY_NUM_TRACKS);
        duration = Long.parseLong(mt.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        bitrate = mt.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
        genre = mt.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
        album = mt.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        artist = mt.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        year = mt.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR);
        int i = path.lastIndexOf('.');
        if (i > 0) {
            ext = path.substring(i + 1);
        }
        Log.e("EXT", ext);
        byte[] s = mt.getEmbeddedPicture();
        if (s != null) {
            InputStream iss = new ByteArrayInputStream(s);
            fullEmbedded = BitmapFactory.decodeStream(iss);
        }
        if (ext.equals("mp3")) {
            Mp3File mp3file = null;
            try {
                mp3file = new Mp3File(path);
                if (mp3file.hasId3v1Tag()) {
                    ID3v1 id3v1Tag = mp3file.getId3v1Tag();
                }
                if (mp3file.hasId3v2Tag()) {
                    ID3v2 id3v2 = mp3file.getId3v2Tag();
                    albumArtist = id3v2.getAlbumArtist();
                    lirycs = id3v2.getAsyncLyrics();
                    isCompilation = id3v2.isCompilation();
                    isVBR = mp3file.isVbr();
                    sampleRate = mp3file.getSampleRate();
                /*byte[] s = id3v2.getAlbumImage();
                if (s != null) {
                    InputStream iss = new ByteArrayInputStream(s);
                    fullEmbedded = BitmapFactory.decodeStream(iss);
                }*/
                }
            } catch (IOException | UnsupportedTagException | InvalidDataException e) {
                e.printStackTrace();
            }
        }
    }

    private void retrieveMin() {
        MediaMetadataRetriever mt = new MediaMetadataRetriever();
        mt.setDataSource(path);
        nombre = mt.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        duration = Long.parseLong(mt.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        album = mt.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        artist = mt.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        byte[] s = mt.getEmbeddedPicture();
        if (s != null) {
            InputStream iss = new ByteArrayInputStream(s);
            fullEmbedded = BitmapFactory.decodeStream(iss);
        }
        albumArtist = "";
        isCompilation = false;
        isVBR = false;
        sampleRate = 320;
    }

    public String retrieveLyr(String path) {
        if (path.endsWith(".mp3")) {
            Mp3File mp3file = null;
            try {
                mp3file = new Mp3File(path);
                if (mp3file.hasId3v2Tag()) {
                    ID3v2 id3v2 = mp3file.getId3v2Tag();
                    return id3v2.getAsyncLyrics();
                }
            } catch (IOException | UnsupportedTagException | InvalidDataException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getPath() {
        return path;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public boolean getVBR() {
        return isVBR;
    }

    public String getNombre() {
        return nombre;
    }

    public String getAlbum() {
        return album;
    }

    public String getAlbumArtist() {
        return albumArtist;
    }

    public String getLirycs() {
        return lirycs;
    }

    public String getArtist() {
        return artist;
    }

    public String getGenre() {
        return genre;
    }

    public String getExt() {
        return ext;
    }


    public String getBitrate() {
        return bitrate;
    }

    public String getYear() {
        return year;
    }

    public Bitmap getFullEmbedded() {
        return fullEmbedded;
    }

    public long getDuration() {
        return duration;
    }

    public boolean isCompilation() {
        return isCompilation;
    }

    public String getTrackNumber() {
        return trackNumber;
    }


}
