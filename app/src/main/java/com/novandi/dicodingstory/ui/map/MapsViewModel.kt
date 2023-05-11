package com.novandi.dicodingstory.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.novandi.dicodingstory.api.ApiConfig
import com.novandi.dicodingstory.api.StoryItems
import com.novandi.dicodingstory.api.StoryResponse
import com.novandi.dicodingstory.helper.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel : ViewModel() {
    private val _stories = MutableLiveData<List<StoryItems>>()
    val stories: LiveData<List<StoryItems>> = _stories

    private val _message = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>> = _message

    fun getStoriesToMaps(token: String) {
        val client = ApiConfig.getApiService().getStories("Bearer $token", location = 1)
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                if (response.isSuccessful) {
                    _stories.value = response.body()?.listStory
                } else {
                    val errorResponse = Gson().fromJson(response.errorBody()?.string(), StoryResponse::class.java)
                    _message.value = Event(errorResponse.message)
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                _message.value = Event(t.message.toString())
            }

        })
    }
}