package com.example.spotifyapp;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import com.example.spotifyapp.callbacks.SpotifyArtistHistoryCallback;
import com.example.spotifyapp.callbacks.SpotifyTrackHistoryCallback;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SpotifyRequests {
    public static String CLIENT_ID;
    public static String REDIRECT_URI;

    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;
    private Call mCall;
    private final OkHttpClient mOkHttpClient = new OkHttpClient();

    public SpotifyRequests(String clientId, String redirectUri) {
        CLIENT_ID = clientId;
        REDIRECT_URI = redirectUri;
    }

    /**
     * Get token from Spotify
     * This method will open the Spotify login activity and get the token
     * What is token?
     * https://developer.spotify.com/documentation/general/guides/authorization-guide/
     */
    public void getToken(Activity activity) {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(activity, AUTH_TOKEN_REQUEST_CODE, request);
    }

    /**
     * Get code from Spotify
     * This method will open the Spotify login activity and get the code
     * What is code?
     * https://developer.spotify.com/documentation/general/guides/authorization-guide/
     */
    public void getCode(Activity activity) {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.CODE);
        AuthorizationClient.openLoginActivity(activity, AUTH_CODE_REQUEST_CODE, request);
    }

    public void getSpotifyTrackHistory(String mAccessToken, SpotifyTrackHistoryCallback callback) {
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/tracks?limit=10")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();
        cancelCall();
        mCall = mOkHttpClient.newCall(request);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final String jsonResponse = response.body().string();
                    callback.onSuccess(jsonResponse);
                    Log.d("HTTP", "Response: " + jsonResponse);
                } catch (IOException e) {
                    Log.d("HTTP", "Failed to read response body: " + e);
                    callback.onFailure(e);
                }
            }
        });
    }

    public void getSpotifyArtistHistory(String mAccessToken, SpotifyArtistHistoryCallback callback) {
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/artists?limit=10")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();
        cancelCall();
        mCall = mOkHttpClient.newCall(request);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    callback.onSuccess(jsonObject.toString());
                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    callback.onFailure(e);
                }
            }
        });
    }


    /**
     * Get authentication request
     *
     * @param type the type of the request
     * @return the authentication request
     */
    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[] { "user-read-email", "user-read-recently-played", "user-top-read" }) // <--- Change the scope of your requested token here
                .setCampaign("your-campaign-token")
                .build();
    }

    /**
     * Gets the redirect Uri for Spotify
     *
     * @return redirect Uri object
     */
    private Uri getRedirectUri() {
        return Uri.parse(REDIRECT_URI);
    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }
}
