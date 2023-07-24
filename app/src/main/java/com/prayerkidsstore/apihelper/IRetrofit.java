/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  java.lang.Object
 *  okhttp3.MultipartBody
 *  okhttp3.MultipartBody$Part
 *  okhttp3.RequestBody
 *  retrofit2.Call
 *  retrofit2.http.Body
 *  retrofit2.http.Headers
 *  retrofit2.http.Multipart
 *  retrofit2.http.POST
 *  retrofit2.http.Part
 */
package com.prayerkidsstore.apihelper;

import com.google.gson.JsonObject;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface IRetrofit {



    @GET("province")
    Call<JsonObject> getProvinsi(
            @Header("key")String keyId
            );
    @GET
    Call<JsonObject> getkabupaten(
            @Header("key")String keyId,
            @Url String url);
    @GET
    Call<JsonObject> getKecamatan(
            @Header("key")String keyId,
            @Url String url);
    @POST("cost")
    public Call<JsonObject> costpost(
            @Header("key")String keyId,
            @Body JsonObject var1);
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAf221WEg:APA91bEveHl71ItpwpzotOJDrc0pMUtvQ2DRHZTjyu3cizxcHlfCkZ2mCjr8no6QL01QAc1g6FTww3BwGdtens-KoFjo3OrQHPfKN7xA-X2QAfh0U1unpTgnroZiYEEemVTzMRFaJmJx" // Your server key refer to video for finding your server key
            }
    )
    @POST("fcm/send")
    public Call<JsonObject> sendNotifcation(

            @Body JsonObject var1);
}

