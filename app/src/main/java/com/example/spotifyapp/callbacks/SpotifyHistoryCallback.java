package com.example.spotifyapp.callbacks;

import org.json.JSONObject;

public interface SpotifyHistoryCallback {
    void onSuccess(String jsonResponse);
    void onFailure(Exception e);
}
