package com.wellingtonhenrique.contatos

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.wellingtonhenrique.contatos.adapter.ContactAdapter
import com.wellingtonhenrique.contatos.data.AppDatabase
import com.wellingtonhenrique.contatos.data.ContactDao
import com.wellingtonhenrique.contatos.ui.AddContactActivity
import com.wellingtonhenrique.contatos.ui.DetailContactActivity

class MainActivity : AppCompatActivity() {

    private lateinit var contactAdapter: ContactAdapter
    private lateinit var contactDao: ContactDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        window.statusBarColor = getColor(R.color.red_dark)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        contactDao = AppDatabase.getDatabase(this).contactDao()
        contactAdapter = ContactAdapter(emptyList()) { contact ->
            val intent = Intent(this, DetailContactActivity::class.java).apply {
                putExtra("contact_id", contact.id)
            }
            startActivity(intent)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.rv_contacts)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = contactAdapter

        recyclerView.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )

        contactDao.getAllContacts().observe(this) { contacts ->
            contactAdapter.updateList(contacts)
        }

        findViewById<FloatingActionButton>(R.id.fab_add_contact).setOnClickListener {
            startActivity(Intent(this, AddContactActivity::class.java))
        }
    }
}