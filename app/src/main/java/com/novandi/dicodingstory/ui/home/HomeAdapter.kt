package com.novandi.dicodingstory.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.novandi.dicodingstory.api.StoryItems
import com.novandi.dicodingstory.databinding.ItemRowStoryBinding

class HomeAdapter : PagingDataAdapter<StoryItems, HomeAdapter.ViewHolder>(DIFF_CALLBACK) {
    private lateinit var onItemClickListener: OnItemClickListener

    class ViewHolder(private var binding: ItemRowStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: StoryItems, x: OnItemClickListener) {
            Glide.with(itemView.context).load(data.photoUrl).into(binding.storyImage)
            binding.storyName.text = data.name
            binding.storyDescription.text = data.description
            binding.storyCardview.setOnClickListener {
                x.onItemClicked(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) holder.bind(data, onItemClickListener)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClicked(data: StoryItems)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryItems>() {
            override fun areItemsTheSame(oldItem: StoryItems, newItem: StoryItems): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoryItems, newItem: StoryItems): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}