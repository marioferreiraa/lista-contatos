package com.example.listadecontatos.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.listadecontatos.R
import com.example.listadecontatos.databinding.FragmentUserBinding
import com.example.listadecontatos.viewModel.UserViewModel
import com.google.gson.JsonObject

class UserFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel
    lateinit var binding : FragmentUserBinding
    private lateinit var pref : SharedPreferences

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        userViewModel =
                ViewModelProvider(this).get(UserViewModel::class.java)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user, container, false)
        val root = binding.root
        binding.viewmodel = userViewModel
        binding.lifecycleOwner = this

        pref = requireActivity().getSharedPreferences("LoginSharedPreferences", Context.MODE_PRIVATE)
        val userEmail : String? = pref.getString("EMAIL", null)
        binding.valueEmail.text = userEmail
        userViewModel.changeValEmail(userEmail)


        binding.buttonAlterarDados.setOnClickListener {
            if(binding.buttonAlterarDados.text != "Salvar"){
                binding.textEmail.visibility = View.VISIBLE
                binding.textPassword.visibility = View.VISIBLE
                binding.valueEmail.visibility = View.GONE
                userViewModel.changeValEmail(userEmail)
                binding.buttonAlterarDados.text = "Salvar"
            }else{
                val jsonEdit = JsonObject()
                jsonEdit.addProperty("_id", pref.getString("ID", null))
                jsonEdit.addProperty("email", binding.inputEmail.text.toString())
                if(!binding.inputPassword.text.toString().isNullOrEmpty()){
                    jsonEdit.addProperty("password", binding.inputPassword.text.toString())
                }
                userViewModel.editUser(jsonEdit, pref)
                binding.buttonAlterarDados.text = getString(R.string.LABEL_ALTERAR_DADOS)
                binding.textEmail.visibility = View.GONE
                binding.textPassword.visibility = View.GONE
                binding.valueEmail.visibility = View.VISIBLE
            }
        }

        binding.labelLinkLogout.setOnClickListener {
            val alertLogOut = AlertDialog.Builder(activity)
            alertLogOut.setTitle("Sair da conta")
            alertLogOut.setPositiveButton("Sim", DialogInterface.OnClickListener { dialogInterface, i ->
                pref.edit().clear().apply()
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
                activity?.finish()
            })
            alertLogOut.setNegativeButton("Não", DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.dismiss()
            })
            alertLogOut.show()
        }

        binding.labelLinkDesativar.setOnClickListener {
            val alertDeleteAccount = AlertDialog.Builder(activity)
                    .setTitle("Deseja desativar definitivamente a sua conta?")
                    .setPositiveButton("Sim", DialogInterface.OnClickListener { dialogInterface, i ->
                        val id : String? = pref.getString("ID", null)
                        if(!id.isNullOrEmpty()){
                            userViewModel.deleteAccount(id, pref, this.requireContext())
                        }
                    })
                    .setNegativeButton("Não", DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.dismiss()
                    }).show()
        }

        userViewModel.email.observe(viewLifecycleOwner, Observer {
            binding.valueEmail.text = it
        })

        setHasOptionsMenu(true)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

}