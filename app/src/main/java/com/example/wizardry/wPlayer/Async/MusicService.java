package com.example.wizardry.wPlayer.Async;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.example.wizardry.wPlayer.Activities.MainActivity;
import com.example.wizardry.wPlayer.Helpers.MetadataHelper;
import com.example.wizardry.wPlayer.Helpers.Utilities;
import com.example.wizardry.wPlayer.R;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;


public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener {
    public final static String action = "CHANGE";
    final static String service = "MusicService";
    public static MediaPlayer mediaPlayer = null;
    private final IBinder mBinder = new LocalBinder();
    public MetadataHelper mh;
    String currentPath;
    Bitmap art;
    int notType;
    private Stack<String> pastItemsPilaVersion = new Stack<>();
    private ArrayDeque<String> listaRepColaVersion = new ArrayDeque<>();

    @Override
    public IBinder onBind(Intent intent) {
        super.onCreate();

        Log.i("MusicService", "Binded");
        if (intent.getBooleanExtra("s", true)) {
            setList(intent.getStringArrayListExtra("paths"), 0);
        }
        if (intent.getStringExtra("nottype") != null) {
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
        }
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
        MainActivity.current = null;
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
        Log.e(service, "Venga, hasta luego");
        stopForeground(true);
        this.stopSelf();
        return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        checkeando(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void checkeando(Intent i) {
        if (i != null) {
            if (i.getAction().equals("PLAY")) {
                Log.w("PendingIntent", "PLAY");
                this.start();
            } else if (i.getAction().equals("NEXT")) {
                Log.w("PendingIntent", "next");
                this.loadNextOrPreviousSong(true);

            } else {
                Log.w("PendingIntent", "BACK");
                this.loadNextOrPreviousSong(false);
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
        loadNextOrPreviousSong(true);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(service, " MediaPlayer error..." + what);
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.i(service, "MediaPlayer Prepared...");
        mp.start();
        Intent i = new Intent(action);
        i.putExtra("path", currentPath);
        sendBroadcast(i);
        MainActivity.current = currentPath;
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public int getTotal() {
        return mediaPlayer.getDuration();
    }

    /**
     * Metodos del servicio
     */
    private void retrieve() {
        /*ASYNC
        try {
            mh = new getMetadataTask().execute(currentPath).get();
            //  mh = new MetadataHelper(currentPath);
            art = mh.getFullEmbedded();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }*/
        mh = new MetadataHelper(currentPath, true);
        art = mh.getFullEmbedded();
    }

    public String getPath() {
        return currentPath;
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public boolean start() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
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
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadNextOrPreviousSong(boolean isNext) {
        String nextPath = "a";
        if (isNext) {
            if (!listaRepColaVersion.isEmpty()) {
                if (listaRepColaVersion.peek().equals(currentPath)) {
                    currentPath = ".-.";
                    loadNextOrPreviousSong(true);
                }
                nextPath = listaRepColaVersion.peek();
                pastItemsPilaVersion.push(listaRepColaVersion.pop());
                setupMedia(nextPath);
            } else {
                Log.e("Service-Load", "ListaNext Vacia");
                return;
            }

        } else {
            if (!pastItemsPilaVersion.isEmpty()) {
                if (pastItemsPilaVersion.peek().equals(currentPath) && pastItemsPilaVersion.size() > 1) {
                    currentPath = ".b-.";
                    loadNextOrPreviousSong(false);
                }

                listaRepColaVersion.addFirst(pastItemsPilaVersion.peek());
                nextPath = pastItemsPilaVersion.pop();
                setupMedia(nextPath);
            } else {
                Log.e("Service-Load", "Pila Vacia");
                return;
            }
        }
        currentPath = nextPath;
        showNotification();
    }

    private void init() {
        if (mediaPlayer == null)
            mediaPlayer = new MediaPlayer();

        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
    }

    public void setList(ArrayList<String> l, int x) {
        Log.e("Current: ", listaRepColaVersion.size() + " -- " + pastItemsPilaVersion.size());

        if (!listaRepColaVersion.isEmpty()) {
            Log.i("Lista", "No Vacia");
            listaRepColaVersion.clear();
        }
        for (String i : l) {
            listaRepColaVersion.add(i);
        }
        Log.e("Current: ", listaRepColaVersion.size() + " -- " + pastItemsPilaVersion.size());
    }

    private void showNotification() {
        retrieve();
        Intent i = new Intent(this, MusicService.class);
        Intent i2 = new Intent(this, MusicService.class);
        Intent i3 = new Intent(this, MusicService.class);
        i.setAction("PLAY");
        i2.setAction("NEXT");
        i3.setAction("BACK");
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Notification.Builder mBuilder;
        PendingIntent pi = PendingIntent.getService(getApplicationContext(), 0, i, 0);
        PendingIntent pi2 = PendingIntent.getService(getApplicationContext(), 0, i2, 0);
        PendingIntent pi3 = PendingIntent.getService(getApplicationContext(), 0, i3, 0);
        switch (notType) {
            case 0:
                mBuilder = new Notification.Builder(this)
                        .setLargeIcon(art)
                        .setSmallIcon(R.mipmap.ic_launcher)
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
                        .setSmallIcon(R.mipmap.ic_launcher)
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
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(mh.getNombre())
                        .setContentText(mh.getArtist())
                        .addAction(R.drawable.ic_action_back, "", pi3)
                        .addAction(R.drawable.ic_action_play, "", pi)
                        .addAction(R.drawable.ic_action_next, "", pi2);
                break;
            default:
                mBuilder = new Notification.Builder(this)
                        .setLargeIcon(art)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(mh.getNombre())
                        .setContentText(mh.getArtist())
                        .addAction(R.drawable.ic_action_back, "", pi3)
                        .addAction(R.drawable.ic_action_play, "", pi)
                        .addAction(R.drawable.ic_action_next, "", pi2);
        }
        startForeground(1337, mBuilder.build());
    }

    public boolean setRepeat() {
        mediaPlayer.setLooping(!mediaPlayer.isLooping());
        return mediaPlayer.isLooping();
    }

    public void setRandom() {
        String[] a = listaRepColaVersion.toArray(new String[listaRepColaVersion.size()]);
        Utilities.shuffleArray(a);
        listaRepColaVersion.clear();
        Collections.addAll(listaRepColaVersion, a);
        Log.i(service, "Shuffleado");
    }

    public class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
}
