package com.socialconnect.data.repository

import com.socialconnect.data.api.ApiService
import com.socialconnect.data.*
import retrofit2.Response

class MessageRepository(private val apiService: ApiService) {
    
    suspend fun sendMessage(token: String, request: SendMessageRequest): Result<Message> {
        return try {
            val response = apiService.sendMessage("Bearer $token", request)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorMessage = response.body()?.message ?: "Failed to send message"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getConversation(token: String, userId: Int, limit: Int = 50, offset: Int = 0): Result<List<Message>> {
        return try {
            val response = apiService.getConversation("Bearer $token", userId, limit, offset)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                val errorMessage = response.body()?.message ?: "Failed to get conversation"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getConversations(token: String, limit: Int = 20, offset: Int = 0): Result<List<Conversation>> {
        return try {
            val response = apiService.getConversations("Bearer $token", limit, offset)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                val errorMessage = response.body()?.message ?: "Failed to get conversations"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun markMessagesAsRead(token: String, userId: Int): Result<Int> {
        return try {
            val response = apiService.markMessagesAsRead("Bearer $token", userId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data?.updatedCount ?: 0)
            } else {
                val errorMessage = response.body()?.message ?: "Failed to mark messages as read"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUnreadCount(token: String): Result<Int> {
        return try {
            val response = apiService.getUnreadCount("Bearer $token")
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data?.unreadCount ?: 0)
            } else {
                val errorMessage = response.body()?.message ?: "Failed to get unread count"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun searchMessages(token: String, query: String, userId: Int? = null, limit: Int = 20, offset: Int = 0): Result<List<Message>> {
        return try {
            val response = apiService.searchMessages("Bearer $token", query, userId, limit, offset)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                val errorMessage = response.body()?.message ?: "Failed to search messages"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteMessage(token: String, messageId: Int): Result<Boolean> {
        return try {
            val response = apiService.deleteMessage("Bearer $token", messageId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(true)
            } else {
                val errorMessage = response.body()?.message ?: "Failed to delete message"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

