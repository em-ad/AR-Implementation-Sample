package com.shliama.augmentedvideotutorial;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

interface apiInterfaces {
    @POST("ar/Magzine/GetList")
    Call<ResponseBody> getLatestVersion();

    @FormUrlEncoded
    @POST("ar/Arasset/Get")
    Call<ResponseBody> getAsset(@Field("MagzineVersion") int version);
}
