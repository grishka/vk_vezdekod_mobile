package com.preview.planner.prefs

import android.content.Context
import androidx.preference.PreferenceManager

object AppPreferences {

    private val SHOW_WEBVIEW = "SHOW_WEBVIEW"
    private val IS_CHECKED = "IS_CHECKED"
    private val IS_FIRST_LAUNCH = "IS_FIRST_LAUNCH"
    private val IS_CHECKED_GOOGLE = "IS_CHECKED_GOOGLE"
    private val IS_WATCHED_APP = "IS_WATCHED_APP"
    private val TOKEN_VIP = "TOKEN_VIP"
    private val AVATAR = "AVATAR"
    private val USER_ID = "USER_ID"
    private val USERNAME = "USERNAME"
    private val SHOW_HASHTAGS = "SHOW_HASHTAGS"
    private val USER_NAME = "USER_NAME"
    private val TOKEN_PASSWORD = "TOKEN_PASSWORD"
    private val SET_FOLLOWED = "SET_FOLLOWED"
    private val TOKEN = "TOKEN"
    private val IS_FREE_COINS = "IS_FREE_COINS"
    private val BOUGHT_500_COINS = "BOUGHT_500_COINS"
    private val BOUGHT_1000_COINS = "BOUGHT_1000_COINS"
    private val BOUGHT_100_COINS = "BOUGHT_100_COINS"
    private val BOUGHT_5000_COINS = "BOUGHT_5000_COINS"
    private val BOUGHT_10000_COINS = "BOUGHT_10000_COINS"
    private val BOUGHT_50000_COINS = "BOUGHT_50000_COINS"
    private val BOUGHT_100000_COINS = "BOUGHT_100000_COINS"
    private val OPENED_TAB = "OPENED_TAB"
    private val EARN_COINS = "EARN_COINS"

    fun setFreeCoins(ctx: Context, b: Boolean) {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        val ed = sPref.edit()
        ed.putBoolean(IS_FREE_COINS, b)
        ed.apply()
    }

    fun isFreeCoins(ctx: Context): Boolean {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        return sPref.getBoolean(IS_FREE_COINS, false)
    }

    fun setEarnCoins(ctx: Context, b: Boolean) {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        val ed = sPref.edit()
        ed.putBoolean(EARN_COINS, b)
        ed.apply()
    }

    fun isEarnCoins(ctx: Context): Boolean {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        return sPref.getBoolean(EARN_COINS, false)
    }
    fun isShowWebView(ctx: Context): Boolean {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        return sPref.getBoolean(SHOW_WEBVIEW, true)
    }

    fun setShowWebView(ctx: Context) {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        val ed = sPref.edit()
        ed.putBoolean(SHOW_WEBVIEW, false)
        ed.apply()
    }
    fun isShowHashtags(ctx: Context): Boolean {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        return sPref.getBoolean(SHOW_HASHTAGS, false)
    }

    fun setShowHashtags(ctx: Context) {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        val ed = sPref.edit()
        ed.putBoolean(SHOW_HASHTAGS, true)
        ed.apply()
    }

    fun isChecked(ctx: Context): Boolean {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        return sPref.getBoolean(IS_CHECKED, false)
    }

    fun setChecked(ctx: Context) {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        val ed = sPref.edit()
        ed.putBoolean(IS_CHECKED, true)
        ed.apply()
    }
    fun isFirstLaunch(ctx: Context): Boolean {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        return sPref.getBoolean(IS_FIRST_LAUNCH, true)
    }

    fun setFirstLaunch(ctx: Context) {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        val ed = sPref.edit()
        ed.putBoolean(IS_FIRST_LAUNCH, false)
        ed.apply()
    }
    fun isCheckedToGoogle(ctx: Context): Boolean {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        return sPref.getBoolean(IS_CHECKED_GOOGLE, false)
    }

    fun setCheckedToGoogle(ctx: Context) {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        val ed = sPref.edit()
        ed.putBoolean(IS_CHECKED_GOOGLE, true)
        ed.apply()
    }
    fun isWatchedApp(ctx: Context): Boolean {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        return sPref.getBoolean(IS_WATCHED_APP, false)
    }

    fun setWatchedApp(ctx: Context) {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        val ed = sPref.edit()
        ed.putBoolean(IS_WATCHED_APP, true)
        ed.apply()
    }
    fun getTokenVIP(ctx: Context): String {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        return sPref.getString(TOKEN_VIP, "")!!
    }



    fun setPassword(ctx: Context, token : String) {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        val ed = sPref.edit()
        ed.putString(TOKEN_PASSWORD, token)
        ed.apply()
    }

    fun getPassword(ctx: Context): String {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        return sPref.getString(TOKEN_PASSWORD, "")!!
    }

    fun setFollowed(ctx: Context, token : String) {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        val ed = sPref.edit()
        ed.putString(SET_FOLLOWED, token)
        ed.apply()
    }

    fun getFollowed(ctx: Context): String {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        return sPref.getString(SET_FOLLOWED, "")!!
    }

    fun setTokenVIP(ctx: Context, token : String) {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        val ed = sPref.edit()
        ed.putString(TOKEN_VIP, token)
        ed.apply()
    }

    fun setToken(ctx: Context, token : String) {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        val ed = sPref.edit()
        ed.putString(TOKEN, token)
        ed.apply()
    }

    fun getToken(ctx: Context): String {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        return sPref.getString(TOKEN, "")!!
    }

    fun setOpenedTab(ctx: Context, count : Int) {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        val ed = sPref.edit()
        ed.putInt(OPENED_TAB, count)
        ed.apply()
    }

    fun getOpenedTabCount(ctx: Context): Int {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        return sPref.getInt(OPENED_TAB, 0)!!
    }

    fun getAvatar(ctx: Context): String {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        return sPref.getString(AVATAR, "")!!
    }

    fun setAvatar(ctx: Context, token : String) {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        val ed = sPref.edit()
        ed.putString(AVATAR, token)
        ed.apply()
    }
    fun getUserID(ctx: Context): String {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        return sPref.getString(USER_ID, "")!!
    }

    fun setUserID(ctx: Context, token : String) {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        val ed = sPref.edit()
        ed.putString(USER_ID, token)
        ed.apply()
    }
    fun getUsername(ctx: Context): String {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        return sPref.getString(USERNAME, "")!!
    }

    fun setUsername(ctx: Context, token : String) {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        val ed = sPref.edit()
        ed.putString(USERNAME, token)
        ed.apply()
    }
    fun isBought100Coins(ctx: Context): Boolean {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        return sPref.getBoolean(BOUGHT_100_COINS, false)
    }
    fun setBought100Coins(ctx: Context, b: Boolean) {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        val ed = sPref.edit()
        ed.putBoolean(BOUGHT_100_COINS, true)
        ed.apply()
    }

    fun isBought500Coins(ctx: Context): Boolean {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        return sPref.getBoolean(BOUGHT_500_COINS, false)
    }

    fun setBought500Coins(ctx: Context, b: Boolean) {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        val ed = sPref.edit()
        ed.putBoolean(BOUGHT_500_COINS, true)
        ed.apply()
    }

    fun isBought1000Coins(ctx: Context): Boolean {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        return sPref.getBoolean(BOUGHT_1000_COINS, false)
    }

    fun setBought1000Coins(ctx: Context, b: Boolean) {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        val ed = sPref.edit()
        ed.putBoolean(BOUGHT_1000_COINS, true)
        ed.apply()
    }

    fun isBought5000Coins(ctx: Context): Boolean {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        return sPref.getBoolean(BOUGHT_5000_COINS, false)
    }

    fun setBought5000Coins(ctx: Context, b: Boolean) {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        val ed = sPref.edit()
        ed.putBoolean(BOUGHT_5000_COINS, true)
        ed.apply()
    }

    fun isBought10000Coins(ctx: Context): Boolean {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        return sPref.getBoolean(BOUGHT_10000_COINS, false)
    }

    fun setBought10000Coins(ctx: Context, b: Boolean) {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        val ed = sPref.edit()
        ed.putBoolean(BOUGHT_10000_COINS, true)
        ed.apply()
    }

    fun isBought50000Coins(ctx: Context): Boolean {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        return sPref.getBoolean(BOUGHT_50000_COINS, false)
    }

    fun setBought50000Coins(ctx: Context, b: Boolean) {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        val ed = sPref.edit()
        ed.putBoolean(BOUGHT_50000_COINS, true)
        ed.apply()
    }

    fun isBought100000Coins(ctx: Context): Boolean {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        return sPref.getBoolean(BOUGHT_100000_COINS, false)
    }

    fun setBought100000Coins(ctx: Context, b: Boolean) {
        val sPref = PreferenceManager
            .getDefaultSharedPreferences(ctx)
        val ed = sPref.edit()
        ed.putBoolean(BOUGHT_100000_COINS, true)
        ed.apply()
    }
//    UserDefaults.standard.set(data["user"]["profile_pic_url"].stringValue, forKey: "avatar_url")
//    UserDefaults.standard.set(data["user"]["pk"].stringValue, forKey: "user_id")
//    UserDefaults.standard.set(data["user"]["username"].stringValue, forKey: "username")
//    UserDefaults.standard.set(data["user"]["pk"].stringValue, forKey: "token")
//    UserDefaults.standard.set(data["user"]["pk"].stringValue, forKey: "google_user_id")
//    UserDefaults.standard.set(data["user"]["pk"].stringValue, forKey: "device_id")


}