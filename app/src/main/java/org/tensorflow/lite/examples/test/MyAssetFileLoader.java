package org.tensorflow.lite.examples.test;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;

public class MyAssetFileLoader {
    public static InputStream loadAssetFile(Context context, String fileName) {
        AssetManager assetManager = context.getAssets();
        try {
            return assetManager.open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}