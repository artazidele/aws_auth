package com.example.aws_auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.amplifyframework.auth.cognito.result.AWSCognitoAuthSignOutResult
import com.amplifyframework.core.Amplify

class AccountActivity : AppCompatActivity() {

    private lateinit var passwordET: EditText
    private lateinit var newPasswordET: EditText
    private lateinit var signOutBtn: Button
    private lateinit var changeBtn: Button
    private lateinit var deleteBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        passwordET = findViewById(R.id.password_et)
        newPasswordET = findViewById(R.id.new_password_et)
        signOutBtn = findViewById(R.id.sign_out_btn)
        changeBtn = findViewById(R.id.change_btn)
        deleteBtn = findViewById(R.id.delete_btn)

        deleteBtn.setOnClickListener {
            deleteUser()
        }

        changeBtn.setOnClickListener {
            val password = passwordET.text.trim().toString()
            val newPassword = newPasswordET.text.trim().toString()

            if (password != "" && newPassword != "") {
                changePassword(password, newPassword)
            }
        }

        signOutBtn.setOnClickListener {
            signOut()
        }
    }

    // Funkcija paroles mainīšanai
    private fun changePassword(password: String, newPassword: String) {
        Amplify.Auth.updatePassword(password, newPassword,
            {
                // Parole veiskmīgi nomainīta
                Toast.makeText(this, "Password changed successfully.", Toast.LENGTH_LONG).show()
            },
            { Log.e("AuthQuickstart", "Password update failed", it) }
        )
    }

    // Funkcija lietotāja dzēšanai
    private fun deleteUser() {
        Amplify.Auth.deleteUser(
            {
                // Lietotājs izdzēsts
                // Dodas uz pierakstīšanās aktivitāti
                toSignIn()
            },
            { Log.e("AuthQuickStart", "Delete user failed with error", it) }
        )
    }

    // Funkcija lai dotos uz pierakstīšanās aktivitāti
    private fun toSignIn() {
        val intent = Intent(this@AccountActivity, SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        ContextCompat.startActivity(this, intent, null)
    }

    // Funkcija lietotāja izrakstīšanai
    private fun signOut() {
        Amplify.Auth.signOut { signOutResult ->
            when(signOutResult) {
                is AWSCognitoAuthSignOutResult.CompleteSignOut -> {
                    // Izrakstīšanās veiksmīga
                    // Dodas uz pierakstīšanās aktivitiāti
                    toSignIn()
                }
                is AWSCognitoAuthSignOutResult.PartialSignOut -> {
                    // Izrakstīšanās veiksmīga, bet ar kļūdām
                    // Dodas uz pierakstīšanās aktivitiāti
                    toSignIn()
                }
                is AWSCognitoAuthSignOutResult.FailedSignOut -> {
                    // Izrakstīšanās neveiksmīga
                    Log.e("AuthQuickStart", "Sign out Failed", signOutResult.exception)
                }
            }
        }
    }
}
