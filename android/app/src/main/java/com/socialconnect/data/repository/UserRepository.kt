package com.socialconnect.data.repository

import com.socialconnect.data.api.ApiService
import com.socialconnect.data.*
import retrofit2.Response

class UserRepository(private val apiService: ApiService) {
    
    suspend fun searchUsers(token: String, query: String, limit: Int = 20, offset: Int = 0): Result<List<User>> {
        return try {
            val response = apiService.searchUsers("Bearer $token", query, limit, offset)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                val errorMessage = response.body()?.message ?: "Failed to search users"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserById(token: String, userId: Int): Result<User> {
        return try {
            val response = apiService.getUserById("Bearer $token", userId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorMessage = response.body()?.message ?: "Failed to get user"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun sendFriendRequest(token: String, userId: Int): Result<Boolean> {
        return try {
            val response = apiService.sendFriendRequest("Bearer $token", userId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(true)
            } else {
                val errorMessage = response.body()?.message ?: "Failed to send friend request"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun acceptFriendRequest(token: String, userId: Int): Result<Boolean> {
        return try {
            val response = apiService.acceptFriendRequest("Bearer $token", userId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(true)
            } else {
                val errorMessage = response.body()?.message ?: "Failed to accept friend request"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun rejectFriendRequest(token: String, userId: Int): Result<Boolean> {
        return try {
            val response = apiService.rejectFriendRequest("Bearer $token", userId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(true)
            } else {
                val errorMessage = response.body()?.message ?: "Failed to reject friend request"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun unfriend(token: String, userId: Int): Result<Boolean> {
        return try {
            val response = apiService.unfriend("Bearer $token", userId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(true)
            } else {
                val errorMessage = response.body()?.message ?: "Failed to unfriend"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getFriendsList(token: String, status: String = "accepted", limit: Int = 50, offset: Int = 0): Result<List<User>> {
        return try {
            val response = apiService.getFriendsList("Bearer $token", status, limit, offset)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                val errorMessage = response.body()?.message ?: "Failed to get friends list"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getFriendRequests(token: String, type: String = "received", limit: Int = 20, offset: Int = 0): Result<List<User>> {
        return try {
            val response = apiService.getFriendRequests("Bearer $token", type, limit, offset)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                val errorMessage = response.body()?.message ?: "Failed to get friend requests"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

