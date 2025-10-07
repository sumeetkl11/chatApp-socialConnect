package com.socialconnect.data.repository

import com.socialconnect.data.api.ApiService
import com.socialconnect.data.*
import retrofit2.Response

class PostRepository(private val apiService: ApiService) {
    
    suspend fun createPost(token: String, request: CreatePostRequest): Result<Post> {
        return try {
            val response = apiService.createPost("Bearer $token", request)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorMessage = response.body()?.message ?: "Failed to create post"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getFeed(token: String, limit: Int = 20, offset: Int = 0): Result<List<Post>> {
        return try {
            val response = apiService.getFeed("Bearer $token", limit, offset)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                val errorMessage = response.body()?.message ?: "Failed to get feed"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserPosts(token: String, userId: Int, limit: Int = 20, offset: Int = 0): Result<List<Post>> {
        return try {
            val response = apiService.getUserPosts("Bearer $token", userId, limit, offset)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                val errorMessage = response.body()?.message ?: "Failed to get user posts"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getPostById(token: String, postId: Int): Result<Post> {
        return try {
            val response = apiService.getPostById("Bearer $token", postId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorMessage = response.body()?.message ?: "Failed to get post"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun likePost(token: String, postId: Int): Result<Boolean> {
        return try {
            val response = apiService.likePost("Bearer $token", postId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data?.liked ?: false)
            } else {
                val errorMessage = response.body()?.message ?: "Failed to like post"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun addComment(token: String, postId: Int, request: CreateCommentRequest): Result<Comment> {
        return try {
            val response = apiService.addComment("Bearer $token", postId, request)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorMessage = response.body()?.message ?: "Failed to add comment"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getPostComments(token: String, postId: Int, limit: Int = 20, offset: Int = 0): Result<List<Comment>> {
        return try {
            val response = apiService.getPostComments("Bearer $token", postId, limit, offset)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                val errorMessage = response.body()?.message ?: "Failed to get comments"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deletePost(token: String, postId: Int): Result<Boolean> {
        return try {
            val response = apiService.deletePost("Bearer $token", postId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(true)
            } else {
                val errorMessage = response.body()?.message ?: "Failed to delete post"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

