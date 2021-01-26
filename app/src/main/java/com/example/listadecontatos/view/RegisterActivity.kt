package com.example.listadecontatos.view

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.listadecontatos.R
import com.example.listadecontatos.databinding.ActivityRegisterBinding
import com.example.listadecontatos.viewModel.RegisterViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var pref : SharedPreferences

    val viewModel : RegisterViewModel by lazy {
        ViewModelProvider(this).get(RegisterViewModel::class.java)
    }

    lateinit var binding : ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pref = getSharedPreferences("LoginSharedPreferences", Context.MODE_PRIVATE)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        binding.buttonRegister.setOnClickListener {
            if(!validateFields()){
                viewModel.signIn(binding.inputEmail.text.toString(), binding.inputPassword.text.toString(), this, pref)
            }
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