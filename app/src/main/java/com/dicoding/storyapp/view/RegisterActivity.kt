package com.dicoding.storyapp.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.model.UserPreference
import com.dicoding.storyapp.databinding.ActivityRegisterBinding
import com.dicoding.storyapp.helper.isEmailValid
import com.dicoding.storyapp.viewmodel.RegisterViewModel
import com.dicoding.storyapp.viewmodel.ViewModelFactory

class RegisterActivity : AppCompatActivity() {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setupViewModel()
        register()
        editTextListener()
        buttonEnable()
        progressBar()
        playAnimation()

        binding.login.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            finish()
        }
    }

    private fun setupViewModel() {
        registerViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore),this)
        )[RegisterViewModel::class.java]
    }

    private fun register() {
        binding.btnRegister.setOnClickListener {
            val name = binding.name.text.toString().trim()
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()

            registerViewModel.register(name, email, password)
            registerViewModel.registerResponse.observe(this@RegisterActivity) { registerResponse ->
                if (registerResponse != null){
                    if (!registerResponse.error) {
                        progressBar()
                        Toast.makeText(this@RegisterActivity, getString(R.string.registration_success), Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    } else {
                        progressBar()
                        binding.btnRegister.visibility = View.VISIBLE
                        Toast.makeText(this@RegisterActivity, getString(R.string.registration_failed), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun editTextListener() {
        binding.email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                buttonEnable()
            }
        })
        binding.password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                buttonEnable()
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
    }

    private fun buttonEnable() {
        binding.btnRegister.isEnabled = binding.email.text.toString().isNotEmpty() && binding.password.text.toString().isNotEmpty() && binding.password.text.toString().length >= 6 && isEmailValid(binding.email.text.toString())
    }

    private fun progressBar() {
        registerViewModel.isLoading.observe(this) {
            binding.apply {
                if (it) {
                    progressBar.visibility = View.VISIBLE
                    btnRegister.visibility = View.INVISIBLE
                }
                else progressBar.visibility = View.GONE
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imgHeader, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val create = ObjectAnimator.ofFloat(binding.createAcc, View.ALPHA, 1f).setDuration(500)
        val greeting = ObjectAnimator.ofFloat(binding.greetings, View.ALPHA, 1f).setDuration(500)
        val nameTextView = ObjectAnimator.ofFloat(binding.tvName, View.ALPHA, 1f).setDuration(500)
        val nameEditTextLayout = ObjectAnimator.ofFloat(binding.name, View.ALPHA, 1f).setDuration(500)
        val emailTextView = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(500)
        val emailEditTextLayout = ObjectAnimator.ofFloat(binding.email, View.ALPHA, 1f).setDuration(500)
        val passwordTextView = ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(500)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(binding.password, View.ALPHA, 1f).setDuration(500)
        val register = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(500)
        val account = ObjectAnimator.ofFloat(binding.account, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.login, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(create, greeting, nameTextView, nameEditTextLayout, emailTextView, emailEditTextLayout, passwordTextView, passwordEditTextLayout, register, account, login)
            startDelay = 500
        }.start()
    }
}