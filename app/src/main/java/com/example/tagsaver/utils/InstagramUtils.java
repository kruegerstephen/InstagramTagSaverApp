package com.example.tagsaver.utils;


import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Created by nathan on 6/12/17.
 */

public class InstagramUtils {

    private final static String INSTAGRAM_BASE_URL = "https://api.instagram.com/v1/tags/";
    private final static String INSTAGRAM_QUERY_PARAM = "q";
    private final static String INSTAGRAM_ACCESS_PARAM = "access_token";


    // Client ID
    public static final String CLIENT_ID = "173788853.0d2ac8b.1f2c46ce522e445c83dbe9d23bcc0a93";

    public static class TagItem implements Serializable {
        public int count;
        public String name;
    }

    public static String buildTagURL(String tag) {
        return Uri.parse(INSTAGRAM_BASE_URL + tag).buildUpon()
                //.appendQueryParameter(INSTAGRAM_QUERY_PARAM, tag)
                .appendQueryParameter(INSTAGRAM_ACCESS_PARAM, CLIENT_ID)
                .build()
                .toString();
    }

    public static TagItem parseTagJSON(String tagJSON) {
        try {
            JSONObject jsonObj = new JSONObject(tagJSON);
            JSONObject tagObj = jsonObj.getJSONObject("data");

            TagItem tItem = new TagItem();
            tItem.name = tagObj.getString("name");
            tItem.count  = tagObj.getInt("media_count");
            return tItem;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }




}
