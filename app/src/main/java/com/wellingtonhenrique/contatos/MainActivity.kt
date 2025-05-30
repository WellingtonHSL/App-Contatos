package com.wellingtonhenrique.contatos

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.wellingtonhenrique.contatos.adapter.ContactAdapter
import com.wellingtonhenrique.contatos.data.AppDatabase
import com.wellingtonhenrique.contatos.data.ContactDao
import com.wellingtonhenrique.contatos.ui.AddContactActivity

class MainActivity : AppCompatActivity() {

    private lateinit var contactAdapter: ContactAdapter
    private lateinit var contactDao: ContactDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        contactDao = AppDatabase.getDatabase(this).contactDao()
        contactAdapter = ContactAdapter(emptyList())

        val recyclerView = findViewById<RecyclerView>(R.id.rv_contacts)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = contactAdapter

        contactDao.getAllContacts().observe(this) { contacts ->
            contactAdapter.updateList(contacts)
        }

        findViewById<FloatingActionButton>(R.id.fab_add_contact).setOnClickListener {
            startActivity(Intent(this, AddContactActivity::class.java))
        }
    }
}