package com.example.spotifyapp.viewmodels
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyapp.SpotifyRequests
import com.example.spotifyapp.callbacks.SpotifyArtistHistoryCallback
import com.example.spotifyapp.callbacks.SpotifyTrackHistoryCallback
import datamodels.SpotifyTopTracksResponse
import datamodels.SpotifyTopArtistsResponse
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class MainViewModel : ViewModel() {
    val clientID = "8e7f849f40ba4e4d80a02604da0e3a76"
    val redirectURI = "gt-wrapped://auth"
    private val _trackNames = MutableStateFlow<List<String>>(emptyList())
    val trackNames: StateFlow<List<String>> = _trackNames
    private val _artistNames = MutableStateFlow<List<String>>(emptyList())
    val artistNames: StateFlow<List<String>> = _artistNames
    val spotifyRequests = SpotifyRequests(clientID, redirectURI)

    fun retrieveSpotifyData(mAccessToken: String) {
        viewModelScope.launch {
            // Assume spotifyRequests is accessible here or passed somehow
            spotifyRequests.getSpotifyTrackHistory(mAccessToken, object :
                SpotifyTrackHistoryCallback {
                override fun onSuccess(jsonResponse: String) {
                    val json = Json { ignoreUnknownKeys = true }
                    try {
                        val trackHistory = json.decodeFromString<SpotifyTopTracksResponse>(jsonResponse)
                        _trackNames.value = trackHistory.items.map { it.name }

                        spotifyRequests.getSpotifyArtistHistory(mAccessToken, object :
                            SpotifyArtistHistoryCallback {
                            override fun onSuccess(jsonResponse: String) {
                                val json = Json { ignoreUnknownKeys = true }
                                try {
                                    val artistHistory = json.decodeFromString<SpotifyTopArtistsResponse>(jsonResponse)
                                    _artistNames.value = artistHistory.items.map { it.name }
                                } catch (e: Exception) {
                                    Log.e("SpotifyHistory", "Failed to parse Spotify Artist history: ${e.message}")
                                    // Consider how you might handle errors, possibly using another StateFlow
                                }
                            }
                            override fun onFailure(e: Exception) {
                                Log.e("SpotifyHistory", "Error fetching Spotify Artist history: ${e.message}")
                                // Handle failure, similarly to success
                            }
                        })


                    } catch (e: Exception) {
                        Log.e("SpotifyHistory", "Failed to parse Spotify Tracks history: ${e.message}")
                        // Consider how you might handle errors, possibly using another StateFlow
                    }
                }

                override fun onFailure(e: Exception) {
                    Log.e("SpotifyHistory", "Error fetching Spotify Tracks history: ${e.message}")
                    // Handle failure, similarly to success
                }
            })


        }
    }
}
