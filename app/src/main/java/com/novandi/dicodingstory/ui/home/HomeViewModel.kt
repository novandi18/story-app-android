package com.novandi.dicodingstory.ui.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.novandi.dicodingstory.api.ApiConfig
import com.novandi.dicodingstory.api.StoryItems
import com.novandi.dicodingstory.api.StoryResponse
import com.novandi.dicodingstory.data.StoryRepository
import com.novandi.dicodingstory.di.Injection
import com.novandi.dicodingstory.helper.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(storyRepository: StoryRepository) : ViewModel() {
    private val _stories = MutableLiveData<List<StoryItems>>()
    val stories: LiveData<PagingData<StoryItems>> = storyRepository.getStories().cachedIn(viewModelScope)

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackbarText = MutableLiveData<Event<String>>()
    val snackbarText: LiveData<Event<String>> = _snackbarText

    fun getStories(token: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getStories("Bearer $token", size = STORY_SIZE)
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _stories.value = response.body()?.listStory
                    _snackbarText.value = Event(if (response.body()?.error == true) response.body()?.message.toString() else "")
                } else {
                    val errorResponse = Gson().fromJson(response.errorBody()?.string(), StoryResponse::class.java)
                    _snackbarText.value = Event(errorResponse.message)
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                _isLoading.value = false
                _snackbarText.value = Event(t.message.toString())
            }

        })
    }

    companion object {
        const val STORY_SIZE = 5
    }
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