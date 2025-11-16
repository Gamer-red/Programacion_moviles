package com.example.nelto

data class Publicacion(
    val usuario: String,
    val titulo: String,
    val descripcion: String,
    val multimedia: String?, // path de imagen o video
    val comentarios: MutableList<Comentario> = mutableListOf()
)
