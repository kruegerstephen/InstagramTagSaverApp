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

    private final static String INSTAGRAM_BASE_URL = "https://api.instagram.com/v1/tags/search";
    private final static String INSTAGRAM_QUERY_PARAM = "q";
    private final static String INSTAGRAM_ACCESS_PARAM = "access_token";


    // Client ID
    public static final String CLIENT_ID = "173788853.0d2ac8b.1f2c46ce522e445c83dbe9d23bcc0a93";

    public static class TagItem implements Serializable {
        public int count;
        public String tag;

    }

    public static String buildTagURL(String tag) {
        return Uri.parse(INSTAGRAM_BASE_URL).buildUpon()
                .appendQueryParameter(INSTAGRAM_QUERY_PARAM, tag)
                .appendQueryParameter(INSTAGRAM_ACCESS_PARAM, CLIENT_ID)
                .build()
                .toString();
    }

    public static ArrayList<TagItem> parseTagJSON(String tagJSON) {
        try {
            JSONObject tagObj = new JSONObject(tagJSON);
            JSONArray tagList = tagObj.getJSONArray("list");

            ArrayList<TagItem> tagItemsList = new ArrayList<TagItem>();
            for (int i = 0; i < tagList.length(); i++) {

                TagItem tagItem = new TagItem();
                JSONObject tagListElem = tagList.getJSONObject(i);

                tagItem.tag = tagListElem.getString("name");
                tagItem.count  = tagListElem.getInt("media_count");

                tagItemsList.add(tagItem);
            }
            return tagItemsList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }




}
