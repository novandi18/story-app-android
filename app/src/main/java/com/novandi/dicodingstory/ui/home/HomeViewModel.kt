package com.novandi.dicodingstory.ui.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.novandi.dicodingstory.api.StoryItems
import com.novandi.dicodingstory.data.StoryRepository
import com.novandi.dicodingstory.di.Injection

class HomeViewModel(storyRepository: StoryRepository) : ViewModel() {
    val stories: LiveData<PagingData<StoryItems>> = storyRepository.getStories().cachedIn(viewModelScope)
}

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(Injection.provideRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}