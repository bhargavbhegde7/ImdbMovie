package com.sound.bytes.asynchdownloader;

import android.content.Context;

/**
 * Created by goodbytes on 6/11/2016.
 */
public interface JsonDataHandler {
    void onJsonDownloadCompleted(Object result, Context mainAppCtxt);
}
