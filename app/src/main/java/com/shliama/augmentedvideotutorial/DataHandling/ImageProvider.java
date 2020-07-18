package com.shliama.augmentedvideotutorial.DataHandling;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

public class ImageProvider {
    public static ArrayList<String> getImagePath(String magazineIssue, int amount){
        ArrayList<String> images = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            if(FileUtils.checkFileExistence(String.valueOf(amount), magazineIssue, FileUtils.FileType.IMAGE))
                images.add(FileUtils.getFilePath(String.valueOf(amount), magazineIssue, FileUtils.FileType.IMAGE));
        }
        return images;
    }
}
