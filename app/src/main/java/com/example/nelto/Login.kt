package com.example.nelto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nelto.model.LoginRequest
import com.example.nelto.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Login : AppCompatActivity() {
    private lateinit var etCorreo: EditText
    private lateinit var etContrasenia: EditText
    private lateinit var btnInicioSesion: Button
    private lateinit var btnRegistro: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        // Inicializar vistas con TUS IDs REALES
        etCorreo = findViewById(R.id.Correo)           // ← ID: Correo
        etContrasenia = findViewById(R.id.Contra)      // ← ID: Contra
        btnInicioSesion = findViewById(R.id.btnInicioSesion)
        btnRegistro = findViewById(R.id.btnRegistro)

        btnInicioSesion.setOnClickListener {
            val correo = etCorreo.text.toString().trim()
            val contrasenia = etContrasenia.text.toString().trim()

            if (correo.isNotEmpty() && contrasenia.isNotEmpty()) {
                if (isValidEmail(correo)) {
                    loginUser(correo, contrasenia)
                } else {
                    Toast.makeText(this, "Ingresa un correo válido", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        btnRegistro.setOnClickListener {
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun loginUser(correo: String, contrasenia: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.retrofitService.login(
                    LoginRequest(correo, contrasenia)
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val loginResponse = response.body()!!

                        Log.d("LOGIN_DEBUG", "🔍 Respuesta completa: ${response.body()}")
                        Log.d("LOGIN_DEBUG", "🔍 User object: $loginResponse")
                        Log.d("LOGIN_DEBUG", "🔍 User fields: ${loginResponse.user}")

                        // GUARDAR TODOS LOS DATOS DEL USUARIO (VERSIÓN COMPLETA)
                        val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putString("auth_token", "Bearer ${loginResponse.token}")
                            putInt("user_id", loginResponse.user.Id_usuario)
                            putString("user_name", loginResponse.user.Nombre)
                            putString("user_apellido_paterno", loginResponse.user.Apellido_paterno)
                            putString("user_apellido_materno", loginResponse.user.Apellido_materno)
                            putString("user_email", loginResponse.user.Correo)
                            putString("user_alias", loginResponse.user.Alias)
                            putString("user_phone", loginResponse.user.telefono)
                            apply()
                        }

                        Log.d("LOGIN_DEBUG", "📝 Datos guardados:")
                        Log.d("LOGIN_DEBUG", "Nombre: ${loginResponse.user.Nombre}")
                        Log.d("LOGIN_DEBUG", "Apellido P: ${loginResponse.user.Apellido_paterno}")
                        Log.d("LOGIN_DEBUG", "Apellido M: ${loginResponse.user.Apellido_materno}")
                        Log.d("LOGIN_DEBUG", "Correo: ${loginResponse.user.Correo}")
                        Log.d("LOGIN_DEBUG", "Teléfono: ${loginResponse.user.telefono}")



                        Toast.makeText(this@Login, "✅ ¡Bienvenido ${loginResponse.user.Nombre}!", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@Login, Perfil::class.java)
                        startActivity(intent)
                        finish()

                    } else {
                        Log.d("LOGIN_DEBUG", "❌ Error - Código: ${response.code()}")
                        Toast.makeText(this@Login, "❌ Error en login", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.d("LOGIN_DEBUG", "💥 Excepción: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Login, "💥 Error de conexión", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}