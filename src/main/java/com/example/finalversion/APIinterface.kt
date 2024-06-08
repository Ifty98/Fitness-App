package com.example.finalversion

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.*

interface APIinterface {

    @GET("checkUser")
    fun checkUser(
        @Query("username") username: String,
        @Query("password") password: String
    ): Call<Response1>

    @GET("checkUsername")
    fun checkUsername(
        @Query("username") username: String,
        @Query("password") password: String
    ): Call<Response2>

    @POST("createUser")
    @FormUrlEncoded
    fun createUser(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<Response1>

    @GET("getID")
    fun getID(
        @Query("username") username: String,
        @Query("password") password: String
    ): Call<Response3>

    @PUT("updatePersonalData/{userId}")
    fun updatePersonalData(
        @Path("userId") userId: String,
        @Body requestData: UpdatePersonalDataRequest
    ): Call<Response1>

    @POST("step-counter")
    fun createStepCounterEntry(
        @Query("user_id") userId: String,
        @Query("date") date: String,
        @Query("steps") steps: Int
    ): Call<Response1>


    @GET("projects/{userId}")
    fun getProjects(
        @Path("userId") userId: String
    ): Call<List<Project>>

    @POST("createProject")
    @FormUrlEncoded
    fun createProject(
        @Field("user_id") userId: String,
        @Field("name") name: String,
        @Field("description") description: String,
        @Field("deadline") deadline: String,
        @Field("status") status: String
    ): Call<Response1>

    @PUT("updateProject/{projectId}")
    fun updateProject(
        @Path("projectId") projectId: String,
        @Query("status") status: String
    ): Call<Response1>

    @POST("createSubtask")
    fun createSubtask(
        @Query("project_id") projectId: String,
        @Query("name") name: String,
        @Query("status") status: String
    ): Call<Response1>

    @GET("subtasks/{projectId}")
    fun getSubtasks(
        @Path("projectId") projectId: String
    ): Call<List<Subtask>>

    @PUT("updateSubtask/{subtaskId}")
    fun updateSubtask(
        @Path("subtaskId") subtaskId: String,
        @Query("status") status: String
    ): Call<Response1>

    @DELETE("subtasks/{subtaskId}")
    fun deleteSubtask(
        @Path("subtaskId") subtaskId: String
    ): Call<Void>

    @DELETE("projects/{projectId}")
    fun deleteProjectAndSubtasks(
        @Path("projectId") projectId: String
    ): Call<Void>

    @GET("step-counter/{userId}")
    fun getStepCounterEntries(
        @Path("userId") userId: String
    ): Call<List<StepCounterEntry>>

    @GET("/personal-data/{userId}")
    fun getPersonalData(
        @Path("userId") userId: String
    ): Call<List<PersonalData>>

    @GET("user/{id}")
    fun getUser(
        @Path("id") id: String
    ): Call<UserResponse>

    @PUT("user/{id}")
    fun updateUserPassword(
        @Path("id") userId: String,
        @Query("password") password: String
    ): Call<Response1>

    @PUT("/updateAge/{id}")
    fun updateAge(
        @Path("id") userId: String,
        @Query("age") newAge: String
    ): Call<Response1>

    @PUT("/updateWeight/{id}")
    fun updateWeight(
        @Path("id") userId: String,
        @Query("weight") newWeight: String
    ): Call<Response1>

    @POST("activities")
    fun createActivity(
        @Query("user_id") userId: String,
        @Query("name") name: String,
        @Query("total_time") totalTime: String,
        @Query("calories_burned") caloriesBurned: String
    ): Call<Response1>

    @GET("/activities/{user_id}")
    fun getActivityEntries(
        @Path("user_id") userId: String
    ): Call<List<ActivityData>>

}