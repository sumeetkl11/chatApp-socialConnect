package com.socialconnect.data.api

import com.socialconnect.data.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // Authentication endpoints
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
    
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
    
    @GET("auth/me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): Response<UserResponse>
    
    @PUT("auth/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): Response<UserResponse>
    
    @PUT("auth/change-password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Body request: ChangePasswordRequest
    ): Response<AuthResponse>
    
    // User endpoints
    @GET("users/search")
    suspend fun searchUsers(
        @Header("Authorization") token: String,
        @Query("q") query: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<UserListResponse>
    
    @GET("users/{id}")
    suspend fun getUserById(
        @Header("Authorization") token: String,
        @Path("id") userId: Int
    ): Response<UserResponse>
    
    @POST("users/{id}/friend-request")
    suspend fun sendFriendRequest(
        @Header("Authorization") token: String,
        @Path("id") userId: Int
    ): Response<AuthResponse>
    
    @PUT("users/{id}/accept-friend")
    suspend fun acceptFriendRequest(
        @Header("Authorization") token: String,
        @Path("id") userId: Int
    ): Response<AuthResponse>
    
    @DELETE("users/{id}/reject-friend")
    suspend fun rejectFriendRequest(
        @Header("Authorization") token: String,
        @Path("id") userId: Int
    ): Response<AuthResponse>
    
    @DELETE("users/{id}/unfriend")
    suspend fun unfriend(
        @Header("Authorization") token: String,
        @Path("id") userId: Int
    ): Response<AuthResponse>
    
    @GET("users/friends/list")
    suspend fun getFriendsList(
        @Header("Authorization") token: String,
        @Query("status") status: String = "accepted",
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): Response<UserListResponse>
    
    @GET("users/friends/requests")
    suspend fun getFriendRequests(
        @Header("Authorization") token: String,
        @Query("type") type: String = "received",
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<UserListResponse>
    
    // Post endpoints
    @POST("posts")
    suspend fun createPost(
        @Header("Authorization") token: String,
        @Body request: CreatePostRequest
    ): Response<PostResponse>
    
    @GET("posts/feed")
    suspend fun getFeed(
        @Header("Authorization") token: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<PostListResponse>
    
    @GET("posts/user/{userId}")
    suspend fun getUserPosts(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<PostListResponse>
    
    @GET("posts/{id}")
    suspend fun getPostById(
        @Header("Authorization") token: String,
        @Path("id") postId: Int
    ): Response<PostResponse>
    
    @POST("posts/{id}/like")
    suspend fun likePost(
        @Header("Authorization") token: String,
        @Path("id") postId: Int
    ): Response<LikeResponse>
    
    @POST("posts/{id}/comments")
    suspend fun addComment(
        @Header("Authorization") token: String,
        @Path("id") postId: Int,
        @Body request: CreateCommentRequest
    ): Response<CommentResponse>
    
    @GET("posts/{id}/comments")
    suspend fun getPostComments(
        @Header("Authorization") token: String,
        @Path("id") postId: Int,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<CommentListResponse>
    
    @DELETE("posts/{id}")
    suspend fun deletePost(
        @Header("Authorization") token: String,
        @Path("id") postId: Int
    ): Response<AuthResponse>
    
    // Message endpoints
    @POST("messages/send")
    suspend fun sendMessage(
        @Header("Authorization") token: String,
        @Body request: SendMessageRequest
    ): Response<MessageResponse>
    
    @GET("messages/conversation/{userId}")
    suspend fun getConversation(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): Response<MessageListResponse>
    
    @GET("messages/conversations")
    suspend fun getConversations(
        @Header("Authorization") token: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<ConversationListResponse>
    
    @PUT("messages/mark-read/{userId}")
    suspend fun markMessagesAsRead(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Response<MarkReadResponse>
    
    @GET("messages/unread-count")
    suspend fun getUnreadCount(
        @Header("Authorization") token: String
    ): Response<UnreadCountResponse>
    
    @GET("messages/search")
    suspend fun searchMessages(
        @Header("Authorization") token: String,
        @Query("q") query: String,
        @Query("userId") userId: Int? = null,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<MessageListResponse>
    
    @DELETE("messages/{id}")
    suspend fun deleteMessage(
        @Header("Authorization") token: String,
        @Path("id") messageId: Int
    ): Response<AuthResponse>
}

