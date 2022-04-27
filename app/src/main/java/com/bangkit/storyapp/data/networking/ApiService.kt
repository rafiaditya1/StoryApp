package com.bangkit.storyapp.data.networking

import com.bangkit.storyapp.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    fun userRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ) : Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun userLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<UserResponse>

    @GET("stories")
    fun getAllStories(
        @Header("Authorization") header: String,
    ) : Call<StoryResponse>

    @GET("stories")
    suspend fun getAllStory(
        @Header("Authorization") header: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Int
    ) : StoryResponse

    @Multipart
    @POST("stories")
    fun storyUpload(
        @Header("Authorization") header: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<UploadResponse>

    @GET("stories?location=1")
    suspend fun getStoryLocation(
        @Header("Authorization") token: String
    ): StoryResponse

}