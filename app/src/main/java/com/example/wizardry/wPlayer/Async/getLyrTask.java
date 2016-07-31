package com.example.wizardry.wPlayer.Async;

/**
 * Created by Wizardry on 06/06/2016.
 */

import android.os.AsyncTask;

import com.example.wizardry.wPlayer.Helpers.MetadataHelper;


public class getLyrTask extends AsyncTask<String, Integer, String> {

    @Override
    protected String doInBackground(String... params) {
        return new MetadataHelper(params[0]).getLirycs();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
