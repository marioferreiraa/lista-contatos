package com.example.listadecontatos.view
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.listadecontatos.R
import com.example.listadecontatos.databinding.ActivityLoginBinding
import com.example.listadecontatos.model.User
import com.example.listadecontatos.viewModel.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var pref : SharedPreferences

    val viewModel : LoginViewModel by lazy {
        ViewModelProvider(this).get(LoginViewModel::class.java)
    }

    lateinit var binding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        pref = getSharedPreferences("LoginSharedPreferences", Context.MODE_PRIVATE)

        if(!pref.getString("EMAIL", null).isNullOrEmpty()){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        binding.buttonLogin.setOnClickListener {
            var user : Array<User>?
            if(!validateFields()){
                viewModel.login(binding.inputEmail.text, binding.inputPassword.text, this, pref)
            }
        }

        binding.labelLinkCadastro.setOnClickListener {
            val intentRegister = Intent(this, RegisterActivity::class.java);
            startActivity(intentRegister);
        }

    }

    private fun validateFields() : Boolean{
        var error = false;
        if(binding.inputEmail.text.isNullOrEmpty()){
            binding.textEmail.error = "Campo obrigatório";
            error = true;
        }else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text).matches()){
            binding.textEmail.error = "Formato de email inválido";
            error = true;
        }else{
            binding.textEmail.error = "";
        }

        if(binding.inputPassword.text.isNullOrEmpty()){
            binding.textPassword.error = "Campo obrigatório";
            error = true;
        }else{
            binding.textPassword.error = "";
        }

        return error;
    }
}