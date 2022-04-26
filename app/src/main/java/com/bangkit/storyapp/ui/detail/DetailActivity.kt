package com.bangkit.storyapp.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bangkit.storyapp.data.model.ListStoryItem
import com.bangkit.storyapp.databinding.ActivityDetailBinding
import com.bumptech.glide.Glide

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val detailStory = intent.getParcelableExtra<ListStoryItem>(STORY) as ListStoryItem
        Glide.with(this)
            .load(detailStory.photoUrl)
            .into(binding.ivPhoto)
        binding.tvName.text = detailStory.name
        binding.tvDescription.text = detailStory.description

    }

    companion object {
        const val STORY = "story"
    }
}