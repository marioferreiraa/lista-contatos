package com.example.listadecontatos.viewModel

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listadecontatos.data.service
import com.example.listadecontatos.view.MainActivity
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import java.lang.Exception

class RegisterViewModel(): ViewModel() {

    private val _email = MutableLiveData<String>()
    private val _password = MutableLiveData<String>()

    val email : LiveData<String> get() = _email
    val password : LiveData<String> get() = _password

    init {
        _email.value = ""
        _password.value = ""
    }

    fun signIn(email: String, password: String, context: Context, pref : SharedPreferences){
        val jsonObjectRegister = JsonObject()
        jsonObjectRegister.addProperty("email", email)
        jsonObjectRegister.addProperty("password", password)

        viewModelScope.launch {
            try {
                val user = service.signIn(jsonObjectRegister)

                if(!user.password.isNullOrEmpty()){
                    Log.d("Register Success", user.password)
                    val editor = pref.edit()
                    editor.putString("EMAIL", user.email)
                    editor.putString("PASSWORD", user.password)
                    editor.putString("ID", user.id)
                    editor.apply()
                    val intent: Intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                }

            }catch (e: Exception){
                Log.d("Service Error", e.toString())
            }
        }

    }


}