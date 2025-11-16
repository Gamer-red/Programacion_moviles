package com.example.nelto

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Publicaciones : AppCompatActivity() {

    private lateinit var rvPublicaciones: RecyclerView
    private lateinit var adapter: PublicacionAdapter
    private val publicaciones = mutableListOf<Publicacion>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.publicaciones_activity)

        rvPublicaciones = findViewById(R.id.rvPublicacionesUsuario)
        adapter = PublicacionAdapter(publicaciones)

        rvPublicaciones.layoutManager = LinearLayoutManager(this)
        rvPublicaciones.adapter = adapter

        // Publicaciones simuladas
        publicaciones.add(
            Publicacion(
                usuario = "Yo",
                titulo = "Mi primer post",
                descripcion = "Probando la app",
                multimedia = null
            )
        )

        publicaciones.add(
            Publicacion(
                usuario = "Yo",
                titulo = "Foto del atardecer",
                descripcion = "Hermoso, ¿verdad?",
                multimedia = "android.resource://${packageName}/${R.drawable.foto_perfil_default}"
            )
        )

        adapter.notifyDataSetChanged()
    }
}
