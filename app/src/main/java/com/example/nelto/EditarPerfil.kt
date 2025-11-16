package com.example.nelto

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nelto.model.UpdateProfileRequest
import com.example.nelto.model.getApellidoMaterno
import com.example.nelto.model.getApellidoPaterno
import com.example.nelto.model.getDisplayName
import com.example.nelto.model.getTelefono
import com.example.nelto.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditarPerfil : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etApellidoP: EditText
    private lateinit var etApellidoM: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etCorreo: EditText
    private lateinit var btnEditar: Button
    private lateinit var btnGuardar: Button
    private lateinit var sharedPref: SharedPreferences

    private var isEditing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.editar_perfil_activity)

        // Inicializar vistas
        etNombre = findViewById(R.id.Nombre)
        etApellidoP = findViewById(R.id.ApellidoP)
        etApellidoM = findViewById(R.id.ApellidoM)
        etTelefono = findViewById(R.id.Telefono)
        etCorreo = findViewById(R.id.Correo)
        btnEditar = findViewById(R.id.btnEditar)
        btnGuardar = findViewById(R.id.btnGuardar)

        sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)

        // Cargar datos actuales del usuario
        cargarDatosUsuario()

        // Configurar botones
        btnEditar.setOnClickListener {
            habilitarEdicion()
        }

        btnGuardar.setOnClickListener {
            guardarCambios()
        }
    }

    private fun cargarDatosUsuario() {
        // Obtener datos guardados del SharedPreferences
        val nombre = sharedPref.getString("user_name", "")
        val apellidoP = sharedPref.getString("user_apellido_paterno", "")
        val apellidoM = sharedPref.getString("user_apellido_materno", "")
        val telefono = sharedPref.getString("user_phone", "")
        val correo = sharedPref.getString("user_email", "")
        val alias = sharedPref.getString("user_alias", "")

        // Mostrar datos en los EditText
        etNombre.setText(nombre)
        etApellidoP.setText(apellidoP)
        etApellidoM.setText(apellidoM)
        etTelefono.setText(telefono)
        etCorreo.setText(correo)

        // El correo es solo lectura (no se puede cambiar)
        etCorreo.isEnabled = false
    }

    private fun habilitarEdicion() {
        isEditing = true

        // Habilitar campos editables
        etNombre.isEnabled = true
        etApellidoP.isEnabled = true
        etApellidoM.isEnabled = true
        etTelefono.isEnabled = true
        etCorreo.isEnabled = true
        // El correo se mantiene deshabilitado

        // Cambiar estado de botones
        btnEditar.isEnabled = false
        btnGuardar.isEnabled = true

        Toast.makeText(this, "Modo edición activado", Toast.LENGTH_SHORT).show()
    }

    private fun guardarCambios() {
        val nombre = etNombre.text.toString().trim()
        val apellidoP = etApellidoP.text.toString().trim()
        val apellidoM = etApellidoM.text.toString().trim()
        val telefono = etTelefono.text.toString().trim()
        val alias = sharedPref.getString("user_alias", "") ?: ""
        val correo = etCorreo.text.toString().trim()


        // Validaciones básicas
        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre es requerido", Toast.LENGTH_SHORT).show()
            return
        }

        val authToken = sharedPref.getString("auth_token", "") ?: ""

        if (authToken.isEmpty()) {
            Toast.makeText(this, "Error: No hay token de autenticación", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.retrofitService.updateProfile(
                    authToken,
                    UpdateProfileRequest(
                        nombre = nombre,
                        apellido_paterno = apellidoP,
                        apellido_materno = apellidoM,
                        alias = alias,
                        telefono = telefono,
                        correo = correo
                    )
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val updateResponse = response.body()!!

                        // Actualizar datos en SharedPreferences
                        with(sharedPref.edit()) {
                            putString("user_name", updateResponse.user.getDisplayName())
                            putString("user_apellido_paterno", updateResponse.user.getApellidoPaterno())
                            putString("user_apellido_materno", updateResponse.user.getApellidoMaterno())
                            putString("user_email", updateResponse.user.Correo)
                            putString("user_phone", updateResponse.user.getTelefono())
                            apply()

                            putString("user_name", updateResponse.user.getDisplayName())
                            putString("user_apellido_paterno", updateResponse.user.getApellidoPaterno())
                            putString("user_apellido_materno", updateResponse.user.getApellidoMaterno())
                            putString("user_phone", updateResponse.user.getTelefono())
                        }

                        Toast.makeText(this@EditarPerfil, "✅ ${updateResponse.message}", Toast.LENGTH_SHORT).show()

                        // Volver al perfil
                        finish()

                    } else {
                        val errorMessage = when (response.code()) {
                            400 -> "Datos inválidos"
                            401 -> "No autorizado"
                            404 -> "Usuario no encontrado"
                            else -> "Error del servidor: ${response.code()}"
                        }
                        Toast.makeText(this@EditarPerfil, "❌ $errorMessage", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditarPerfil, "💥 Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}