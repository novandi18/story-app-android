package com.novandi.dicodingstory.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.novandi.dicodingstory.api.StoryItems
import com.novandi.dicodingstory.databinding.ItemRowStoryBinding

class HomeAdapter(private val stories: List<StoryItems>) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {
    private lateinit var onItemClickListener: OnItemClickListener

    class ViewHolder(var binding: ItemRowStoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = stories.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (_, name, description, photoUrl) = stories[position]
        Glide.with(holder.itemView.context).load(photoUrl).into(holder.binding.storyImage)
        holder.binding.storyName.text = name
        holder.binding.storyDescription.text = description
        holder.binding.storyCardview.setOnClickListener {
            onItemClickListener.onItemClicked(stories[holder.adapterPosition])
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClicked(data: StoryItems)
    }
}