package com.example.listadecontatos.view

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.listadecontatos.R
import com.example.listadecontatos.data.service
import com.example.listadecontatos.model.Contact
import com.example.listadecontatos.viewModel.ContactsViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContactsFragment : Fragment() {

    private lateinit var contactsViewModel: ContactsViewModel

    private lateinit var contacts : MutableList<Contact>
    private lateinit var pref : SharedPreferences
    private lateinit var recyclerContacts: RecyclerView

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        pref = requireActivity().getSharedPreferences("LoginSharedPreferences", Context.MODE_PRIVATE)
        contactsViewModel =
                ViewModelProvider(this).get(ContactsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_contacts, container, false)

        val floatingAdd : FloatingActionButton = root.findViewById(R.id.addContact)
        val customDialog = LayoutInflater.from(context).inflate(R.layout.dialog_contacts, null)
        val customBuilder = AlertDialog.Builder(context)

        val cancelButtonDialog : Button = customDialog.findViewById(R.id.cancel_contact)
        val saveButtonDialog : Button = customDialog.findViewById(R.id.save_contact)
        val inputNameDialog : EditText = customDialog.findViewById(R.id.contacts_name)
        val inputEmailDialog : EditText = customDialog.findViewById(R.id.contacts_email)
        val inputTelDialog : EditText = customDialog.findViewById(R.id.contacts_tel)

        floatingAdd.setOnClickListener {
            customBuilder
                    .setView(customDialog)
                    .setTitle("Adicionar Contato")

            inputNameDialog.setText("")
            inputEmailDialog.setText("")
            inputTelDialog.setText("")

            val alertCustomDialogAdd = customBuilder.show()
            cancelButtonDialog.setOnClickListener {
                alertCustomDialogAdd.dismiss()
            }
            saveButtonDialog.setOnClickListener {
                var jsonAddContact = JsonObject()
                jsonAddContact.addProperty("user_email", pref.getString("EMAIL", null))
                jsonAddContact.addProperty("person_email", inputEmailDialog.text.toString())
                jsonAddContact.addProperty("cellphone", inputTelDialog.text.toString())
                jsonAddContact.addProperty("name", inputNameDialog.text.toString())

                var call = service.addContact((jsonAddContact))
                call.enqueue(object : Callback<Contact>{
                    override fun onResponse(call: Call<Contact>, response: Response<Contact>) {
                        response.body()?.let {
                            if (response.body() != null) {
                                contacts.add(response.body()!!)
                                updateRecyclerView(contacts as ArrayList<Contact>)
                            }else{
                                Toast.makeText(activity, "Erro ao tentar cadastrar", Toast.LENGTH_SHORT).show()
                            }
                            alertCustomDialogAdd.dismiss()
                        }
                    }

                    override fun onFailure(call: Call<Contact>, t: Throwable) {
                        Log.d("Service ERROR", t.message)
                    }
                })
            }
        }

        return root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contacts = ArrayList<Contact>()
        recyclerContacts = view.findViewById(R.id.recycler_contacts)

        var userEmail: String = pref.getString("EMAIL", null)!!
        var call = service.getContacts(userEmail)
        call.enqueue(object : Callback<ArrayList<Contact>>{
            override fun onResponse(call: Call<ArrayList<Contact>>, response: Response<ArrayList<Contact>?>?) {
                response?.body()?.let {
                    Log.d("OK Recycler", response.body().toString())
                    contacts = it
                    updateRecyclerView(contacts as ArrayList<Contact>);
                }
            }

            override fun onFailure(call: Call<ArrayList<Contact>>, t: Throwable) {
                Log.d("ERROR", t.message)
            }
        })

    }

    fun updateRecyclerView(contacts : ArrayList<Contact>){
        recyclerContacts.layoutManager = LinearLayoutManager(activity)
        recyclerContacts.adapter = RecyclerAdapter(contacts, requireContext(), pref)
        recyclerContacts.isAnimating
    }
}
