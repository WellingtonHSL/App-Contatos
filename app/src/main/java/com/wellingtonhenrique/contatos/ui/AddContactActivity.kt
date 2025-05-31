package com.wellingtonhenrique.contatos.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.material.appbar.MaterialToolbar
import com.wellingtonhenrique.contatos.R
import com.wellingtonhenrique.contatos.data.AppDatabase
import com.wellingtonhenrique.contatos.data.Contact
import com.wellingtonhenrique.contatos.data.ContactDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddContactActivity : AppCompatActivity() {

    private lateinit var contactDao: ContactDao
    private lateinit var edtImage: ImageView
    private lateinit var edtName: EditText
    private lateinit var edtPhone: EditText
    private lateinit var btnCancel: Button
    private lateinit var btnSave: Button

    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_contact)
        setSupportActionBar(findViewById(R.id.toolbar_add))


        window.statusBarColor = getColor(R.color.red_dark)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.add_contact)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar_add)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        contactDao = AppDatabase.getDatabase(this).contactDao()

        edtImage = findViewById(R.id.iv_add_person)
        edtName = findViewById(R.id.edt_name)
        edtPhone = findViewById(R.id.edt_phone)
        btnCancel = findViewById(R.id.btnCancel)
        btnSave = findViewById(R.id.btnSave)

        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                if (uri != null) {
                    edtImage.setImageURI(uri)
                    edtImage.tag = uri.toString()
                }
            }

        edtImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        btnCancel.setOnClickListener {
            finish()
        }

        btnSave.setOnClickListener {
            val name = edtName.text.toString()
            val phone = edtPhone.text.toString()
            val imageUri = edtImage.tag?.toString() ?: ""

            if (name.isNotEmpty() && phone.isNotEmpty()) {
                val contact = Contact(
                    name = name,
                    phone = phone,
                    imageUrl = imageUri
                )

                CoroutineScope(Dispatchers.IO).launch {
                    contactDao.insertContact(contact)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@AddContactActivity,
                            "Contato salvo!",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}