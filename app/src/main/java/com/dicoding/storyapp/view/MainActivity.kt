package com.dicoding.storyapp.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.model.UserData
import com.dicoding.storyapp.data.model.UserPreference
import com.dicoding.storyapp.databinding.ActivityMainBinding
import com.dicoding.storyapp.viewmodel.MainViewModel
import com.dicoding.storyapp.viewmodel.ViewModelFactory

class MainActivity : AppCompatActivity() {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private lateinit var userData: UserData
    private lateinit var binding: ActivityMainBinding

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        buttonOnClick()
        playAnimation()
        setupViewModel()

        binding.setting.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
    }

    private fun setupViewModel() {
        val pref = UserPreference.getInstance(dataStore)

        mainViewModel = ViewModelProvider(this, ViewModelFactory(pref, this))[MainViewModel::class.java]

        mainViewModel.getUser().observe(this) { user ->
            this.userData = user
            if (!user.isLogin) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                this.userData = UserData(
                    user.userId,
                    user.name,
                    user.token,
                    true
                )
                binding.greeting.text = getString(R.string.greeting, user.name)
            }
        }
    }

    private fun buttonOnClick() {
        binding.btnNext.setOnClickListener {
            val intent = Intent(this@MainActivity, StoryActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setMessage(getString(R.string.ask_logout))
                setPositiveButton(getString(R.string.logout)) { _, _ ->
                    mainViewModel.logout()
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()
                }
                setNegativeButton(getString(R.string.cancel)) { _, _ ->
                }
                create()
                show()
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imgHeader, View.TRANSLATION_X, -38f, 38f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val name = ObjectAnimator.ofFloat(binding.greeting, View.ALPHA, 1f).setDuration(500)
        val about = ObjectAnimator.ofFloat(binding.aboutApp, View.ALPHA, 1f).setDuration(500)
        val next = ObjectAnimator.ofFloat(binding.btnNext, View.ALPHA, 1f).setDuration(500)
        val logout = ObjectAnimator.ofFloat(binding.btnLogout, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(next, logout)
        }

        AnimatorSet().apply {
            playSequentially(name, about, together)
            start()
        }
    }
}