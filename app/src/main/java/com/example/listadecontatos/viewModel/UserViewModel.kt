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
import com.example.listadecontatos.model.User
import com.example.listadecontatos.view.LoginActivity
import com.example.listadecontatos.view.MainActivity
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import java.lang.Exception

class UserViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is user Fragment"
    }

    private val _email = MutableLiveData<String>()
    private val _password = MutableLiveData<String>()

    val email: LiveData<String> get() = _email
    val password: LiveData<String> get() = _password

    init {
        _email.value = ""
        _password.value = ""
    }

    fun changeValEmail(value : String?){
        _email.value = value
    }

    fun editUser(jsonObject: JsonObject, pref: SharedPreferences){
        var user : Array<User>

        viewModelScope.launch {
            try {
                user = service.editUser(jsonObject)
                if(!user[0].id.isNullOrEmpty()){
                    changeValEmail(user[0].email)
                    pref.edit()
                            .putString("EMAIL", user[0].email)
                            .putString("PASSWORD", user[0].password)
                            .putString("ID", user[0].id)
                            .apply()
                }
            }catch (e : Exception){
                Log.d("Service error", e.toString())
            }
        }
    }

    fun deleteAccount(id: String, pref : SharedPreferences, context: Context){
        var user : User? = null
        viewModelScope.launch {
            try {
                val jsonDelete = JsonObject()
                jsonDelete.addProperty("_id", id)
                user = service.deleteAccount(jsonDelete)
                if(user?.id == id){
                    pref.edit().clear().apply()
                    val intent = Intent(context, LoginActivity::class.java)
                    context.startActivity(intent)
                    MainActivity().finish()
                }
            }catch (e : Exception){
                Log.d("Service error", e.toString())
            }
        }
    }
}