package com.example.nelto

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent

import android.app.Activity
import android.net.Uri
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.bumptech.glide.Glide
import android.widget.Toast
import android.util.Patterns
import kotlin.text.isEmpty
import kotlin.text.isNotEmpty
import kotlin.text.trim

class Registro : AppCompatActivity() {

    private lateinit var ivFotoPerfil: ImageView
    private val PICK_IMAGE = 100
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registro_activity)

        ivFotoPerfil = findViewById(R.id.ivFotoPerfil)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)

        // Seleccionar foto de perfil
        ivFotoPerfil.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK)
            gallery.type = "image/*"
            startActivityForResult(gallery, PICK_IMAGE)
        }

        btnRegistrar.setOnClickListener {
            // Recoger datos
            val nombre = findViewById<EditText>(R.id.Nombre)
            val apellidoP = findViewById<EditText>(R.id.ApellidoP)
            val apellidoM = findViewById<EditText>(R.id.ApellidoM)
            val correo = findViewById<EditText>(R.id.Correo)
            val contrasena = findViewById<EditText>(R.id.Contra)
            val contrasena2 = findViewById<EditText>(R.id.Contra2)
            val telefono = findViewById<EditText>(R.id.Telefono)


            if(!validarCampoVacio(nombre)) {
                return@setOnClickListener
            }

            if(!validarCampoVacio(apellidoP)) {
                return@setOnClickListener
            }

            if(!validarCampoVacio(apellidoM)) {
                return@setOnClickListener
            }

            if(!validarTelefono(telefono)) {
                return@setOnClickListener
            }

            if (!esCorreoValido(correo.text.toString())) {
                correo.error = "Correo no válido"
                return@setOnClickListener
            }

            if (!esContrasenaValida(contrasena.text.toString())) {
                contrasena.error = "Debe tener mínimo 10 caracteres, una mayúscula, una minúscula y un número"
                return@setOnClickListener
            }

            if (contrasena.text.toString() != contrasena2.text.toString()) {
                contrasena2.error = "Las contraseñas no coinciden"
                return@setOnClickListener
            }

            Toast.makeText(this, "Registro válido", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            imageUri = data?.data
            Glide.with(this)
                .load(imageUri)
                .circleCrop()
                .into(ivFotoPerfil)
        }
    }

    private fun esCorreoValido(correo: String): Boolean {
        return correo.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(correo).matches()
    }

    // ✅ Función 2: Validar contraseña segura
    private fun esContrasenaValida(contra: String): Boolean {
        // Debe tener al menos 10 caracteres, 1 número, 1 mayúscula y 1 minúscula
        val regex = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{10,}$")
        return contra.isNotEmpty() && regex.matches(contra)
    }

    private fun validarCampoVacio(editText: EditText): Boolean {
        val texto = editText.text.toString().trim()
        return if (texto.isEmpty()) {
            editText.error = "El campo no puede estar vacio"
            false
        } else {
            editText.error = null
            true
        }
    }

    private fun validarTelefono(numero: EditText): Boolean {
        val telefono = numero.text.toString()
        return if(telefono.isEmpty() || telefono.length < 10){
            numero.error = "numero no valido"
            false
        }else {
            numero.error = null
            true
        }
    }
}
