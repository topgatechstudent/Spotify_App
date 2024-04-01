package com.example.spotifyapp.callbacks;

public interface SpotifyArtistHistoryCallback {
    void onSuccess(String jsonResponse);
    void onFailure(Exception e);
}
