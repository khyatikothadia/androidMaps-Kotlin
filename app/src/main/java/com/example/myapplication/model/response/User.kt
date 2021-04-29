package com.example.myapplication.model.response

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("authentication_token")
    val authenticationToken: String,
    @SerializedName("person")
    val person: Person,
    @SerializedName("message")
    val message: String,
    @SerializedName("error_code")
    val errorCode: Int
)

data class Person(
    @SerializedName("display_name")
    val displayName: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("role")
    val role: Role
)

data class Role(
    @SerializedName("key")
    val key: String,
    @SerializedName("rank")
    val rank: Int
)