package com.wellingtonhenrique.contatos.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.wellingtonhenrique.contatos.R
import com.wellingtonhenrique.contatos.data.Contact

class ContactAdapter(private var contacts: List<Contact>) :
    RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ShapeableImageView = itemView.findViewById(R.id.iv_add_person)
        val textName: TextView = itemView.findViewById(R.id.tv_name)
        val textPhone: TextView = itemView.findViewById(R.id.tv_phone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.contact_item, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        holder.textName.text = contact.name
        holder.textPhone.text = contact.phone

        Glide.with(holder.itemView.context)
            .load(contact.imageUrl)
            .placeholder(R.drawable.ic_person)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = contacts.size

    fun updateList(newList: List<Contact>) {
        contacts = newList
        notifyDataSetChanged()
    }
}