package com.novandi.dicodingstory.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("/v1/stories")
    fun getStories(
        @Header("Authorization") header: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null
    ) : Call<StoryResponse>

    @Multipart
    @POST("/v1/stories")
    fun createStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Header("Authorization") header: String
    ) : Call<StoryResponse>

    @GET("/v1/stories/{id}")
    fun getStory(@Path("id") id: String, @Header("Authorization") header: String) : Call<StoryResponse>

    @POST("/v1/register")
    fun register(
        @Body registerRequest: RegisterRequest
    ) : Call<StoryResponse>

    @POST("/v1/login")
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
    val story: StoryItems,

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