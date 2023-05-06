package com.example.authenticationapp1

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.authenticationapp1.databinding.ActivityResetPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.rxbinding2.widget.RxTextView


@SuppressLint("CheckResult")
class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding:ActivityResetPasswordBinding
    private lateinit var auth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

//  auth
        auth = FirebaseAuth.getInstance()

//  email validation
        val emailStream = RxTextView.textChanges(binding.etMail)
            .skipInitialValue()
            .map { email ->
                !Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }
        emailStream.subscribe{
            showEmailValidationAlert(it)
        }

//  Reset password
        binding.btnResetPw.setOnClickListener{
            val email = binding.etMail.text.toString().trim()

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this){reset ->
                    if (reset.isSuccessful){
                        Intent(this, LoginActivity::class.java).also {
                            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(it)
                            Toast.makeText(this, "Check email to rearrange the password", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(this, reset.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
        }

//  click

        binding.tvBackLogin.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }

    }

    private fun showEmailValidationAlert(isNotValid: Boolean){
        if(isNotValid){
            binding.etMail.error = "Invalid Email!"
            binding.btnResetPw.isEnabled = false
            binding.btnResetPw.backgroundTintList = ContextCompat.getColorStateList(this,android.R.color.darker_gray)
        }else{
            binding.etMail.error = null
            binding.btnResetPw.isEnabled = true
            binding.btnResetPw.backgroundTintList = ContextCompat.getColorStateList(this,R.color.primary_color)
        }
    }
}