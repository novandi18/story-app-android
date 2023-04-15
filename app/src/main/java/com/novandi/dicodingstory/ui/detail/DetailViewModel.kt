package com.novandi.dicodingstory.ui.detail

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

class DetailViewModel : ViewModel() {
    private val _story = MutableLiveData<StoryItems>()
    val story: LiveData<StoryItems> = _story

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackbarText = MutableLiveData<Event<String>>()
    val snackbarText: LiveData<Event<String>> = _snackbarText

    fun getStory(id: String, token: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getStory(id, "Bearer $token")
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _story.value = response.body()?.story
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
}