package com.socialconnect.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: Int,
    val username: String,
    val email: String,
    val fullName: String,
    val bio: String? = null,
    val profilePicture: String? = null,
    val isOnline: Boolean = false,
    val lastSeen: String? = null,
    val createdAt: String? = null,
    val friendshipStatus: String? = null
) : Parcelable

@Parcelize
data class UserResponse(
    val success: Boolean,
    val message: String? = null,
    val data: User? = null,
    val errors: List<ValidationError>? = null
) : Parcelable

@Parcelize
data class UserListResponse(
    val success: Boolean,
    val message: String? = null,
    val data: List<User>? = null,
    val errors: List<ValidationError>? = null
) : Parcelable

@Parcelize
data class ValidationError(
    val field: String,
    val message: String
) : Parcelable

