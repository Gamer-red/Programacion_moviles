package com.example.nelto

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PublicacionAdapter(private val publicaciones: List<Publicacion>) :
    RecyclerView.Adapter<PublicacionAdapter.PublicacionViewHolder>() {

    inner class PublicacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUsuario: TextView = itemView.findViewById(R.id.tvUsuario)
        val tvTitulo: TextView = itemView.findViewById(R.id.tvTitulo)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        val ivMultimedia: ImageView = itemView.findViewById(R.id.ivMultimedia)

        val btnLike: Button = itemView.findViewById(R.id.btnLike)
        val btnDislike: Button = itemView.findViewById(R.id.btnDislike)
        val btnComentar: Button = itemView.findViewById(R.id.btnComentar)

        val rvComentarios: RecyclerView = itemView.findViewById(R.id.rvComentarios)
        val etNuevoComentario: EditText = itemView.findViewById(R.id.etNuevoComentario)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublicacionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_publicacion, parent, false)
        return PublicacionViewHolder(view)
    }

    override fun onBindViewHolder(holder: PublicacionViewHolder, position: Int) {
        val publicacion = publicaciones[position]

        holder.tvUsuario.text = publicacion.usuario
        holder.tvTitulo.text = publicacion.titulo
        holder.tvDescripcion.text = publicacion.descripcion

        // Mostrar multimedia si existe
        if (publicacion.multimedia != null) {
            holder.ivMultimedia.visibility = View.VISIBLE

            Glide.with(holder.itemView.context)
                .load(Uri.parse(publicacion.multimedia)) // convertimos el String a URI
                .into(holder.ivMultimedia)
        } else {
            holder.ivMultimedia.visibility = View.GONE
        }

        // Like/Dislike (simples, sin lógica real)
        holder.btnLike.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Te gustó la publicación", Toast.LENGTH_SHORT).show()
        }

        holder.btnDislike.setOnClickListener {
            Toast.makeText(holder.itemView.context, "No te gustó la publicación", Toast.LENGTH_SHORT).show()
        }

        // Manejar comentarios
        val context = holder.itemView.context
        val comentarioAdapter = ComentarioAdapter(publicacion.comentarios)
        holder.rvComentarios.layoutManager = LinearLayoutManager(context)
        holder.rvComentarios.adapter = comentarioAdapter

        var comentariosVisibles = false

        holder.btnComentar.setOnClickListener {
            comentariosVisibles = !comentariosVisibles

            holder.rvComentarios.visibility = if (comentariosVisibles) View.VISIBLE else View.GONE
            holder.etNuevoComentario.visibility = if (comentariosVisibles) View.VISIBLE else View.GONE
        }

        holder.etNuevoComentario.setOnEditorActionListener { v, actionId, event ->
            val texto = holder.etNuevoComentario.text.toString()
            if (texto.isNotBlank()) {
                val nuevoComentario = Comentario("Tú", texto)
                publicacion.comentarios.add(nuevoComentario)
                comentarioAdapter.notifyItemInserted(publicacion.comentarios.size - 1)
                holder.etNuevoComentario.text.clear()
            }
            true
        }
    }

    override fun getItemCount(): Int = publicaciones.size
}
