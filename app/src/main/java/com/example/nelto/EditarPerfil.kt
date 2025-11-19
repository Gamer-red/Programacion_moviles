package com.example.nelto

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import com.example.nelto.model.ProfileResponse
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
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.IOException

class EditarPerfil : AppCompatActivity() {

    private lateinit var ivFotoPerfil: ImageView
    private lateinit var etNombre: EditText
    private lateinit var etApellidoP: EditText
    private lateinit var etApellidoM: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etCorreo: EditText
    private lateinit var btnCambiarFoto: Button
    private lateinit var btnEditar: Button
    private lateinit var btnGuardar: Button
    private lateinit var sharedPref: SharedPreferences

    private val PICK_IMAGE = 100
    private var selectedImageBitmap: Bitmap? = null
    private var isEditing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.editar_perfil_activity)

        initViews()
        setupClickListeners()

        sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)

        // Cargar datos actuales del usuario
        cargarDatosUsuario()
    }

    private fun initViews() {
        ivFotoPerfil = findViewById(R.id.FotoPerfil)
        etNombre = findViewById(R.id.Nombre)
        etApellidoP = findViewById(R.id.ApellidoP)
        etApellidoM = findViewById(R.id.ApellidoM)
        etTelefono = findViewById(R.id.Telefono)
        etCorreo = findViewById(R.id.Correo)
        btnCambiarFoto = findViewById(R.id.btnCambiarFoto)
        btnEditar = findViewById(R.id.btnEditar)
        btnGuardar = findViewById(R.id.btnGuardar)
    }

    private fun setupClickListeners() {
        // Botón cambiar foto
        btnCambiarFoto.setOnClickListener {
            if (isEditing) {
                val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(gallery, PICK_IMAGE)
            }
        }

        // Botón editar
        btnEditar.setOnClickListener {
            habilitarEdicion(true)
        }

        // Botón guardar
        btnGuardar.setOnClickListener {
            guardarCambios()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            try {
                selectedImageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                ivFotoPerfil.setImageBitmap(selectedImageBitmap)
                Toast.makeText(this, "Imagen seleccionada", Toast.LENGTH_SHORT).show()
                Log.d("EDITAR_PERFIL", "📸 Nueva imagen seleccionada")
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show()
                Log.e("EDITAR_PERFIL", "❌ Error cargando nueva imagen: ${e.message}")
            }
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

        // Cargar imagen actual desde la API
        cargarImagenActual()

        // Inicialmente deshabilitar edición
        habilitarEdicion(false)
    }

    private fun cargarImagenActual() {
        val token = sharedPref.getString("auth_token", "") ?: ""

        if (token.isEmpty()) {
            // Fallback: imagen por defecto
            ivFotoPerfil.setImageResource(android.R.drawable.sym_def_app_icon)
            Log.d("EDITAR_PERFIL", "🔐 No hay token, usando imagen por defecto")
            return
        }

        // Cargar la imagen actual desde la API
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("EDITAR_PERFIL", "🔄 Cargando imagen actual desde API...")

                val response: Response<ProfileResponse> = ApiClient.retrofitService.getProfile(token)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val user = response.body()!!.user
                        Log.d("EDITAR_PERFIL", "✅ Datos obtenidos, cargando imagen...")

                        // Cargar imagen si existe
                        user.Avatar?.let { avatarBase64 ->
                            if (avatarBase64.isNotEmpty()) {
                                cargarImagenDesdeBase64(avatarBase64)
                                return@withContext
                            }
                        }

                        // Si no hay imagen, usar por defecto
                        Log.d("EDITAR_PERFIL", "📸 Usuario no tiene imagen, usando por defecto")
                        ivFotoPerfil.setImageResource(android.R.drawable.sym_def_app_icon)
                    } else {
                        // Error, usar imagen por defecto
                        Log.e("EDITAR_PERFIL", "❌ Error obteniendo perfil: ${response.code()}")
                        ivFotoPerfil.setImageResource(android.R.drawable.sym_def_app_icon)
                    }
                }
            } catch (e: Exception) {
                Log.e("EDITAR_PERFIL", "💥 Excepción cargando imagen: ${e.message}")
                withContext(Dispatchers.Main) {
                    // Error de conexión, usar imagen por defecto
                    ivFotoPerfil.setImageResource(android.R.drawable.sym_def_app_icon)
                }
            }
        }
    }

    private fun cargarImagenDesdeBase64(base64String: String) {
        try {
            val pureBase64 = if (base64String.contains("base64,")) {
                base64String.substringAfter("base64,")
            } else {
                base64String
            }

            val imageBytes = Base64.decode(pureBase64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

            if (bitmap != null) {
                ivFotoPerfil.setImageBitmap(bitmap)
                Log.d("EDITAR_PERFIL", "✅ Imagen actual cargada exitosamente")
            } else {
                throw Exception("No se pudo decodificar la imagen")
            }
        } catch (e: Exception) {
            Log.e("EDITAR_PERFIL", "❌ Error cargando imagen actual: ${e.message}")
            ivFotoPerfil.setImageResource(android.R.drawable.sym_def_app_icon)
        }
    }

    private fun habilitarEdicion(habilitar: Boolean) {
        isEditing = habilitar

        etNombre.isEnabled = habilitar
        etApellidoP.isEnabled = habilitar
        etApellidoM.isEnabled = habilitar
        etTelefono.isEnabled = habilitar
        etCorreo.isEnabled = habilitar

        btnCambiarFoto.isEnabled = habilitar
        btnGuardar.isEnabled = habilitar
        btnEditar.isEnabled = !habilitar

        if (habilitar) {
            Toast.makeText(this, "Modo edición activado", Toast.LENGTH_SHORT).show()
            Log.d("EDITAR_PERFIL", "✏️ Modo edición activado")
        }
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
            etNombre.error = "El nombre es requerido"
            Toast.makeText(this, "El nombre es requerido", Toast.LENGTH_SHORT).show()
            return
        }

        val authToken = sharedPref.getString("auth_token", "") ?: ""

        if (authToken.isEmpty()) {
            Toast.makeText(this, "Error: No hay token de autenticación", Toast.LENGTH_SHORT).show()
            return
        }

        // Convertir imagen a base64 si se seleccionó una nueva
        val avatarBase64 = selectedImageBitmap?.let {
            Log.d("EDITAR_PERFIL", "📸 Convirtiendo nueva imagen a base64...")
            convertBitmapToBase64(it)
        }

        Log.d("EDITAR_PERFIL", "📸 Imagen a enviar: ${if (avatarBase64 != null) "SÍ" else "NO"}")
        Log.d("EDITAR_PERFIL", "📝 Datos a enviar: nombre=$nombre, correo=$correo")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("EDITAR_PERFIL", "🔄 Enviando actualización al servidor...")

                val response = ApiClient.retrofitService.updateProfile(
                    authToken,
                    UpdateProfileRequest(
                        nombre = nombre,
                        apellido_paterno = apellidoP,
                        apellido_materno = apellidoM,
                        alias = alias,
                        telefono = telefono,
                        correo = correo,
                        avatar = avatarBase64
                    )
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val updateResponse = response.body()!!
                        Log.d("EDITAR_PERFIL", "✅ Actualización exitosa: ${updateResponse.message}")

                        // Actualizar datos en SharedPreferences
                        with(sharedPref.edit()) {
                            putString("user_name", updateResponse.user.getDisplayName())
                            putString("user_apellido_paterno", updateResponse.user.getApellidoPaterno())
                            putString("user_apellido_materno", updateResponse.user.getApellidoMaterno())
                            putString("user_email", updateResponse.user.Correo)
                            putString("user_phone", updateResponse.user.getTelefono())
                            apply()
                        }

                        Toast.makeText(this@EditarPerfil, "✅ ${updateResponse.message}", Toast.LENGTH_SHORT).show()

                        // Volver al perfil
                        finish()

                    } else {
                        val errorMessage = try {
                            val errorBody = response.errorBody()?.string()
                            "Error: ${errorBody ?: response.message()}"
                        } catch (e: Exception) {
                            "Error del servidor: ${response.code()}"
                        }
                        Log.e("EDITAR_PERFIL", "❌ Error HTTP: $errorMessage")
                        Toast.makeText(this@EditarPerfil, "❌ $errorMessage", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("EDITAR_PERFIL", "💥 Excepción: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditarPerfil, "💥 Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun convertBitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()

        // Comprimir imagen
        val scaledBitmap = scaleBitmap(bitmap, 400)
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream)

        val byteArray = byteArrayOutputStream.toByteArray()
        val base64String = "data:image/jpeg;base64,${Base64.encodeToString(byteArray, Base64.DEFAULT)}"

        Log.d("EDITAR_PERFIL", "📸 Imagen convertida, tamaño: ${base64String.length} caracteres")
        return base64String
    }

    private fun scaleBitmap(bitmap: Bitmap, maxWidth: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        if (width <= maxWidth) return bitmap

        val scaleRatio = maxWidth.toFloat() / width
        val newWidth = maxWidth
        val newHeight = (height * scaleRatio).toInt()

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
}