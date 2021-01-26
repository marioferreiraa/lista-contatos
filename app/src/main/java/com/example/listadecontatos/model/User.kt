package com.example.listadecontatos.model

import com.google.gson.annotations.SerializedName


data class User (
        @SerializedName("_id")
        val id : String,
        @SerializedName("email")
        var email : String,
        @SerializedName("password")
        var password : String
)
