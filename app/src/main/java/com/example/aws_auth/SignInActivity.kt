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
import com.amplifyframework.core.Amplify

class SignInActivity : AppCompatActivity() {

    private lateinit var usernameET: EditText
    private lateinit var passwordET: EditText
    private lateinit var signInBtn: Button
    private lateinit var resetBtn: Button
    private lateinit var toUpBtn: Button
    private lateinit var alertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        usernameET = findViewById(R.id.username_et)
        passwordET = findViewById(R.id.password_et)
        signInBtn = findViewById(R.id.sign_in_btn)
        resetBtn = findViewById(R.id.reset_btn)
        toUpBtn = findViewById(R.id.to_up_btn)

        toUpBtn.setOnClickListener {
            toSignUp()
        }

        signInBtn.setOnClickListener {
            val username = usernameET.text.trim().toString()
            val password = passwordET.text.trim().toString()

            if (username != "" && password != "") {
                // izsauc pierakstīšanās funkciju
                signIn(username, password)
            }
        }

        resetBtn.setOnClickListener {
            val username = usernameET.text.trim().toString()
            // izsauc paroles atjaunošanas funkciju
            resetPassword1(username)
        }
    }

    // Funkcija lietotāja pierakstīšanai
    private fun signIn(username: String, password: String) {
        Amplify.Auth.signIn(username, password,
            { result ->
                if (result.isSignedIn) {
                    toAccount()
                } else {
                    Log.i("AuthQuickstart", "Sign in not complete")
                }
            },
            { Log.e("AuthQuickstart", "Failed to sign in", it) }
        )
    }

    // Funkcija lai dotos uz reģistrēšanās aktivitāti
    private fun toSignUp() {
        val intent = Intent(this@SignInActivity, SignUpActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        ContextCompat.startActivity(this, intent, null)
    }

    // Funkcija lai dotos uz konta aktivitāti
    private fun toAccount() {
        val intent = Intent(this@SignInActivity, AccountActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        ContextCompat.startActivity(this, intent, null)
    }

    // Funkcijas paroles atjaunošanai
    private fun resetPassword1(username: String) {
        Amplify.Auth.resetPassword(username,
            {
                openResetPasswordWindow(username)
            },
            { Log.e("AuthQuickstart", "Password reset failed", it) }
        )
    }

    private fun openResetPasswordWindow(username: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.reset_password_layout, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
        val alertDialog = builder.show()
        this.alertDialog = alertDialog

        dialogView.findViewById<EditText>(R.id.username_et).setText(username)

        dialogView.findViewById<Button>(R.id.reset_btn).setOnClickListener {
            val username = dialogView.findViewById<EditText>(R.id.username_et).text.toString()
            val password = dialogView.findViewById<EditText>(R.id.password_et).text.toString()
            val code = dialogView.findViewById<EditText>(R.id.code_et).text.toString()
            resetPassword2(username, password, code)
        }
        dialogView.findViewById<Button>(R.id.close_btn).setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun resetPassword2(username: String, password: String, code: String) {
        Amplify.Auth.confirmResetPassword(username, password, code,
            {
                alertDialog.dismiss()
            },
            { Log.e("AuthQuickstart", "Failed to confirm password reset", it) }
        )
    }
}