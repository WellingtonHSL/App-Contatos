package com.wellingtonhenrique.contatos.ui

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.wellingtonhenrique.contatos.R
import com.wellingtonhenrique.contatos.data.AppDatabase
import com.wellingtonhenrique.contatos.data.Contact
import com.wellingtonhenrique.contatos.utils.ImageUtils
import com.wellingtonhenrique.contatos.utils.ImageUtils.saveImageToInternalStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

class DetailContactActivity : AppCompatActivity() {

    private var currentContact: Contact? = null
    private var isEditing = false
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_contact)
        setSupportActionBar(findViewById(R.id.toolbar_detail))

        window.statusBarColor = getColor(R.color.red_dark)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detail_contact)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar_detail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        val edtName = findViewById<EditText>(R.id.edt_name)
        val edtPhone = findViewById<EditText>(R.id.edt_phone)
        val edtImage = findViewById<ImageView>(R.id.iv_add_person)
        val btnEdit = findViewById<Button>(R.id.btnEdit)
        val btnDelete = findViewById<Button>(R.id.btnDelete)

        edtName.isEnabled = false
        edtPhone.isEnabled = false

        val contactId = intent.getIntExtra("contact_id", -1)
        val dao = AppDatabase.getDatabase(this).contactDao()

        if (contactId != -1) {
            dao.getContactById(contactId).observe(this) { contact ->
                contact?.let {
                    currentContact = it
                    edtName.setText(it.name)
                    edtPhone.setText(it.phone)
                    if (it.imageUrl.isNotEmpty()) {
                        Glide.with(this)
                            .load(File(it.imageUrl))
                            .placeholder(R.drawable.ic_person)
                            .error(R.drawable.ic_person)
                            .into(edtImage)
                    } else {
                        edtImage.setImageResource(R.drawable.ic_person)
                    }
                }
            }
        }

        val imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                if (uri != null && isEditing) {
                    selectedImageUri = uri
                    edtImage.setImageURI(uri)
                }
            }

        edtImage.setOnClickListener {
            if (isEditing) {
                imagePickerLauncher.launch("image/*")
            }
        }

        btnEdit.setOnClickListener {
            if (!isEditing) {
                edtName.isEnabled = true
                edtPhone.isEnabled = true
                btnEdit.text = "Salvar"
                isEditing = true
            } else {
                val updatedName = edtName.text.toString()
                val updatedPhone = edtPhone.text.toString()
                val updatedImage = selectedImageUri?.let { uri ->
                    currentContact?.imageUrl?.let { oldPath ->
                        ImageUtils.deleteImageFromInternalStorage(oldPath)
                    }
                    saveImageToInternalStorage(this@DetailContactActivity, uri)
                } ?: currentContact?.imageUrl ?: ""

                if (updatedName.isNotEmpty() && updatedPhone.isNotEmpty()) {
                    val updatedContact = currentContact?.copy(
                        name = updatedName,
                        phone = updatedPhone,
                        imageUrl = updatedImage
                    )

                    updatedContact?.let {
                        CoroutineScope(Dispatchers.IO).launch {
                            dao.updateContact(it)
                            runOnUiThread {
                                Toast.makeText(
                                    this@DetailContactActivity,
                                    "Contato atualizado!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                }
            }

        }

        btnDelete.setOnClickListener {
            currentContact?.let { contact ->
                CoroutineScope(Dispatchers.IO).launch {
                    dao.deleteContact(contact)
                    runOnUiThread {
                        Toast.makeText(
                            this@DetailContactActivity,
                            "Contato excluído!",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }
            }
        }
    }
}