package com.shliama.augmentedvideotutorial.DataHandling;

import android.graphics.Bitmap;
import android.util.Pair;

import java.util.ArrayList;

public class Dataholder {
    public static String baseUrl = "";
    public static ArrayList<MagazineResult> magazineVersion = null;
    public static ArrayList<Pair<String, String>> photos = new ArrayList<>();
    public static ArrayList<Bitmap> photosBitmaps = new ArrayList<>();
    public static final String testUrl = "https://itfamili.zavoshsoftware.com/";
    public static boolean processDone = false;
    public static Bitmap image = null;

}
