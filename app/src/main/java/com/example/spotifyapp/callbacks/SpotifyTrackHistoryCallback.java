package com.example.spotifyapp.callbacks;

public interface SpotifyTrackHistoryCallback {
    void onSuccess(String jsonResponse);
    void onFailure(Exception e);
}
