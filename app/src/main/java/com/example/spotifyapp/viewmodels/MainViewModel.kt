package com.example.spotifyapp.viewmodels
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyapp.SpotifyRequests
import com.example.spotifyapp.callbacks.SpotifyArtistHistoryCallback
import com.example.spotifyapp.callbacks.SpotifyTrackHistoryCallback
import datamodels.SpotifyArtistHistoryResponse
import datamodels.SpotifyTrackHistoryResponse
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class MainViewModel : ViewModel() {
    val clientID = "3e5170dac3b84657bd5747aa48749987"
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
                        val trackHistory = json.decodeFromString<SpotifyTrackHistoryResponse>(jsonResponse)
                        _trackNames.value = trackHistory.items.map { it.track.name }
                    } catch (e: Exception) {
                        Log.e("SpotifyHistory", "Failed to parse Spotify history: ${e.message}")
                        // Consider how you might handle errors, possibly using another StateFlow
                    }
                }

                override fun onFailure(e: Exception) {
                    Log.e("SpotifyHistory", "Error fetching Spotify history: ${e.message}")
                    // Handle failure, similarly to success
                }
            })

            spotifyRequests.getSpotifyArtistHistory(mAccessToken, object :
                SpotifyArtistHistoryCallback {
                override fun onSuccess(jsonResponse: String) {
                    val json = Json { ignoreUnknownKeys = true }
                    try {
                        val artistHistory = json.decodeFromString<SpotifyArtistHistoryResponse>(jsonResponse)
                        _artistNames.value = artistHistory.items.map { it.name }
                    } catch (e: Exception) {
                        Log.e("SpotifyHistory", "Failed to parse Spotify history: ${e.message}")
                        // Consider how you might handle errors, possibly using another StateFlow
                    }
                }
                override fun onFailure(e: Exception) {
                    Log.e("SpotifyHistory", "Error fetching Spotify history: ${e.message}")
                    // Handle failure, similarly to success
                }
            })


        }
    }
}
