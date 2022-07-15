package com.dicoding.storyapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.model.ListStory
import com.dicoding.storyapp.databinding.ActivityDetailStoryBinding
import com.dicoding.storyapp.helper.formatDate
import java.util.*

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.apply {
            title = getString(R.string.detail_story)
            setDisplayHomeAsUpEnabled(true)
        }

        val story = intent.getParcelableExtra<ListStory>(EXTRA_STORY) as ListStory

        Glide.with(this)
            .load(story.photoUrl)
            .placeholder(R.drawable.ic_baseline_image_24)
            .into(binding.userStory)
        binding.name.text = story.name
        binding.description.text = story.description
        binding.postedOn.text = formatDate(story.createdAt, TimeZone.getDefault().id)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    companion object {
        const val EXTRA_STORY = "detailStory"
    }
}