package com.novandi.dicodingstory.ui.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.novandi.dicodingstory.api.ApiConfig
import com.novandi.dicodingstory.api.StoryResponse
import com.novandi.dicodingstory.helper.Event
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryViewModel : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackbarText = MutableLiveData<Event<String>>()
    val snackbarText: LiveData<Event<String>> = _snackbarText

    fun uploadStory(image: MultipartBody.Part, description: RequestBody, token: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().createStory(image, description, "Bearer $token")
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
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