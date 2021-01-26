package com.example.listadecontatos.data

import android.content.SharedPreferences
import android.text.Editable
import com.example.listadecontatos.model.Contact
import com.example.listadecontatos.model.User
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface Service {

    /**
     * Rotas de usuário
     */

    @GET("/api/user/email/{email}/password/{password}")
    suspend fun login(@Path(value = "email") email: Editable?, @Path(value = "password") password: Editable?) : Array<User>

    @POST("/api/user")
    suspend fun signIn(@Body user: JsonObject) : User

    @PUT("/api/user")
    suspend fun editUser(@Body user: JsonObject) : Array<User>

    @HTTP(method = "DELETE", path = "/api/user", hasBody = true)
    suspend fun deleteAccount(@Body id: JsonObject) : User

    /**
     * Rotas de Contatos
     */
    @GET("/api/contact-list/person-email/{email}")
    fun getContacts(@Path(value = "email") email: String) : Call<ArrayList<Contact>>

    @POST("/api/contact-list")
    fun addContact(@Body Contact: JsonObject) : Call<Contact>

    @PUT("/api/contact-list")
    fun editContact(@Body Contact: JsonObject) : Call<ArrayList<Contact>>

    @HTTP(method = "DELETE", path = "/api/contact-list", hasBody = true)
    fun deleteContact(@Body id: JsonObject) : Call<User>

}

val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl("https://inovacao-teste.herokuapp.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val service: Service = retrofit.create(Service::class.java)
