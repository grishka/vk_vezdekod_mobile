package com.travels.searchtravels.api

import com.google.gson.JsonElement
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

public interface GeolocationApi {
    public companion object Factory {
        fun create(): GeolocationApi {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://maps.googleapis.com/maps/api/geocode/")
                .build()

            return retrofit.create<GeolocationApi>(GeolocationApi::class.java!!)
        }
    }



    @GET("json?location_type=APPROXIMATE&key=AIzaSyAOsW6LcYlfiaT6ze2uSA_0dGpy8BcSDhI")
    fun getCity(@Query("latlng") latlng: String, @Query("language") language: String): Observable<JsonElement>




}