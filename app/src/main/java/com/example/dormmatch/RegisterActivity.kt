package com.example.dormmatch


import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.dormmatch.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.ktx.Firebase


class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload()
        }
    }

    private fun createAccount(email: String, password: String, username: String) {
        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser

                    val userProfileChangeRequest = UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build()

                    user?.updateProfile(userProfileChangeRequest)
                        ?.addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                showPopup(this, getString(R.string.information), getString(R.string.account_created), user)

                            } else {
                                // Failed to update additional user information
                            }
                        }

                } else {
                    // If sign in fails, display a message to the user.
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        val exception = task.exception as FirebaseAuthUserCollisionException
                        val errorCode = exception.errorCode

                        if (errorCode == "ERROR_EMAIL_ALREADY_IN_USE") {
                            // Wrong password
                            Toast.makeText(this, "Email already in use", Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            R.string.authentication_failed,
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
            }
        // [END create_user_with_email]
    }

    private fun updateUI(user: FirebaseUser?) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun reload() {
    }

    companion object {
        private const val TAG = "EmailPassword"
    }

    fun btnReturn(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    fun btnCreateAccount(view: View) {

        val email = findViewById<EditText>(R.id.et_email).text.toString()
        val password = findViewById<EditText>(R.id.et_password).text.toString().trim()
        val username = findViewById<EditText>(R.id.et_username).text.toString()

        var emailIsCorrect: Boolean = checkEmail(email)
        var passwordIsCorrect:Boolean = checkPassword(password)
        var usernameIsCorrect:Boolean = checkUsername(username)

        if(emailIsCorrect && passwordIsCorrect && usernameIsCorrect) {
            createAccount(email, password, username)
        }
    }

    private fun checkUsername(username: String):Boolean {

        if (username.isEmpty()) {
            findViewById<EditText>(R.id.et_username).error = getString(R.string.username_required)
            return false
        }

        return true
    }

    private fun checkEmail(email: String):Boolean {

        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

        if (email.isEmpty()) {
            findViewById<EditText>(R.id.et_email).error = getString(R.string.email_required)
            return false
        }
        else if (!email.matches(emailPattern.toRegex())) {
            findViewById<EditText>(R.id.et_email).error = getString(R.string.invalid_email)
            return false
        }

        return true
    }

    private fun checkPassword(password: String):Boolean {

        if (password.isEmpty()) {
            findViewById<EditText>(R.id.et_password).error = getString(R.string.password_required)
            return false
        }
        else if (password.length < 6) {
            findViewById<EditText>(R.id.et_password).error = getString(R.string.password_min_length)
            return false
        }

        return true
    }

    fun showPopup(context: Context, title: String, message: String, user: FirebaseUser?) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss() // Close the popup when OK button is clicked
            updateUI(user)
        }
        val dialog = builder.create()
        dialog.show()
    }
}