package com.example.listadecontatos.viewModel

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.example.listadecontatos.data.service
import com.example.listadecontatos.model.User
import com.example.listadecontatos.view.MainActivity
import kotlinx.coroutines.launch

class LoginViewModel(): ViewModel() {

    val LOGIN_PREFERENCES : String = "DADOS_LOGIN"
    private val _email = MutableLiveData<String>()
    private val _password = MutableLiveData<String>()

    val email : LiveData<String> get() = _email
    val password : LiveData<String> get() = _password

    init {
        _email.value = ""
        _password.value = ""
    }

    fun login(email: Editable?, password: Editable?, context: Context, pref : SharedPreferences){
        var user : Array<User>? = null
        viewModelScope.launch {
            try {
                user = service.login(email, password)

                if(!user.isNullOrEmpty()){
                    val editor = pref.edit()
                    editor.putString("EMAIL", user!![0].email)
                    editor.putString("PASSWORD", user!![0].password)
                    editor.putString("ID", user!![0].id)
                    editor.apply()
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                }else{
                    Toast.makeText(context, "Credenciais inválidas. verifique o email e senha e tente novamente", Toast.LENGTH_LONG)
                }
            }catch (e: Exception){
                Log.d("Service error", e.toString())
                Toast.makeText(context, "Erro no servidor. tente novamente mais tarde", Toast.LENGTH_LONG)
            }
        }
    }

}