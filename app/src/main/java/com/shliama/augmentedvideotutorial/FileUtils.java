package com.shliama.augmentedvideotutorial;

import android.os.Environment;
import android.util.Log;

import java.io.File;

class FileUtils {

    public static String getFilePath(String valueOf, String magazineIssue, FileType type) {
        String extension = "";
        switch (type){
            case VIDEO:
                extension = ".mp4";
                break;
            case IMAGE:
                extension = ".jpg";
                break;
        }
        String dirName;
        dirName = Environment.getExternalStorageDirectory().getPath() + File.separator + "itfamiliar"  +
                File.separator + "download" + File.separator + magazineIssue + File.separator + valueOf + extension;
        if (new File(dirName).exists())
            return new File(dirName).getPath();
        else return "";
    }

    public enum FileType {
        VIDEO, IMAGE
    }

    public static void removeFile(String id, String issue, FileType type) {
        String extension = "";
        switch (type){
            case VIDEO:
                extension = ".mp4";
                break;
            case IMAGE:
                extension = ".jpg";
                break;
        }
        String dirName;
        dirName = Environment.getExternalStorageDirectory().getPath() + File.separator + "itfamiliar"  +
                File.separator + "download" + File.separator + issue + File.separator + id + extension;
        File toRemove = new File(dirName);
        if(toRemove.delete()){
            Log.e("tag", "Removed File: " + dirName );
        }
    }

    public static boolean checkFileExistence(String id, String issue, FileType type) {
        String extension = "";
        switch (type){
            case VIDEO:
                extension = ".mp4";
                break;
            case IMAGE:
                extension = ".jpg";
                break;
        }
        String dirName;
        dirName = Environment.getExternalStorageDirectory().getPath() + File.separator + "itfamiliar"  +
                File.separator + "download" + File.separator + issue + File.separator + id + extension;

        if(new File(dirName).exists()) {
            Log.e("tag", dirName + " FILE EXISTS" );
            return true;
        }
        Log.e("tag", dirName + " FILE DOESN'T EXISTS" );
        return false;
    }
}
