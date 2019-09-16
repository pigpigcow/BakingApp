package com.example.bakingapp.recipe;

import com.example.bakingapp.helpers.ListHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Step implements DisplayTexable {
    private static final String ID_KEY = "id";
    private static final String SHORT_DESCRIPTION_KEY = "shortDescription";
    private static final String DESCRIPTION_KEY = "description";
    private static final String VIDEO_URL_KEY = "videoURL";
    private static final String THUMBNAIL_URL_KEY = "thumbnailURL";

    private final int id;
    private final String shortDescription;
    private final String description;
    private final String videoURL;
    private final String thumbnailURL;

    public Step(int id, String shortDescription, String description, String videoURL, String thumbnailURL) {
        this.id = id;
        this.shortDescription = shortDescription;
        this.description = description;
        this.videoURL = videoURL;
        this.thumbnailURL = thumbnailURL;
    }

    public static List<Step> getListFromJSONList(JSONArray list) throws JSONException {
        List<Step> result = new ArrayList<>();
        if (list != null) {
            for (int i = 0; i < list.length(); i++) {
                final JSONObject json = list.getJSONObject(i);
                ListHelper.addObjectToLIstIfNotNull(result, getFromJSON(json));
            }
        }
        return result;
    }

    private static Step getFromJSON(JSONObject json) throws JSONException {
        Step result = null;
        if (json != null) {
            final int id = json.getInt(ID_KEY);
            final String shortDescription = json.getString(SHORT_DESCRIPTION_KEY);
            final String description = json.getString(DESCRIPTION_KEY);
            final String videoURL = json.getString(VIDEO_URL_KEY);
            final String thumbnailURL = json.getString(THUMBNAIL_URL_KEY);
            result = new Step(id, shortDescription, description, videoURL, thumbnailURL);
        }
        return result;
    }

    public int getId() {
        return id;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    @Override
    public String getDisplayText() {
        return getShortDescription();
    }
}
