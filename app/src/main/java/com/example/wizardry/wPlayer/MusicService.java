package com.example.wizardry.wPlayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.wizardry.wPlayer.Helpers.Utilities;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;


public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener {
    public final static String action = "CHANGE";
    final static String service = "MusicService";
    private final IBinder mBinder = new LocalBinder();
    String currentPath;
    private boolean isCompleted = false;
    private MediaPlayer mediaPlayer = null;
    private SizedStack<String> pastItems = new SizedStack<>(15);
    private ArrayDeque<String> nextItems = new ArrayDeque<>();

    @Override
    public IBinder onBind(Intent intent) {
        super.onCreate();

        Log.i("MusicService", "Binded");
        if (intent.getBooleanExtra("s", true)) {
            setList(intent.getStringArrayListExtra("paths"));
        }
      /*  if (intent.getStringExtra("nottype") != null) {
            String a = intent.getStringExtra("nottype");
            //    Log.e("TYPE", a);
            switch (a) {
                case "0":
                    notType = 0;
                    break;
                case "1":
                    notType = 1;
                    break;
                case "2":
                    notType = 2;
                    break;
            }
        }*/
        init();
        return mBinder;
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        Log.e(service, "Releasing...");
        stopForeground(true);
        //MainActivity.current = null;
        MetadataSingle.INSTANCE.path = null;
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        Log.e(service, "UnBind");
        stopForeground(true);
        this.stopSelf();
        return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("Inte", "sd");
        checkIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void checkIntent(Intent i) {
        if (i != null) {
            if (i.getAction().equals("PLAY")) {
                Log.w("PendingIntent", "PLAY");
                this.start();
            } else if (i.getAction().equals("NEXT")) {
                Log.w("PendingIntent", "next");
                loadNext();

            } else {
                Log.w("PendingIntent", "BACK");
                loadPrev();
            }
        }
    }

    public void onCreate() {
        super.onCreate();
        Log.i(service, "Created");
    }

    //----------MEDIAPLAYER
    @Override
    public void onSeekComplete(MediaPlayer mp) {
        Log.w("", "");
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.i(service, "MediaPlayer Completed...");
        isCompleted = true;
        loadNext();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(service, " MediaPlayer error..." + what);
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        if (isCompleted) {
            showNotification();
            Log.e("isCompeted", "true");
        }
        isCompleted = false;
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public int getTotal() {
        return mediaPlayer.getDuration();
    }

    public boolean isLooping() {
        return mediaPlayer.isLooping();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public void seekTo(int i) {
        mediaPlayer.seekTo(i);
    }

    public boolean isNotNull() {
        return mediaPlayer != null;
    }

    /**
     * Service
     */

  /*  private void retrieve() {
        mh = new MetadataHelper(currentPath, true);
        art = mh.getFullEmbedded();
    }*/
    public String getPath() {
        return currentPath;
    }

    public void pause() {
        mediaPlayer.pause();
        stopForeground(false);
    }

    public boolean start() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            stopForeground(false);
            return false;
        } else {
            mediaPlayer.start();
            return true;
        }
    }

    private void setupMedia(String path) {
        try {
            Log.i("Setup:  ", path);
            currentPath = path;
            if (!isCompleted) {
                showNotification();
            }
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initSession() {

    }

    // public MetadataHelper getMh() {
    //  return mh;
    // }

    public void loadNext() {
        if (!nextItems.isEmpty()) {
            if (nextItems.peek().equals(currentPath)) {
                if (nextItems.size() == 1) {
                    setupMedia(currentPath);
                    return;
                }
                currentPath = "";
                loadNext();
            }
            setupMedia(nextItems.peek());
            pastItems.push(nextItems.pop());

            //  MainActivity.current = currentPath;
        } else {
            Log.e("Service-Load", "ListaNext Vacia");
        }
    }

    public void loadPrev() {
        if (!pastItems.isEmpty()) {
            if (pastItems.peek().equals(currentPath) && pastItems.size() > 1) {
                currentPath = "";
                loadPrev();
            }
            nextItems.addFirst(pastItems.peek());
            setupMedia(pastItems.pop());
            // showNotification();
        } else {
            Log.e("Service-Load", "Pila Vacia");
        }
    }

    private void init() {
        if (mediaPlayer == null)
            mediaPlayer = new MediaPlayer();

        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        // mSession = new MediaSessionCompat(this, "MusicService");
    }

    public void setList(ArrayList<String> l) {
        Log.e("Current: ", nextItems.size() + " -- " + pastItems.size());

        if (!nextItems.isEmpty()) {
            Log.i("Lista", "No Vacia");
            nextItems.clear();
        }
        for (String i : l) {
            nextItems.add(i);
        }
        Log.e("Current: ", nextItems.size() + " -- " + pastItems.size());
    }

    private void showNotification() {
        //retrieve();
        MetadataSingle.INSTANCE.retrieveMin(currentPath);

        Intent i = new Intent(this, MusicService.class).setAction("PLAY");
        Intent i2 = new Intent(this, MusicService.class).setAction("NEXT").setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Intent i3 = new Intent(this, MusicService.class).setAction("BACK").setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //     Notification.Builder mBuilder;
        PendingIntent pi = PendingIntent.getService(getApplicationContext(), 0, i, 0);
        PendingIntent pi2 = PendingIntent.getService(getApplicationContext(), 0, i2, 0);
        PendingIntent pi3 = PendingIntent.getService(getApplicationContext(), 0, i3, 0);
      /*  MediaMetadataCompat mm = new MediaMetadataCompat.Builder().putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, art).build();
        mSession.setPlaybackToLocal(AudioManager.STREAM_MUSIC);
        mSession.setActive(true);
        mSession.setMetadata(mm);*/
       /* Notification not = new NotificationCompat.Builder(getBaseContext())
                .setLargeIcon(art)
                .setSmallIcon(R.drawable.ic_noti)
                .setContentTitle(mh.getNombre())
                .setContentText(mh.getArtist())
                .addAction(R.drawable.ic_action_back, "", pi3)
                .addAction(R.drawable.ic_action_play, "", pi)
                .addAction(R.drawable.ic_action_next, "", pi2)
                .setColor(getColor(R.color.colorPrimary))
                .setStyle(new NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1, 2)).build();*/
        Notification not = new NotificationCompat.Builder(getBaseContext())
                .setLargeIcon(MetadataSingle.INSTANCE.fullEmbedded)
                .setSmallIcon(R.drawable.ic_noti)
                .setContentTitle(MetadataSingle.INSTANCE.nombre)
                .setContentText(MetadataSingle.INSTANCE.artist)
                .addAction(R.drawable.ic_action_back, "", pi3)
                .addAction(R.drawable.ic_action_play, "", pi)
                .addAction(R.drawable.ic_action_next, "", pi2)
                //  .setColor(getColor(R.color.colorPrimary))
                .setColor(MetadataSingle.INSTANCE.currentColors[5])
                .setStyle(new NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1, 2)).build();
     /*   switch (notType) {
            case 0:
                not = new NotificationCompat.Builder(getBaseContext())
                        .setLargeIcon(art)
                        .setSmallIcon(R.drawable.ic_noti)
                        .setContentTitle(mh.getNombre())
                        .setContentText(mh.getArtist())
                        .addAction(R.drawable.ic_action_back, "", pi3)
                        .addAction(R.drawable.ic_action_play, "", pi)
                        .addAction(R.drawable.ic_action_next, "", pi2)
                        .setStyle(new NotificationCompat.MediaStyle()).build();
                        /*
                mBuilder = new Notification.Builder(this).setVisibility(1)
                        .setLargeIcon(art)
                        .setSmallIcon(R.drawable.ic_noti)
                        .setContentTitle(mh.getNombre())
                        .setContentText(mh.getArtist())
                        .addAction(R.drawable.ic_action_back, "", pi3)
                        .addAction(R.drawable.ic_action_play, "", pi)
                        .addAction(R.drawable.ic_action_next, "", pi2)

                        .setStyle(new Notification.MediaStyle());
                break;
            case 1:
                mBuilder = new Notification.Builder(this)
                        .setLargeIcon(art)
                        .setSmallIcon(R.drawable.ic_noti)
                        .setContentTitle(mh.getNombre())
                        .setContentText(mh.getArtist())
                        .addAction(R.drawable.ic_action_back, "", pi3)
                        .addAction(R.drawable.ic_action_play, "", pi)
                        .addAction(R.drawable.ic_action_next, "", pi2)
                        .setStyle(new Notification.BigPictureStyle().bigPicture(art));

                break;
            case 2:
                mBuilder = new Notification.Builder(this)
                        .setLargeIcon(art)
                        .setSmallIcon(R.drawable.ic_noti)
                        .setContentTitle(mh.getNombre())
                        .setContentText(mh.getArtist())
                        .addAction(R.drawable.ic_action_back, "", pi3)
                        .addAction(R.drawable.ic_action_play, "", pi)
                        .addAction(R.drawable.ic_action_next, "", pi2);
                break;
            default:
                mBuilder = new Notification.Builder(this)
                        .setLargeIcon(art)
                        .setSmallIcon(R.drawable.ic_noti)
                        .setContentTitle(mh.getNombre())
                        .setContentText(mh.getArtist())
                        .addAction(R.drawable.ic_action_back, "", pi3)
                        .addAction(R.drawable.ic_action_play, "", pi)
                        .addAction(R.drawable.ic_action_next, "", pi2);
        }*/
        startForeground(1337, not);
        sendBroadcast(new Intent(action).putExtra("path", currentPath));
    }

    public boolean setRepeat() {
        mediaPlayer.setLooping(!mediaPlayer.isLooping());
        return mediaPlayer.isLooping();
    }

    public void setRandom() {
        String[] a = nextItems.toArray(new String[nextItems.size()]);
        Utilities.shuffleArray(a);
        nextItems.clear();
        Collections.addAll(nextItems, a);
        Log.i(service, "Shuffled");
    }

 /*   @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                if (mediaPlayer != null) {
                    if (!mediaPlayer.isPlaying()) mediaPlayer.start();
                    mediaPlayer.setVolume(1.0f, 1.0f);
                    Log.i("AudioFocus", "GAIN");
                }
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media activity_player
                Log.i("AudioFocus", "LOST");

                if (mediaPlayer.isPlaying()) mediaPlayer.pause();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media activity_player because playback
                // is likely to resume
                Log.i("AudioFocus", "TRAN");

                if (mediaPlayer.isPlaying()) mediaPlayer.pause();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                Log.i("AudioFocus", "TRAN");

                if (mediaPlayer.isPlaying()) mediaPlayer.setVolume(0.1f, 0.1f);
                break;
        }
    }

    private void initAudioManager() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.e("No Focus","");
            // could not get audio focus.
        }
    }*/

    public class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public class SizedStack<T> extends Stack<T> {
        private int maxSize;

        SizedStack(int size) {
            super();
            this.maxSize = size;
        }

        @Override
        public Object push(Object object) {
            //If the stack is too big, remove elements until it's the right size.
            if (this.size() == maxSize) {
                this.remove(0);

            }
           /* while (this.size() >= maxSize) {
                this.remove(0);
            }*/
            return super.push((T) object);
        }
    }
}