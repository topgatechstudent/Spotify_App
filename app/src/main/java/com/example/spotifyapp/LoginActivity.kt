package com.example.spotifyapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController
import com.google.firebase.database.FirebaseDatabase
import androidx.compose.ui.platform.LocalContext

class LoginActivity : ComponentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            myApp()
        }
    }
}

@Composable
fun myApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(onNavigateToCreateAccount = { navController.navigate("createAccount") })
        }
        composable("createAccount") {
            AccountCreationScreen(navController = navController);
        }
    }
}

@Composable
fun AccountCreationScreen(navController: NavController) {
    val context = LocalContext.current
    val database = FirebaseDatabase.getInstance()
    val usersRef = database.getReference("Users")
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { androidx.compose.material.Text("Create Account") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Username input field
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Password input field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Button to create account
            Button(
                onClick = {
                    val userRef = usersRef.child(username)
                    usersRef.child(username).get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val snapshot = task.result
                            if (snapshot.exists()) {
                                Toast.makeText(
                                    context,
                                    "Username already exists.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@addOnCompleteListener
                            } else {
                                userRef.child("password").setValue(password)
                                    .addOnSuccessListener {
                                        // Handle success
                                        Toast.makeText(
                                            context,
                                            "Account created successfully!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navController.navigateUp()
                                    }
                                    .addOnFailureListener { exception ->
                                        // Handle failure
                                        Toast.makeText(
                                            context,
                                            "Failed to create user account.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        } else {
                            Toast.makeText(context, "Failed to query the database.", Toast.LENGTH_SHORT)
                                .show()
                            return@addOnCompleteListener
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                androidx.compose.material.Text("Create Account")
            }
        }
    }
}

@Composable
fun LoginScreen(onNavigateToCreateAccount: () -> Unit) {
    val context = LocalContext.current
    val usersRef = FirebaseDatabase.getInstance().getReference("Users")
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { androidx.compose.material.Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { androidx.compose.material.Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                usersRef.child(username).get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val snapshot = task.result
                        val storedPassword = snapshot.child("password").value.toString()

                        // Comparing the provided password with the stored one
                        if (storedPassword == password) {
                            // Navigating to MainActivity if the password is correct
                            val intent = Intent(context, MainActivity::class.java)
                            context.startActivity(intent)
                        } else {
                            // Show a toast if the password is incorrect
                            Toast.makeText(
                                context,
                                "Username or password is incorrect.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        // Show a toast if there was an error querying the database
                        Toast.makeText(context, "Failed to query the database.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            androidx.compose.material.Text("Login")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onNavigateToCreateAccount,
            modifier = Modifier.fillMaxWidth()
        ) {
            androidx.compose.material.Text("Create Account")
        }
    }
}
