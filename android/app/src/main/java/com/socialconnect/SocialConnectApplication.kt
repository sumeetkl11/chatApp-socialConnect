package com.socialconnect

import android.app.Application
import com.socialconnect.data.api.ApiClient
import com.socialconnect.data.repository.AuthRepository
import com.socialconnect.data.repository.UserRepository
import com.socialconnect.data.repository.PostRepository
import com.socialconnect.data.repository.MessageRepository
import com.socialconnect.utils.SocketManager

class SocialConnectApplication : Application() {
    
    // Repositories
    lateinit var authRepository: AuthRepository
    lateinit var userRepository: UserRepository
    lateinit var postRepository: PostRepository
    lateinit var messageRepository: MessageRepository
    
    // Socket Manager
    lateinit var socketManager: SocketManager
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize API client
        val apiClient = ApiClient.getInstance()
        
        // Initialize repositories
        authRepository = AuthRepository(apiClient)
        userRepository = UserRepository(apiClient)
        postRepository = PostRepository(apiClient)
        messageRepository = MessageRepository(apiClient)
        
        // Initialize socket manager
        socketManager = SocketManager()
    }
}

