package com.example.nelto

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import android.widget.*
import com.bumptech.glide.Glide

class Inicio : AppCompatActivity() {

    private lateinit var rvPublicaciones: RecyclerView
    private lateinit var publicacionAdapter: PublicacionAdapter
    private val publicaciones = mutableListOf<Publicacion>()

    private lateinit var ivPreviewMultimedia: ImageView

    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.inicio_activity)

        // Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = "Inicio"
        toolbar.inflateMenu(R.menu.menu_toolbar)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_inicio -> {
                    true
                }
                R.id.menu_perfil -> {
                    val intent = Intent(this, Perfil::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_cerrar_sesion -> {
                    finish();
                    true
                }
                else -> false
            }
        }

        ivPreviewMultimedia = findViewById(R.id.ivPreviewMultimedia)

        val btnCargarMultimedia = findViewById<Button>(R.id.btnCargarMultimedia)

        btnCargarMultimedia.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Inicializar RecyclerView
        rvPublicaciones = findViewById(R.id.rvPublicaciones)
        publicacionAdapter = PublicacionAdapter(publicaciones)
        rvPublicaciones.layoutManager = LinearLayoutManager(this)
        rvPublicaciones.adapter = publicacionAdapter

        // Publicar
        val btnPublicar: Button = findViewById(R.id.btnPublicar)
        val etTitulo: EditText = findViewById(R.id.etTitulo)
        val etDescripcion: EditText = findViewById(R.id.etDescripcion)

        btnPublicar.setOnClickListener {
            val titulo = etTitulo.text.toString()
            val descripcion = etDescripcion.text.toString()

            if (titulo.isNotEmpty() && descripcion.isNotEmpty()) {
                val nueva = Publicacion(
                    usuario = "Tú",
                    titulo = titulo,
                    descripcion = descripcion,
                    multimedia = selectedImageUri?.toString()
                )
                publicaciones.add(0, nueva)
                publicacionAdapter.notifyItemInserted(0)
                rvPublicaciones.scrollToPosition(0)

                etTitulo.text.clear()
                etDescripcion.text.clear()
                selectedImageUri = null

                // Limpia la previsualización
                ivPreviewMultimedia.setImageDrawable(null)
                ivPreviewMultimedia.visibility = ImageView.GONE
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data

            ivPreviewMultimedia.visibility = ImageView.VISIBLE
            Glide.with(this)
                .load(selectedImageUri)
                .into(ivPreviewMultimedia)
        }
    }
}