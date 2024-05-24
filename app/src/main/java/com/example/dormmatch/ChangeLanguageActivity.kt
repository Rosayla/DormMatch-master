package com.example.dormmatch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dormmatch.databinding.ActivityLoginBinding
import com.example.dormmatch.databinding.ChangeLanguageBinding
import com.example.dormmatch.fragments.Settings
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class ChangeLanguageActivity : AppCompatActivity() {

    private val menuActivityRequestCode = 1
    private lateinit var binding: ChangeLanguageBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChangeLanguageBinding.inflate(layoutInflater)

        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        FirebaseApp.initializeApp(this)

        checkRadioButton()
    }

    private fun checkRadioButton() {

        val radioButtonPortuguese = findViewById<RadioButton>(R.id.rb_portuguese)
        val radioButtonEnglish = findViewById<RadioButton>(R.id.rb_english)

        val currentLanguage = Locale.getDefault().language

        if (currentLanguage == "pt") {
            radioButtonPortuguese.isChecked = true
        } else if (currentLanguage == "en") {
            radioButtonEnglish.isChecked = true
        }
    }

    fun btnReturn(view: View) {
        super.onBackPressed()
        finish()

    }

    fun saveChanges(view: View) {

        val radioGroup = findViewById<RadioGroup>(R.id.rg_languages)
        val selectedRadioButtonId = radioGroup.checkedRadioButtonId

        if (selectedRadioButtonId != -1) {
            val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
            val selectedValue = selectedRadioButton.text.toString()

            if(selectedValue == "Portuguese") {
                setAppLanguage(this, "pt")

            }
            else if (selectedValue == "Inglês") {
                setAppLanguage(this, "en")

            }

        }
    }

    fun setAppLanguage(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = context.resources
        val configuration = resources.configuration
        configuration.setLocale(locale)

        // Update app resources with the new configuration
        resources.updateConfiguration(configuration, resources.displayMetrics)

        // Restart the activity to apply the new language
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }


}