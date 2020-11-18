package com.example.darkside

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.view.GestureDetectorCompat
import com.example.darkside.databinding.ActivityMainBinding
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        var securityHint = false
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)

        binding.constructPasswordButton.setOnClickListener() {
            binding.passwordResultTextview.text = constructPassword(
                binding.lowercaseSwitch.isChecked,
                binding.uppercaseSwitch.isChecked,
                binding.numbersSwitch.isChecked,
                binding.specialCharactersSwitch.isChecked,
                binding.lengthEdittext.text.toString().toIntOrNull()
            )
            if (binding.passwordResultTextview.text == "" ||
                binding.passwordResultTextview.text == "Please input valid length" ||
                binding.passwordResultTextview.text == "Please select at least one option" ||
                binding.passwordResultTextview.text == "Please increase length" ||
                binding.passwordResultTextview.text == "Length cannot exceed 10,000" ||
                binding.passwordResultTextview.text == "Length must be equal to or greater than " +
                "activated options") {
                binding.copyToClipboardIcon.visibility = View.INVISIBLE

            } else {
                binding.copyToClipboardIcon.visibility = View.VISIBLE

                if (!securityHint) {
                    securityHint = true
                    val animation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
                    binding.securityTipsPrompt.visibility = View.VISIBLE
                    binding.securityTipsPrompt.startAnimation((fadeIn))
                }
            }
        }

        binding.copyToClipboardIcon.setOnClickListener() {
            var clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            var clip: ClipData = ClipData.newPlainText("password", binding.passwordResultTextview.text.toString())
            clipboard.setPrimaryClip((clip))
            Toast.makeText(applicationContext, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
        }

    }

}

val lowCase = "abcdefghijklmnopqrstuvwxyz"
val upCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
val numbers = "0123456789"
val specialChars = "£$%^&*()_-+={}[]'@#~;:,.<>/?!"

fun constructPassword(lowercase: Boolean, uppercase: Boolean, nums: Boolean, specials: Boolean, length: Int?): String {

    if (length == null || length == 0) {
        return "Please input valid length"
    }

    if (length > 10000) {
        return "Length cannot exceed 10,000"
    }

    var prototypePassword = ""
    var characterArray : MutableList<String> = ArrayList()

    if (!lowercase && !uppercase && !nums && !specials) {
        return "Please select at least one option"
    }
    if (lowercase) {
        characterArray.add(lowCase)
    }
    if (uppercase) {
        characterArray.add(upCase)
    }
    if (nums) {
        characterArray.add(numbers)
    }
    if (specials) {
        characterArray.add(specialChars)
    }

    if (characterArray.size > length) {
        return "Length must be equal to or greater than activated options"
    }

    //construct prototypePassword using combination of switch options user selected
    while (prototypePassword.length < length) {
        if (length - prototypePassword.length >= 3) {
            for (i in 0 until characterArray.size) {
                val potentialCharacters = characterArray[i].length-1
                prototypePassword += characterArray[i][(0..potentialCharacters).random()]
            }
        } else {
            for (i in 0 until length - prototypePassword.length) {
                val randomCharacterSet = characterArray.random()
                val randomCharacter = randomCharacterSet[(randomCharacterSet.indices).random()]
                prototypePassword += randomCharacter
            }
        }
    }

    var password = ""

    //shuffle characters within prototypePassword to create more randomness
    while (prototypePassword.isNotEmpty()) {
        val randomCharacter = (prototypePassword.indices).random()
        password += prototypePassword[randomCharacter]
        prototypePassword = StringBuilder(prototypePassword).deleteCharAt(randomCharacter)
            .toString()
    }

    return password
}

