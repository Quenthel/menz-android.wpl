package com.example.wizardry.pruebas.Helpers;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v7.graphics.Palette;

/**
 * Created by Witchery on 5/5/2016.
 */
public class ImageHelper {
    public static Bitmap decodeSampledBitmapFromResource(int reqWidth, int reqHeight, String path) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    public static Bitmap decodeSampledBitmapFromResource(int reqWidth, int reqHeight, byte[] path) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(path, 0, path.length, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(path, 0, path.length, options);

    }

    public static Bitmap decodeSampledBitmapFromResource(int reqWidth, int reqHeight, int id) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(Resources.getSystem(), id);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(Resources.getSystem(), id, options);
    }
   // <color name="colorAccent">#ff4081</color>

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static int[] getColors(Bitmap b) {
        int[] a = new int[7];
        if (b != null) {
            Palette p = Palette.from(b).generate();
            //reduceVisualCancer(p);
            a[0] = p.getVibrantColor(Color.WHITE);
            a[1] = p.getDarkMutedColor(Color.BLACK);
            a[2] = p.getLightVibrantColor(Color.LTGRAY);
            a[3] = p.getDarkVibrantColor(Color.BLACK);
            a[4] = p.getLightMutedColor(Color.WHITE);
            a[5] = p.getVibrantColor(Color.BLACK);
            a[6]= p.getVibrantColor(Color.LTGRAY);

        } else {
            a[0] = Color.WHITE;
            a[1] = Color.BLACK;
            a[2] = Color.LTGRAY;
            a[3] = Color.BLACK;
            a[4] = Color.WHITE;
            a[5] = Color.BLACK;
            a[5] = Color.LTGRAY;

        }
      /*  Palette.from( b ).generate( new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated( Palette palette ) {
                //work with the palette here
            }
        });*/
        return a;
    }


    //TODO

    /* public static List<Palette.Swatch> getSwatch(Bitmap b) {
         List<Palette.Swatch> a;
         Palette.from(b).generate(new Palette.PaletteAsyncListener() {
             @Override
             public void onGenerated(Palette palette) {

                List<Palette.Swatch> d = palette.getSwatches();
             }
         });
         return a;
     }*/
    public static StateListDrawable makeSelector(int color, int color2, int alp) {
        StateListDrawable res = new StateListDrawable();
        res.setExitFadeDuration(400);
        res.setAlpha(alp);
        res.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(color));
        res.addState(new int[]{}, new ColorDrawable(color2));
        return res;
    }
}
