package com.example.spotifyapp

import android.os.Bundle
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color.Companion.Cyan
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.spotifyapp.ui.theme.LightBlue
import com.example.spotifyapp.ui.theme.Purple
import com.example.spotifyapp.viewmodels.MainViewModel

class MainActivity : ComponentActivity() {
    private lateinit var mAccessToken : String
    private lateinit var mAccessCode : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val username = intent.getStringExtra("username")
        setContent {
            if (username != null) {
                MyApp(username)
            }
        }
    }

    @Composable
    fun MyApp(username: String) {
        // Obtain ViewModel instance
        val mainViewModel: MainViewModel = viewModel()

        // Now you can use mainViewModel
        myAppContent(mainViewModel, username)
    }

    @Composable
    fun myAppContent(viewModel: MainViewModel, username: String) {
        val navController = rememberNavController()
        val trackNames by viewModel.trackNames.collectAsState()

        NavHost(navController = navController, startDestination = "main") {
            composable("main") {
                MainScreen(username, navController, viewModel)
            }
            composable("wrapped") {
                Wrapped(trackNames, navController)
            }
        }
    }

    @Composable
    fun MainScreen(username : String,navController: NavController, viewModel: MainViewModel) {
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

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (!::mAccessToken.isInitialized) {
                        Toast.makeText(this@MainActivity, "Please login with Spotify first", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    viewModel.retrieveSpotifyHistory(mAccessToken)
                    navController.navigate("wrapped")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                androidx.compose.material.Text("Get Wrapped Playlist")
            }
        }
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Wrapped(trackNames: List<String>, navController: NavController) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Wrapped Tracks") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
        ) { innerPadding ->
            LazyColumn(modifier = Modifier.padding(innerPadding)) {
                items(trackNames) { trackName ->
                    Text(text = trackName, modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}