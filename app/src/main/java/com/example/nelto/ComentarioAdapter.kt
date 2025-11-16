package com.example.nelto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ComentarioAdapter(private val comentarios: List<Comentario>) :
    RecyclerView.Adapter<ComentarioAdapter.ComentarioViewHolder>() {

    inner class ComentarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAutor: TextView = itemView.findViewById(R.id.tvAutor)
        val tvContenido: TextView = itemView.findViewById(R.id.tvContenido)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComentarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comentario, parent, false)
        return ComentarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: ComentarioViewHolder, position: Int) {
        val comentario = comentarios[position]
        holder.tvAutor.text = comentario.autor
        holder.tvContenido.text = comentario.contenido
    }

    override fun getItemCount(): Int = comentarios.size
}
