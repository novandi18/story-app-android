package com.novandi.dicodingstory.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @GET("/stories")
    fun getStories() : Call<StoryResponse>

    @Multipart
    @POST("/stories")
    fun createStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ) : Call<StoryResponse>

    @GET("/stories/{id}")
    fun getStory(@Path("id") id: String) : Call<StoryResponse>

    @POST("register")
    fun register(
        @Body registerRequest: RegisterRequest
    ) : Call<StoryResponse>

    @POST("login")
    fun login(
        @Body loginRequest: LoginRequest,
    ) : Call<StoryResponse>
}

data class StoryResponse(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("listStory")
    val listStory: List<StoryItems>,

    @field:SerializedName("story")
    val story: List<StoryDetail>,

    @field:SerializedName("loginResult")
    val loginResult: LoginResult
)

@Parcelize
data class StoryItems(
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("description")
    val description: String,

    @field:SerializedName("photoUrl")
    val photoUrl: String,

    @field:SerializedName("createdAt")
    val createdAt: String,
) : Parcelable

data class LoginResult(
    @field:SerializedName("userId")
    val userId: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("token")
    val token: String
)

@Parcelize
data class StoryDetail(
    @field:SerializedName("id")
    val id: String
) : Parcelable

data class LoginRequest(
    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("password")
    val password: String
)

data class RegisterRequest(
    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("password")
    val password: String
)