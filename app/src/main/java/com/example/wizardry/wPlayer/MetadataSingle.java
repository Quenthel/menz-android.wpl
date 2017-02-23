package com.example.wizardry.wPlayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

import com.example.wizardry.wPlayer.Helpers.ImageHelper;
import com.example.wizardry.wPlayer.Helpers.mp3agic.ID3v2;
import com.example.wizardry.wPlayer.Helpers.mp3agic.InvalidDataException;
import com.example.wizardry.wPlayer.Helpers.mp3agic.Mp3File;
import com.example.wizardry.wPlayer.Helpers.mp3agic.UnsupportedTagException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public enum MetadataSingle {
    INSTANCE;
    public String path;
    public String nombre;
    public String album;
    public String albumArtist;
    public String lyrics;
    public String artist;
    public String genre;
    public String bitrate;
    public String year;
    public String ext;
    public String trackNumber = "";
    public boolean isVBR, isCompilation;
    public int sampleRate;
    public long duration = 0;
    public Bitmap fullEmbedded;
    public int[] currentColors = new int[8];
//    public Typeface type;

    public void retrieve(String path) {
        this.path = path;

        MediaMetadataRetriever mt = new MediaMetadataRetriever();
        try {
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
            byte[] s = mt.getEmbeddedPicture();
            if (s != null) {
                InputStream iss = new ByteArrayInputStream(s);
                fullEmbedded = BitmapFactory.decodeStream(iss);
            }
            if (ext.equals("mp3")) {
                Mp3File mp3file = null;
                try {
                    mp3file = new Mp3File(path);
              /*  if (mp3file.hasId3v1Tag()) {
                    ID3v1 id3v1Tag = mp3file.getId3v1Tag();
                }*/
                    if (mp3file.hasId3v2Tag()) {
                        ID3v2 id3v2 = mp3file.getId3v2Tag();
                        albumArtist = id3v2.getAlbumArtist();
                        lyrics = id3v2.getAsyncLyrics();
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
        } catch (NumberFormatException x) {
            duration = 0;
            Mp3File mp3file = null;
            try {
                mp3file = new Mp3File(path);

                if (mp3file.hasId3v2Tag()) {
                    ID3v2 id3v2 = mp3file.getId3v2Tag();
                    albumArtist = id3v2.getAlbumArtist();
                    lyrics = id3v2.getAsyncLyrics();
                    isCompilation = id3v2.isCompilation();
                    isVBR = mp3file.isVbr();
                    sampleRate = mp3file.getSampleRate();
                    nombre = id3v2.getTitle();
                    album = id3v2.getAlbum();
                    artist = id3v2.getArtist();
                    byte[] s = id3v2.getAlbumImage();
                    if (s != null) {
                        InputStream iss = new ByteArrayInputStream(s);
                        fullEmbedded = BitmapFactory.decodeStream(iss);
                    }
                }
            } catch (IOException | UnsupportedTagException | InvalidDataException e) {
                e.printStackTrace();
            }

        } finally {
            mt.release();
        }
    }

    public void retrieveMin(String path) {
        this.path = path;
        try {
            MediaMetadataRetriever mt = new MediaMetadataRetriever();
            mt.setDataSource(path);

            nombre = mt.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            duration = Long.parseLong(mt.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            album = mt.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            artist = mt.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            byte[] s = mt.getEmbeddedPicture();
            mt.release();
            if (s != null) {
                InputStream iss = new ByteArrayInputStream(s);
                fullEmbedded = BitmapFactory.decodeStream(iss);
                iss.close();
                currentColors = ImageHelper.getColors(fullEmbedded);
            } else {
                fullEmbedded = null;
                currentColors = ImageHelper.getDefaultColors();
            }
            isCompilation = false;
            isVBR = false;
            sampleRate = 320;


        } catch (NumberFormatException x) {
            duration = 0;
            Mp3File mp3file;
            try {
                mp3file = new Mp3File(path);
                if (mp3file.hasId3v2Tag()) {
                    ID3v2 id3v2 = mp3file.getId3v2Tag();
                    albumArtist = id3v2.getAlbumArtist();
                    lyrics = id3v2.getAsyncLyrics();
                    isCompilation = id3v2.isCompilation();
                    isVBR = mp3file.isVbr();
                    sampleRate = mp3file.getSampleRate();
                    nombre = id3v2.getTitle();
                    album = id3v2.getAlbum();
                    artist = id3v2.getArtist();
                    byte[] s = id3v2.getAlbumImage();
                    if (s != null) {
                        InputStream iss = new ByteArrayInputStream(s);
                        fullEmbedded = BitmapFactory.decodeStream(iss);
                        iss.close();
                        currentColors = ImageHelper.getColors(fullEmbedded);
                    } else {
                        currentColors = ImageHelper.getDefaultColors();
                    }
                }
            } catch (IOException | UnsupportedTagException | InvalidDataException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   /* public void init() {
        nombre = "";
        album = "";
        albumArtist = "";
        artist = "";
        trackNumber = "";
        duration = 0;
        fullEmbedded = null;
        currentColors = ImageHelper.getDefaultColors();
    }*/
}