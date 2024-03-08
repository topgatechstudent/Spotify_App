package com.example.spotifyapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color.Companion.Cyan
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spotifyapp.ui.theme.LightBlue
import com.example.spotifyapp.ui.theme.Purple
import com.spotify.sdk.android.auth.AuthorizationClient
import com.example.spotifyapp.callbacks.SpotifyHistoryCallback
import datamodels.SpotifyHistoryResponse
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    val clientID = "3e5170dac3b84657bd5747aa48749987"
    val redirectURI = "gt-wrapped://auth"
    val spotifyRequests = SpotifyRequests(clientID, redirectURI)
    private lateinit var mAccessToken : String
    private lateinit var mAccessCode : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val username = intent.getStringExtra("username")
        setContent {
            MainScreen(username ?: "User")
        }
    }

    @Composable
    fun MainScreen(username : String) {
        val gradientColors = listOf(Cyan, LightBlue, Purple)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier,
                text = "Welcome to Spotify Wrapped $username!",
                fontSize = 30.sp,
                style = TextStyle(
                    brush = Brush.linearGradient(
                        colors = gradientColors
                    )
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    spotifyRequests.getToken(this@MainActivity)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                androidx.compose.material.Text("Login With Spotify")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    retrieveSpotifyHistory()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                androidx.compose.material.Text("Get Wrapped Playlist")
            }
        }
    }

    fun retrieveSpotifyHistory() {
        if (!::mAccessToken.isInitialized) {
            Toast.makeText(this@MainActivity, "Access token not initialized", Toast.LENGTH_LONG).show()
            return
        }
        spotifyRequests.getSpotifyHistory(mAccessToken, object : SpotifyHistoryCallback {
            override fun onSuccess(jsonResponse: String) {
                Log.d("SpotifyHistory", jsonResponse)
                val json = Json { ignoreUnknownKeys = true } // Create a Json instance with a configuration to ignore unknown keys
                try {
                    // Decode the JSON string into Kotlin objects
                    val trackHistory = json.decodeFromString<SpotifyHistoryResponse>(jsonResponse)
                    Log.d("SpotifyHistory", trackHistory.toString())

                } catch (e: Exception) {
                    // Handle possible parsing errors
                    runOnUiThread {
                        Log.d("SpotifyHistory", "Failed to parse Spotify history: ${e.message}")
                        Toast.makeText(this@MainActivity, "Failed to parse Spotify history: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(e: Exception) {
                // Handle failure
                runOnUiThread {
                    // Update your UI or notify the user of the error
                    Toast.makeText(this@MainActivity, "Error fetching Spotify history: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val response = AuthorizationClient.getResponse(resultCode, data)

        // Check which request code is present (if any)
        if (AuthenticationActivity.AUTH_TOKEN_REQUEST_CODE == requestCode) {
            mAccessToken = response.accessToken
            Log.d("Token", mAccessToken)
            spotifyRequests.getCode(this@MainActivity)
        } else if (AuthenticationActivity.AUTH_CODE_REQUEST_CODE == requestCode) {
            mAccessCode = response.code
            Log.d("Code", mAccessCode)
        }
    }
}