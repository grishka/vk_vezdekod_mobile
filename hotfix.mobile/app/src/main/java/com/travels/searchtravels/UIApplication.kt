package com.preview.planner

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex

import retrofit2.Retrofit

class UILApplication : Application() {



    override fun onCreate() {
        super.onCreate()
//        val configy = YandexMetricaConfig.newConfigBuilder("cea891ed-dd21-4717-83f9-5b902173d764").build()
//        // Initializing the AppMetrica SDK.
//        YandexMetrica.activate(applicationContext, configy)
//        // Automatic tracking of user activity.
//        YandexMetrica.enableActivityAutoTracking(this)
//        Realm.init(this)
//        val config = RealmConfiguration.Builder().build()
//        Realm.setDefaultConfiguration(config)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)


        MultiDex.install(this)
    }


}