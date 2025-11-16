package com.example.nelto

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Favoritos : AppCompatActivity() {

    private lateinit var rvFavoritos: RecyclerView
    private lateinit var adapter: PublicacionAdapter
    private val favoritos = mutableListOf<Publicacion>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favoritos_activity)

        rvFavoritos = findViewById(R.id.rvFavoritos)
        adapter = PublicacionAdapter(favoritos)

        rvFavoritos.layoutManager = LinearLayoutManager(this)
        rvFavoritos.adapter = adapter

        // Ejemplo de favoritos simulados
        favoritos.add(
            Publicacion(
                usuario = "María",
                titulo = "Viaje a la montaña",
                descripcion = "Un lugar hermoso para desconectar.",
                multimedia = "android.resource://${packageName}/${R.drawable.foto_perfil_default}"
            )
        )

        favoritos.add(
            Publicacion(
                usuario = "Luis",
                titulo = "Receta de pizza 🍕",
                descripcion = "¡Imperdible!",
                multimedia = null
            )
        )

        adapter.notifyDataSetChanged()
    }
}
