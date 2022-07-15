package com.dicoding.storyapp.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.R
import com.dicoding.storyapp.adapter.ListStoryAdapter
import com.dicoding.storyapp.adapter.LoadingStateAdapter
import com.dicoding.storyapp.data.model.UserPreference
import com.dicoding.storyapp.databinding.ActivityStoryBinding
import com.dicoding.storyapp.viewmodel.StoryViewModel
import com.dicoding.storyapp.viewmodel.ViewModelFactory

class StoryActivity : AppCompatActivity() {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private lateinit var storyViewModel: StoryViewModel
    private lateinit var binding: ActivityStoryBinding
    private lateinit var storyAdapter: ListStoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.apply {
            title = getString(R.string.user_story)
        }

        setRecyclerView()
        uploadStory()
        openMaps()

        binding.refreshListStory.setOnRefreshListener {
            refreshStory()
        }
        refreshStory()
    }

    private fun refreshStory() {
        val pref = UserPreference.getInstance(dataStore)
        binding.refreshListStory.isRefreshing = true

        storyViewModel = ViewModelProvider(this, ViewModelFactory(pref, this))[StoryViewModel::class.java]
        storyViewModel.getUser().observe(this) { user ->
            storyViewModel.getAllStories(user.token).observe(this) {
                storyAdapter.submitData(lifecycle, it)
            }
        }
        binding.refreshListStory.isRefreshing = false
    }

    private fun setRecyclerView() {
        storyAdapter = ListStoryAdapter()
        binding.rvStory.layoutManager = LinearLayoutManager(this@StoryActivity)
        binding.rvStory.adapter = storyAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                storyAdapter.retry()
            })
    }

    private fun uploadStory(){
        binding.uploadStory.setOnClickListener {
            val intent = Intent(this, UploadStoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun openMaps(){
        binding.openMaps.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
    }
}