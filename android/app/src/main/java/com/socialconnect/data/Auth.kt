package com.socialconnect.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginRequest(
    val email: String,
    val password: String
) : Parcelable

@Parcelize
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val fullName: String
) : Parcelable

@Parcelize
data class AuthResponse(
    val success: Boolean,
    val message: String? = null,
    val data: AuthData? = null,
    val errors: List<ValidationError>? = null
) : Parcelable

@Parcelize
data class AuthData(
    val user: User,
    val token: String
) : Parcelable

@Parcelize
data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
) : Parcelable

@Parcelize
data class UpdateProfileRequest(
    val fullName: String? = null,
    val bio: String? = null
) : Parcelable

