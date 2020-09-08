package com.travels.searchtravels.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by alexeybalabanov on 24.11.14.
 */
public class ConverterUtil {
    public static String getStringIdsFromJson(final JSONObject mUsers){
        JSONArray idUsers = null;
        String ids = "";
        try {
            idUsers = mUsers.getJSONArray("items");
            for(int i = 0; i < idUsers.length(); i++){
                String id = "" + idUsers.get(i);
                if(!ids.isEmpty()) ids += ",";
                ids += id;
                if(i>200){
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return ids;
    }

    public static String getStringIdsFromJsonBookmarks(final JSONObject mUsers){
        JSONArray idUsers = null;
        String ids = "";
        try {
            idUsers = mUsers.getJSONArray("items");
            for(int i = 0; i < idUsers.length(); i++){
                String id = "" + idUsers.getJSONObject(i).getInt("id");
                if(!ids.isEmpty()) ids += ",";
                ids += id;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ids;
    }

//    public static int getIntFromStringDouble(String doubleStr){
//        double d;
//        try {
//            d = Double.parseDouble(doubleStr);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return 0;
//        }
//
//        return (int) d;
//    }
//    public static long getLongFromString(String doubleStr){
//        double d;
//        try {
//            d = Double.parseDouble(doubleStr);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return 0;
//        }
//
//        return (long) d;
//    }

    public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if(json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    public static Map toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    public static long getLongFromString(String doubleStr){
        double d;
        try {
            d = Double.parseDouble(doubleStr);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

        return (long) d;
    }
    public static int getIntFromStringDouble(String doubleStr){
        double d;
        try {
            d = Double.parseDouble(doubleStr);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

        return (int) d;
    }
}
