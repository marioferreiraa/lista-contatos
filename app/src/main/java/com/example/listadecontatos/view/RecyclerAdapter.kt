package com.example.listadecontatos.view

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.listadecontatos.R
import com.example.listadecontatos.data.service
import com.example.listadecontatos.model.Contact
import com.example.listadecontatos.model.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecyclerAdapter (private var contacts : MutableList<Contact>, private var context: Context, private var pref : SharedPreferences) :
RecyclerView.Adapter<RecyclerAdapter.ViewHolder>(){

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val nome: TextView = itemView.findViewById(R.id.value_name_ctt)
        val tel: TextView = itemView.findViewById(R.id.value_tel_ctt)
        val email: TextView = itemView.findViewById(R.id.value_email_ctt)
        val btnDelete : ImageButton = itemView.findViewById(R.id.button_delete)
        val btnEdit : ImageButton = itemView.findViewById(R.id.button_edit)


        init {

            val customDialog = LayoutInflater.from(context).inflate(R.layout.dialog_contacts, null)
            val customBuilder = AlertDialog.Builder(context)

            val cancelButtonDialog : Button = customDialog.findViewById(R.id.cancel_contact)
            val saveButtonDialog : Button = customDialog.findViewById(R.id.save_contact)
            val inputNameDialog : EditText = customDialog.findViewById(R.id.contacts_name)
            val inputEmailDialog : EditText = customDialog.findViewById(R.id.contacts_email)
            val inputTelDialog : EditText = customDialog.findViewById(R.id.contacts_tel)

            btnDelete.setOnClickListener {
                var alertDeleteContact = AlertDialog.Builder(context)
                        .setTitle("Tem certeza que deseja deletar esse contatot?")
                        .setPositiveButton("Sim", DialogInterface.OnClickListener { dialogInterface, i ->

                            var jsonObject = JsonObject()
                            jsonObject.addProperty("_id", contacts.get(adapterPosition).id)

                            var call = service.deleteContact(jsonObject)
                            call.enqueue(object : Callback<User>{
                                override fun onResponse(call: Call<User>, response: Response<User>) {
                                    response.body()?.let {
                                        Toast.makeText(itemView.context, "Contato ${contacts.get(adapterPosition).name} removido com sucesso", Toast.LENGTH_SHORT).show()
                                        removeContact(adapterPosition)
                                    }
                                }

                                override fun onFailure(call: Call<User>, t: Throwable) {
                                    Toast.makeText(context, "Não foi possivel remover o usuário", Toast.LENGTH_SHORT)
                                }
                            })
                        })
                        .setNegativeButton("Não", DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.dismiss()
                        }).show()
            }

            btnEdit.setOnClickListener {

                customBuilder
                        .setView(customDialog)
                        .setTitle("Editar Contato")

                inputNameDialog.setText(contacts.get(adapterPosition).name)
                inputEmailDialog.setText(contacts.get(adapterPosition).userEmail)
                inputTelDialog.setText(contacts.get(adapterPosition).tel)

                val alertCustomDialog = customBuilder.show()
                cancelButtonDialog.setOnClickListener {
                    alertCustomDialog.dismiss()
                }
                saveButtonDialog.setOnClickListener {

                    var jsonEditContact = JsonObject()
                    jsonEditContact.addProperty("user_email", pref.getString("EMAIL", null))
                    jsonEditContact.addProperty("person_email", inputEmailDialog.text.toString())
                    jsonEditContact.addProperty("cellphone", inputTelDialog.text.toString())
                    jsonEditContact.addProperty("name", inputNameDialog.text.toString())
                    jsonEditContact.addProperty("_id", contacts.get(adapterPosition).id)

                    var call = service.editContact(jsonEditContact)
                    call.enqueue(object : Callback<ArrayList<Contact>>{
                        override fun onResponse(call: Call<ArrayList<Contact>>, response: Response<ArrayList<Contact>>) {
                            response.body()?.let {
                                Toast.makeText(itemView.context, "Contato ${contacts.get(adapterPosition).name} alterado com sucesso", Toast.LENGTH_SHORT).show()
                                contacts.get(adapterPosition).name = inputNameDialog.text.toString()
                                contacts.get(adapterPosition).personEmail = inputEmailDialog.text.toString()
                                contacts.get(adapterPosition).tel = inputTelDialog.text.toString()
                                alertCustomDialog.dismiss()
                                notifyItemChanged(adapterPosition)
                            }
                        }

                        override fun onFailure(call: Call<ArrayList<Contact>>, t: Throwable) {
                            Toast.makeText(context, "Não foi possivel editar o usuário", Toast.LENGTH_SHORT)
                        }
                    })

                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nome.text = contacts[position].name
        holder.tel.text = contacts[position].tel
        holder.email.text = contacts[position].userEmail
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    fun removeContact(position: Int){
        contacts.remove(contacts.get(position))
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }

}