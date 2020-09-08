package com.travels.searchtravels.api

import com.github.kittinunf.fuel.android.core.Json
import com.google.gson.JsonElement
import com.preview.planner.Define
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

public interface ServerApi {
    public companion object Factory {
        fun create(): ServerApi {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://instarlike.com/api/")
                .build()

            return retrofit.create<ServerApi>(ServerApi::class.java!!)
        }
    }


//    @FormUrlEncoded
//    @POST("searchTag")
//    fun searchTag(@Field("tag") tag: String): Observable<TagsResponse>


}