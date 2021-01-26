package com.example.listadecontatos.model

import com.google.gson.annotations.SerializedName

data class Contact(
        @SerializedName("_id")
        val id : String,
        @SerializedName("user_email")
        var userEmail : String,
        @SerializedName("cellphone")
        var tel : String,
        @SerializedName("name")
        var name : String,
        @SerializedName("person_email")
        var personEmail : String
)
