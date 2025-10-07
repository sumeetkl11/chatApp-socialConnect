package com.socialconnect.data.repository

import com.socialconnect.data.api.ApiService
import com.socialconnect.data.*
import retrofit2.Response

class AuthRepository(private val apiService: ApiService) {
    
    suspend fun register(request: RegisterRequest): Result<AuthData> {
        return try {
            val response = apiService.register(request)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorMessage = response.body()?.message ?: "Registration failed"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun login(request: LoginRequest): Result<AuthData> {
        return try {
            val response = apiService.login(request)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorMessage = response.body()?.message ?: "Login failed"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getCurrentUser(token: String): Result<User> {
        return try {
            val response = apiService.getCurrentUser("Bearer $token")
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorMessage = response.body()?.message ?: "Failed to get user profile"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateProfile(token: String, request: UpdateProfileRequest): Result<User> {
        return try {
            val response = apiService.updateProfile("Bearer $token", request)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorMessage = response.body()?.message ?: "Failed to update profile"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun changePassword(token: String, request: ChangePasswordRequest): Result<Boolean> {
        return try {
            val response = apiService.changePassword("Bearer $token", request)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(true)
            } else {
                val errorMessage = response.body()?.message ?: "Failed to change password"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

