package com.returno.dynasty.admin.utils;

import android.content.Context;
import android.net.Uri;

import com.iceteck.silicompressorr.SiliCompressor;
import com.returno.dynasty.admin.listeners.CompressListener;

import java.io.File;

public class FileUtils {
    public void compressFile(String file, Context context,CompressListener listener){
      String result = SiliCompressor.with(context).compress(Uri.fromFile(new File(file)).toString(), new File(getCompressDir(context)));
     listener.onComplete(Uri.parse(result));
    }

    public static String getCompressDir(Context context) {
        File dir = new File(context.getCacheDir(),"Compressed");

        if (!dir.exists()){
            dir.mkdirs();
        }

        return dir.getAbsolutePath();
    }


}
