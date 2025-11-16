package com.example.nelto

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Borradores : AppCompatActivity() {

    private lateinit var rvBorradores: RecyclerView
    private lateinit var borradorAdapter: BorradorAdapter
    private val borradores = mutableListOf<Borrador>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.borradores_activity)

        rvBorradores = findViewById(R.id.rvBorradores)
        borradorAdapter = BorradorAdapter(
            this,
            borradores,
            onPublicar = { posicion ->
                val borrador = borradores[posicion]
                Toast.makeText(this, "Publicado: ${borrador.titulo}", Toast.LENGTH_SHORT).show()
                borradores.removeAt(posicion)
                borradorAdapter.notifyItemRemoved(posicion)
                // Aquí podrías moverlo a la lista de publicaciones reales
            },
            onBorrar = { posicion ->
                borradores.removeAt(posicion)
                borradorAdapter.notifyItemRemoved(posicion)
                Toast.makeText(this, "Borrador eliminado", Toast.LENGTH_SHORT).show()
            }
        )

        rvBorradores.layoutManager = LinearLayoutManager(this)
        rvBorradores.adapter = borradorAdapter

        // Datos simulados
        borradores.add(
            Borrador(
                titulo = "Idea genial 💡",
                descripcion = "Descripción de la idea",
                multimedia = "android.resource://${packageName}/${R.drawable.foto_perfil_default}"
            )
        )
        borradores.add(
            Borrador(
                titulo = "Otro post",
                descripcion = "Sin imagen",
                multimedia = null
            )
        )
        borradorAdapter.notifyDataSetChanged()
    }
}
