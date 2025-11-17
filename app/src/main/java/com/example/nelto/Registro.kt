package com.example.nelto

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nelto.model.RegisterRequest
import com.example.nelto.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException

class Registro : AppCompatActivity() {

    private lateinit var ivFotoPerfil: ImageView
    private lateinit var etNombre: EditText
    private lateinit var etApellidoP: EditText
    private lateinit var etApellidoM: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etContra: EditText
    private lateinit var etContra2: EditText
    private lateinit var etTelefono: EditText
    private lateinit var btnRegistrar: Button
    private lateinit var btnIniciarSesion: Button

    private val PICK_IMAGE = 100
    private var selectedImageBitmap: android.graphics.Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registro_activity)

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        ivFotoPerfil = findViewById(R.id.ivFotoPerfil)
        etNombre = findViewById(R.id.Nombre)
        etApellidoP = findViewById(R.id.ApellidoP)
        etApellidoM = findViewById(R.id.ApellidoM)
        etCorreo = findViewById(R.id.Correo)
        etContra = findViewById(R.id.Contra)
        etContra2 = findViewById(R.id.Contra2)
        etTelefono = findViewById(R.id.Telefono)
        btnRegistrar = findViewById(R.id.btnRegistrar)
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion)
    }

    private fun setupClickListeners() {
        // Seleccionar foto de perfil
        ivFotoPerfil.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(gallery, PICK_IMAGE)
        }

        // Botón registrar
        btnRegistrar.setOnClickListener {
            attemptRegistration()
        }

        // Botón iniciar sesión (volver al login)
        btnIniciarSesion.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            try {
                selectedImageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                ivFotoPerfil.setImageBitmap(selectedImageBitmap)
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun attemptRegistration() {
        val nombre = etNombre.text.toString().trim()
        val apellidoP = etApellidoP.text.toString().trim()
        val apellidoM = etApellidoM.text.toString().trim()
        val correo = etCorreo.text.toString().trim()
        val contra = etContra.text.toString().trim()
        val contra2 = etContra2.text.toString().trim()
        val telefono = etTelefono.text.toString().trim()

        if (validateInputs(nombre, apellidoP, apellidoM, correo, contra, contra2, telefono)) {
            registerUser(nombre, apellidoP, apellidoM, correo, contra, telefono)
        }
    }

    private fun validateInputs(
        nombre: String,
        apellidoP: String,
        apellidoM: String,
        correo: String,
        contra: String,
        contra2: String,
        telefono: String
    ): Boolean {
        // Validar campos vacíos
        if (nombre.isEmpty()) {
            etNombre.error = "El nombre es requerido"
            return false
        }
        if (apellidoP.isEmpty()) {
            etApellidoP.error = "El apellido paterno es requerido"
            return false
        }
        if (apellidoM.isEmpty()) {
            etApellidoM.error = "El apellido materno es requerido"
            return false
        }
        if (!esCorreoValido(correo)) {
            etCorreo.error = "Correo no válido"
            return false
        }
        if (!esContrasenaValida(contra)) {
            etContra.error = "Debe tener mínimo 10 caracteres, una mayúscula, una minúscula y un número"
            return false
        }
        if (contra != contra2) {
            etContra2.error = "Las contraseñas no coinciden"
            return false
        }
        if (telefono.isEmpty() || telefono.length < 10) {
            etTelefono.error = "Número no válido"
            return false
        }

        return true
    }

    private fun registerUser(
        nombre: String,
        apellidoP: String,
        apellidoM: String,
        correo: String,
        contra: String,
        telefono: String
    ) {
        // Generar alias automáticamente
        val alias = "${nombre.lowercase()}.${apellidoP.lowercase()}"

        // Convertir imagen a base64 si existe
        val avatarBase64 = selectedImageBitmap?.let { convertBitmapToBase64(it) }

        val registerRequest = RegisterRequest(
            nombre = nombre,
            apellido_paterno = apellidoP,
            apellido_materno = apellidoM,
            correo = correo,
            contrasenia = contra,
            alias = alias,
            telefono = telefono,
            avatar = avatarBase64
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.retrofitService.register(registerRequest)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val registerResponse = response.body()!!
                        handleRegistrationSuccess(registerResponse)
                    } else {
                        // Manejar error del servidor
                        val errorMessage = try {
                            "Error: ${response.errorBody()?.string() ?: response.message()}"
                        } catch (e: Exception) {
                            "Error en el registro: ${response.message()}"
                        }
                        showError(errorMessage)
                        Log.e("REGISTRO_DEBUG", "Error: $errorMessage")
                    }
                }
            } catch (e: Exception) {
                Log.e("REGISTRO_DEBUG", "Excepción: ${e.message}")
                withContext(Dispatchers.Main) {
                    showError("Error de conexión: ${e.message}")
                }
            }
        }
    }

    private fun convertBitmapToBase64(bitmap: android.graphics.Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return "data:image/jpeg;base64,${Base64.encodeToString(byteArray, Base64.DEFAULT)}"
    }

    private fun handleRegistrationSuccess(response: com.example.nelto.model.RegisterResponse) {
        Log.d("REGISTRO_DEBUG", "✅ Registro exitoso")
        Log.d("REGISTRO_DEBUG", "Token: ${response.token}")
        Log.d("REGISTRO_DEBUG", "Usuario: ${response.user}")

        // Guardar token y información del usuario en SharedPreferences
        val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("auth_token", "Bearer ${response.token}")
            putInt("user_id", response.user.Id_usuario)
            putString("user_name", response.user.Nombre)
            putString("user_apellido_paterno", response.user.Apellido_paterno)
            putString("user_apellido_materno", response.user.Apellido_materno)
            putString("user_email", response.user.Correo)
            putString("user_alias", response.user.Alias)
            putString("user_phone", response.user.telefono)
            apply()
        }

        Toast.makeText(this, "✅ ${response.message}", Toast.LENGTH_SHORT).show()

        // Redirigir a la actividad principal (ajusta según tu app)
        val intent = Intent(this, Perfil::class.java) // o MainActivity::class.java
        startActivity(intent)
        finish()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun esCorreoValido(correo: String): Boolean {
        return correo.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()
    }

    private fun esContrasenaValida(contra: String): Boolean {
        val regex = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{10,}$")
        return contra.isNotEmpty() && regex.matches(contra)
    }
}