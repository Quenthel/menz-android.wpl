package com.example.wizardry.wPlayer.Async;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.example.wizardry.wPlayer.Helpers.ImageHelper;

import java.lang.ref.WeakReference;

/**
 * Created by Witchery on 2/4/2016.
 * Workers para la carga asincrona de las images de la listview
 * Obsoleto y quizás no funcional, pues la  parte de carga por cache fue borrada cuando migré a Glide para quitar archivos viejos
 * Esto ahora se hace con Glidle por cuestión de rendimiento y sencillez.
 */
public class ImageAsyncTask extends AsyncTask<String, Integer, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    private Integer cnt = 0;
    private Context ctx;
    private ImageView view;
    private String previousParth = "";
    private Bitmap previous = null;
    private int cachePos = -1;

    public ImageAsyncTask(Context context, ImageView imageView) {
        imageViewReference = new WeakReference<>(imageView);
        ctx = context;
    }

    @Override
    protected Bitmap doInBackground(String... paths) {
        MediaMetadataRetriever mt = new MediaMetadataRetriever();
        byte[] b;
        String p = paths[0];
        mt.setDataSource(p);
        Log.e("ASYNC", paths[0]);
        b = mt.getEmbeddedPicture();
        mt.release();

        if (b != null) {
            return ImageHelper.decodeSampledBitmapFromResource(100, 100, b);
        } else {
            return null;
        }
      /*  if(paths!=null){
            if(paths[0].equals(previousParth)){
                previousParth=paths[0];
             //   return previous;
            }
            else{
                previousParth=paths[0];
              //  return  ImageHelper.decodeSampledBitmapFromResource(100, 100, paths[0]);
            }
        }*/
        /*else{
            return null;
        }*/
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {

    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

        if (imageViewReference != null) {
            final ImageView img = imageViewReference.get();

            if (bitmap != null) {
                if (img != null) {
                    img.setImageBitmap(bitmap);
                    previous = bitmap;
                }
            } else {
                if (img != null) {
                    //    img.setImageResource(R.mipmap.stop);
                }
            }
        }
    }
}