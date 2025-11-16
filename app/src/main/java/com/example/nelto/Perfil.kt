package com.example.nelto

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Perfil : AppCompatActivity() {

    private lateinit var tvNombre: TextView
    private lateinit var tvApellidoP: TextView
    private lateinit var tvApellidoM: TextView
    private lateinit var tvCorreo: TextView
    private lateinit var tvTelefono: TextView
    private lateinit var btnEditarPerfil: Button
    private lateinit var btnBorradores: Button
    private lateinit var btnFavoritos: Button
    private lateinit var btnPublicaciones: Button
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.perfil_activity)

        // Inicializar vistas con TUS IDs REALES
        tvNombre = findViewById(R.id.tvNombre)
        tvApellidoP = findViewById(R.id.tvApellidoP)
        tvApellidoM = findViewById(R.id.tvApellidoM)
        tvCorreo = findViewById(R.id.tvCorreo)
        tvTelefono = findViewById(R.id.tvTelefono)
        btnEditarPerfil = findViewById(R.id.btnEditarPerfil)
        btnBorradores = findViewById(R.id.btnBorradores)
        btnFavoritos = findViewById(R.id.btnFavoritos)
        btnPublicaciones = findViewById(R.id.btnPublicaciones)

        sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)

        // Cargar y mostrar datos del usuario
        cargarDatosUsuario()

        // Configurar botones (por ahora solo mensajes)
        btnEditarPerfil.setOnClickListener {
            val intent = Intent(this, EditarPerfil::class.java)
            startActivity(intent)
        }

        btnBorradores.setOnClickListener {
            Toast.makeText(this, "Borradores - Próximamente", Toast.LENGTH_SHORT).show()
        }

        btnFavoritos.setOnClickListener {
            Toast.makeText(this, "Favoritos - Próximamente", Toast.LENGTH_SHORT).show()
        }

        btnPublicaciones.setOnClickListener {
            Toast.makeText(this, "Publicaciones - Próximamente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarDatosUsuario() {
        // Obtener datos guardados del SharedPreferences
        val nombre = sharedPref.getString("user_name", "No disponible")
        val apellidoP = sharedPref.getString("user_apellido_paterno", "No disponible")
        val apellidoM = sharedPref.getString("user_apellido_materno", "No disponible")
        val email = sharedPref.getString("user_email", "No disponible")
        val telefono = sharedPref.getString("user_phone", "No disponible")

        // Mostrar datos en los TextView
        tvNombre.text = nombre
        tvApellidoP.text = apellidoP
        tvApellidoM.text = apellidoM
        tvCorreo.text = email
        tvTelefono.text = telefono

        Toast.makeText(this, "Perfil cargado: $nombre", Toast.LENGTH_SHORT).show()
    }
}