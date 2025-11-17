package com.example.nelto.model

data class User(
    val Id_usuario: Int,              // ← Exactamente como en tu BD
    val Nombre: String,               // ← Con mayúscula
    val Apellido_materno: String?,    // ← Puede ser null
    val Apellido_paterno: String?,    // ← Puede ser null
    val Correo: String,               // ← Con mayúscula
    val Alias: String?,               // ← Con mayúscula, puede ser null
    val telefono: String?,            // ← minúscula, puede ser null
    val Avatar: String?               // ← Con mayúscula, puede ser null
)
data class LoginRequest(
    val correo: String,
    val contrasenia: String
)

data class LoginResponse(
    val message: String,
    val token: String,
    val user: User
)

data class RegisterRequest(
    val nombre: String,
    val apellido_paterno: String,
    val apellido_materno: String,
    val correo: String,
    val contrasenia: String,
    val alias: String,
    val telefono: String? = null,      // ← Opcional
    val avatar: String? = null
)

data class RegisterResponse(
    val message: String,
    val token: String,
    val user: User
)

// User Models
data class UpdateProfileRequest(
    val nombre: String,
    val apellido_paterno: String,
    val apellido_materno: String,
    val alias: String,
    val telefono: String,
    val correo: String
)

data class UpdateProfileResponse(
    val message: String,
    val user: User
)

data class ProfileResponse(
    val user: User
)

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)

data class MessageResponse(
    val message: String
)

fun User.getDisplayName(): String {
    return this.Nombre ?: "Usuario"
}

fun User.getEmail(): String {
    return this.Correo ?: "No disponible"
}

fun User.getApellidoPaterno(): String {
    return this.Apellido_paterno ?: "No disponible"
}

fun User.getApellidoMaterno(): String {
    return this.Apellido_materno ?: "No disponible"
}

fun User.getTelefono(): String {
    return this.telefono ?: "No disponible"
}

fun User.getAlias(): String {
    return this.Alias ?: "Sin alias"
}

