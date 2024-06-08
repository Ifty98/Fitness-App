package com.example.finalversion

data class Response1 (
    val message: String
)

data class Response2 (
    val exists: String,
    val message: String
)

data class Response3 (
    val userId: String
)

data class UpdatePersonalDataRequest (
    val gender: String,
    val age: String,
    val weight: String
)

data class Project (
    val id: String,
    val name: String,
    val description: String,
    val deadline: String,
    val status: String
)

data class Subtask(
    val id: String,
    val projectId: String,
    val name: String,
    val status: String
)

data class StepCounterEntry(
    val id: Int,
    val userId: String,
    val date: String,
    val steps: Int
)

data class PersonalData(
    val id: Int,
    val userId: String,
    val gender: String,
    val age: String,
    val weight: String
)
data class UserResponse(
    val username: String,
    val password: String
)

data class ActivityData(
    val id: String,
    val user_id: String,
    val name: String,
    val total_time: String,
    val calories_burned: String
)




