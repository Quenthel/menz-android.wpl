package com.example.wizardry.pruebas.Async;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;
import com.example.wizardry.pruebas.Helpers.ImageHelper;
import com.example.wizardry.pruebas.R;

import java.lang.ref.WeakReference;

/**
 * Created by Witchery on 5/4/2016.
 * Workers para la carga asincrona de las images del gridview
 * Obsoleto y quizás no funcional, pues la  parte de carga por cache fue borrada cuando migré a Glide para quitar archivos viejos
 * Esto ahora se hace con Glidle por cuestión de rendimiento y sencillez.
 */
public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    private int data = 0;
    private int cachePos=-1;

    public BitmapWorkerTask(ImageView imageView) {
        imageViewReference = new WeakReference<>(imageView);
    }

    @Override
    protected Bitmap doInBackground(String... path) {
        String s = path[0];
        if (s.equals("vac")) {
            return null;
        }
        return ImageHelper.decodeSampledBitmapFromResource(120, 120, s);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewReference != null) {
            final ImageView imageView = imageViewReference.get();
            if(bitmap != null) {
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            } else {
                imageView.setImageResource(R.drawable.nodata);
            }
        }
    }
}