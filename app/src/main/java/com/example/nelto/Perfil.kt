package com.example.nelto

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nelto.model.ProfileResponse
import com.example.nelto.model.User
import com.example.nelto.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class Perfil : AppCompatActivity() {

    private lateinit var tvNombre: TextView
    private lateinit var tvApellidoP: TextView
    private lateinit var tvApellidoM: TextView
    private lateinit var tvCorreo: TextView
    private lateinit var tvTelefono: TextView
    private lateinit var ivFotoPerfil: ImageView
    private lateinit var btnEditarPerfil: Button
    private lateinit var btnBorradores: Button
    private lateinit var btnFavoritos: Button
    private lateinit var btnPublicaciones: Button
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.perfil_activity)

        initViews()
        setupClickListeners()

        sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)

        // Cargar datos desde la API
        cargarDatosDesdeAPI()
    }

    private fun initViews() {
        tvNombre = findViewById(R.id.tvNombre)
        tvApellidoP = findViewById(R.id.tvApellidoP)
        tvApellidoM = findViewById(R.id.tvApellidoM)
        tvCorreo = findViewById(R.id.tvCorreo)
        tvTelefono = findViewById(R.id.tvTelefono)
        ivFotoPerfil = findViewById(R.id.ivFotoPerfil)
        btnEditarPerfil = findViewById(R.id.btnEditarPerfil)
        btnBorradores = findViewById(R.id.btnBorradores)
        btnFavoritos = findViewById(R.id.btnFavoritos)
        btnPublicaciones = findViewById(R.id.btnPublicaciones)
    }

    private fun setupClickListeners() {
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

    private fun cargarDatosDesdeAPI() {
        val token = sharedPref.getString("auth_token", "") ?: ""

        if (token.isEmpty()) {
            Toast.makeText(this, "No hay sesión activa", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("PERFIL_DEBUG", "🔍 Solicitando perfil con token: ${token.take(20)}...")

                val response: Response<ProfileResponse> = ApiClient.retrofitService.getProfile(token)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val profileResponse = response.body()!!
                        Log.d("PERFIL_DEBUG", "✅ Perfil obtenido: ${profileResponse.user}")
                        actualizarUI(profileResponse.user)
                    } else {
                        Log.e("PERFIL_DEBUG", "❌ Error: ${response.code()} - ${response.message()}")
                        // Fallback: cargar desde SharedPreferences
                        cargarDatosDesdeSharedPreferences()
                    }
                }
            } catch (e: Exception) {
                Log.e("PERFIL_DEBUG", "💥 Excepción: ${e.message}")
                withContext(Dispatchers.Main) {
                    // Fallback: cargar desde SharedPreferences
                    cargarDatosDesdeSharedPreferences()
                    Toast.makeText(this@Perfil, "Error de conexión, mostrando datos locales", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun actualizarUI(user: User) {
        // Actualizar UI con datos del API
        tvNombre.text = user.Nombre
        tvApellidoP.text = user.Apellido_paterno ?: "No disponible"
        tvApellidoM.text = user.Apellido_materno ?: "No disponible"
        tvCorreo.text = user.Correo
        tvTelefono.text = user.telefono ?: "No disponible"

        // Actualizar SharedPreferences con datos frescos
        actualizarSharedPreferences(user)

        // Cargar imagen si existe
        if (user.Avatar != null && user.Avatar.isNotEmpty()) {
            Log.d("PERFIL_DEBUG", "📸 Cargando imagen de perfil")
            cargarImagenDesdeBase64(user.Avatar)
        } else {
            Log.d("PERFIL_DEBUG", "📸 No hay imagen de perfil")
            ivFotoPerfil.setImageResource(R.drawable.foto_perfil_default)
        }

        Toast.makeText(this, "Perfil actualizado: ${user.Nombre}", Toast.LENGTH_SHORT).show()
    }

    private fun cargarImagenDesdeBase64(base64String: String) {
        try {
            // Remover el prefijo "data:image/jpeg;base64," si existe
            val pureBase64 = if (base64String.contains("base64,")) {
                base64String.substringAfter("base64,")
            } else {
                base64String
            }

            val imageBytes = Base64.decode(pureBase64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

            if (bitmap != null) {
                ivFotoPerfil.setImageBitmap(bitmap)
                Log.d("PERFIL_DEBUG", "✅ Imagen cargada exitosamente")
            } else {
                throw Exception("No se pudo decodificar la imagen")
            }
        } catch (e: Exception) {
            Log.e("PERFIL_DEBUG", "❌ Error cargando imagen: ${e.message}")
            // Usar imagen por defecto si hay error
            ivFotoPerfil.setImageResource(R.drawable.foto_perfil_default)
        }
    }

    private fun actualizarSharedPreferences(user: User) {
        with(sharedPref.edit()) {
            putString("user_name", user.Nombre)
            putString("user_apellido_paterno", user.Apellido_paterno ?: "")
            putString("user_apellido_materno", user.Apellido_materno ?: "")
            putString("user_email", user.Correo)
            putString("user_phone", user.telefono ?: "")
            putString("user_alias", user.Alias ?: "")
            apply()
        }
    }

    private fun cargarDatosDesdeSharedPreferences() {
        // Fallback: cargar datos desde SharedPreferences
        val nombre = sharedPref.getString("user_name", "No disponible")
        val apellidoP = sharedPref.getString("user_apellido_paterno", "No disponible")
        val apellidoM = sharedPref.getString("user_apellido_materno", "No disponible")
        val email = sharedPref.getString("user_email", "No disponible")
        val telefono = sharedPref.getString("user_phone", "No disponible")

        tvNombre.text = nombre
        tvApellidoP.text = apellidoP
        tvApellidoM.text = apellidoM
        tvCorreo.text = email
        tvTelefono.text = telefono

        ivFotoPerfil.setImageResource(R.drawable.foto_perfil_default)

        Toast.makeText(this, "Perfil cargado (modo local): $nombre", Toast.LENGTH_SHORT).show()
    }
}