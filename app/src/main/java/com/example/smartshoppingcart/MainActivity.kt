package com.example.smartshoppingcart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Reference to your XML layout

        // Find views by ID
        val usernameEditText = findViewById<EditText>(R.id.username)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val signUpTextView = findViewById<TextView>(R.id.signUpTextView)

        // Login Button Click Listener
        loginButton.setOnClickListener {
            val enteredUsername = usernameEditText.text.toString()
            val enteredPassword = passwordEditText.text.toString()

            // Fetch saved credentials from SharedPreferences
            val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val savedUsername = sharedPref.getString("username", null)
            val savedPassword = sharedPref.getString("password", null)

            // Handle login logic
            when {
                savedUsername == null -> {
                    Toast.makeText(this, "Username not registered", Toast.LENGTH_SHORT).show()
                }
                enteredUsername == savedUsername && enteredPassword == savedPassword -> {
                    // Correct credentials, navigate to HomePageActivity
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HomePageActivity::class.java))
                }
                enteredUsername == savedUsername -> {
                    // Correct username but wrong password
                    Toast.makeText(this, "Password incorrect", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // Username not found (not registered)
                    Toast.makeText(this, "Username not registered", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Sign Up TextView Click Listener
        signUpTextView.setOnClickListener {
            // Navigate to SignUpActivity
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}


