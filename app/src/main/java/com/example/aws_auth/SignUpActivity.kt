package com.example.aws_auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify

class SignUpActivity : AppCompatActivity() {

    private lateinit var emailET: EditText
    private lateinit var usernameET: EditText
    private lateinit var passwordET: EditText
    private lateinit var signUpBtn: Button
    private lateinit var toInBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        emailET = findViewById(R.id.email_et)
        usernameET = findViewById(R.id.username_et)
        passwordET = findViewById(R.id.password_et)
        signUpBtn = findViewById(R.id.sign_up_btn)
        toInBtn = findViewById(R.id.to_in_btn)

        signUpBtn.setOnClickListener {
            val email = emailET.text.trim().toString()
            val username = usernameET.text.trim().toString()
            val password = passwordET.text.trim().toString()

            if (email != "" && username != "" && password != "") {
                // izsauc pierakstīšanās funkciju
                registerUser(email, password, username)
            }
        }

        toInBtn.setOnClickListener {
            // dodas uz SignInActivity
            toSignIn()
        }
    }

    // Funkcija lai dotos uz pierakstīšanās aktivitāti
    private fun toSignIn() {
        val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        ContextCompat.startActivity(this, intent, null)
    }

    // Funkcija lietotāja reģistrēšanai
    private fun registerUser(email: String, password: String, username: String) {
        val options = AuthSignUpOptions.builder()
            .userAttribute(AuthUserAttributeKey.email(), email)
            .build()
        Amplify.Auth.signUp(username, password, options,
            {
                // reģistrēšanās veiksmīga
                // atver epasta pārbaudes logu
                openEmailVerificationWindow(username)
            },
            {
                // reģistrēšanās nav veiksmīga
                Log.e ("AuthQuickStart", "Sign up failed", it)
            }
        )
    }

    // Funkcija epasta pārbaudes loga atvēršanai
    private fun openEmailVerificationWindow(username: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.verify_email_layout, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
        val alertDialog = builder.show()
        dialogView.findViewById<EditText>(R.id.username_et).setText(username)

        dialogView.findViewById<Button>(R.id.send_btn).setOnClickListener {
            val username = dialogView.findViewById<EditText>(R.id.username_et).text.toString()
            val code = dialogView.findViewById<EditText>(R.id.code_et).text.toString()
            verifyEmail(username, code)
        }
        dialogView.findViewById<Button>(R.id.close_btn).setOnClickListener {
            alertDialog.dismiss()
        }
    }

    // Funkcija epasta pārbaudei
    private fun verifyEmail(username: String, code: String) {
        Amplify.Auth.confirmSignUp(
            username, code,
            { result ->
                if (result.isSignUpComplete) {
                    toSignIn()
                } else {
                    Log.i("AuthQuickstart","Confirm sign up not complete")
                }
            },
            { Log.e("AuthQuickstart", "Failed to confirm sign up", it) }
        )
    }
}