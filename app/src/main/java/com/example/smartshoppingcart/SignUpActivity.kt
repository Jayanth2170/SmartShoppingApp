package com.example.smartshoppingcart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.style.TextAlign
import com.example.smartshoppingcart.ui.theme.SmartShoppingCartTheme

class SignUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartShoppingCartTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SignUpScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun SignUpScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var registrationMessage by remember { mutableStateOf("") }

    Column(modifier = modifier.padding(32.dp)) {
        // Register Heading
        Text(
            text = "Sign Up",
            style = MaterialTheme.typography.headlineLarge.copy(color = Color.Blue),
            modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Username Field
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth().padding(top = 30.dp)
        )

        // Password Field
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Create password") },
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
            visualTransformation = PasswordVisualTransformation()
        )

        // Confirm Password Field
        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm password") },
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
            visualTransformation = PasswordVisualTransformation()
        )

        // Register Button
        Button(
            onClick = {
                if (username.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                    if (password == confirmPassword) {
                        val sharedPref = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                        val editor = sharedPref.edit()
                        editor.putString("username", username)
                        editor.putString("password", password)
                        editor.apply()

                        registrationMessage = "Registration Successful"
                        Toast.makeText(context, registrationMessage, Toast.LENGTH_SHORT).show()
                    } else {
                        registrationMessage = "Passwords do not match"
                        Toast.makeText(context, registrationMessage, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    registrationMessage = "Please fill out all fields"
                    Toast.makeText(context, registrationMessage, Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 30.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCCCCFF)) // Use containerColor
        ) {
            Text(text = "Register", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Login Redirect
        Text(
            text = "Already have an account? Login",
            color = Color(0xFF007BFF),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(align = Alignment.Center)
                .padding(top = 20.dp)
                .clickable {
                    // Redirect to Login (MainActivity)
                    val intent = Intent(context, MainActivity::class.java)  // Replace MainActivity with your login activity if different
                    context.startActivity(intent)
                }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpPreview() {
    SmartShoppingCartTheme {
        SignUpScreen()
    }
}






