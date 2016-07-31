package com.example.wizardry.wPlayer.Async;

/**
 * Created by admin on 1/6/16.
 */

import android.os.AsyncTask;

import com.example.wizardry.wPlayer.Helpers.MetadataHelper;

/**
 * Created by admin on 1/6/16.
 */

public class getMetadataTask extends AsyncTask<String, Integer, MetadataHelper> {

    @Override
    protected MetadataHelper doInBackground(String... params) {

        return new MetadataHelper(params[0]);
    }

    @Override
    protected void onPostExecute(MetadataHelper metadataHelper) {
        super.onPostExecute(metadataHelper);
    }
}
