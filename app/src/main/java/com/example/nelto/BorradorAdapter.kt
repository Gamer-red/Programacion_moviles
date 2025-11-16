package com.example.nelto

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class BorradorAdapter(
    private val context: Context,
    private val borradores: MutableList<Borrador>,
    private val onPublicar: (Int) -> Unit,
    private val onBorrar: (Int) -> Unit
) : RecyclerView.Adapter<BorradorAdapter.BorradorViewHolder>() {

    inner class BorradorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitulo: TextView = itemView.findViewById(R.id.tvTitulo)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        val ivMultimedia: ImageView = itemView.findViewById(R.id.ivMultimedia)
        val btnPublicar: Button = itemView.findViewById(R.id.btnPublicar)
        val btnBorrar: Button = itemView.findViewById(R.id.btnBorrar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BorradorViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_borrador, parent, false)
        return BorradorViewHolder(view)
    }

    override fun getItemCount(): Int = borradores.size

    override fun onBindViewHolder(holder: BorradorViewHolder, position: Int) {
        val borrador = borradores[position]
        holder.tvTitulo.text = borrador.titulo
        holder.tvDescripcion.text = borrador.descripcion

        if (!borrador.multimedia.isNullOrEmpty()) {
            holder.ivMultimedia.visibility = View.VISIBLE
            Glide.with(context)
                .load(Uri.parse(borrador.multimedia))
                .into(holder.ivMultimedia)
        } else {
            holder.ivMultimedia.visibility = View.GONE
        }

        holder.btnPublicar.setOnClickListener { onPublicar(position) }
        holder.btnBorrar.setOnClickListener { onBorrar(position) }
    }
}
