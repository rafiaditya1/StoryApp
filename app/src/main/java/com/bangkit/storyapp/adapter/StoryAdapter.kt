package com.bangkit.storyapp.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.storyapp.data.model.ListStoryItem
import com.bangkit.storyapp.data.model.StoryResponse
import com.bangkit.storyapp.databinding.ItemRowStoryBinding
import com.bangkit.storyapp.ui.detail.DetailActivity
import com.bumptech.glide.Glide

//(private val listStory: List<ListStoryItem>)
class StoryAdapter : PagingDataAdapter<ListStoryItem, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    class ViewHolder (var binding: ItemRowStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem?) {
            with(binding) {
                Glide.with(binding.ivPhoto)
                    .load(story?.photoUrl)
                    .into(binding.ivPhoto)
                tvName.text = story?.name
                itemView.setOnClickListener {
                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as Activity,
                            Pair(binding.ivPhoto, "photo"),
                            Pair(binding.tvName, "name"),
                        )
                    val intent = Intent(itemView.context, DetailActivity::class.java)
                    intent.putExtra(DetailActivity.STORY, story)
                    itemView.context.startActivity(intent, optionsCompat.toBundle())
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.binding.tvName.text = listStory[position].name
//        Glide.with(holder.itemView.context)
//            .load(listStory[position].photoUrl)
//            .into(holder.binding.ivPhoto)
//
//
//        holder.itemView.setOnClickListener {
//            val optionsCompat: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                holder.itemView.context as Activity,
//                Pair(holder.binding.ivPhoto, "photo"),
//                Pair(holder.binding.tvName, "name")
//            )
//
//            val intent = Intent(holder.itemView.context, DetailActivity::class.java)
//            intent.putExtra(DetailActivity.STORY, listStory[position])
//            holder.itemView.context.startActivity(intent, optionsCompat.toBundle())
//        }
        holder.bind(getItem(position))
    }

//    override fun getItemCount(): Int {
//        return listStory.size
//    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}