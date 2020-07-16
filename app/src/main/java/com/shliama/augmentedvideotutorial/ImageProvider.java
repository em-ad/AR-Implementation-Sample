package com.shliama.augmentedvideotutorial;

import java.util.ArrayList;

class ImageProvider {
    public static ArrayList<String> getImagePath(String magazineIssue, int amount){
        ArrayList<String> images = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            if(FileUtils.checkFileExistence(String.valueOf(amount), magazineIssue, FileUtils.FileType.IMAGE))
                images.add(FileUtils.getFilePath(String.valueOf(amount), magazineIssue, FileUtils.FileType.IMAGE));
        }
        return images;
    }
}
