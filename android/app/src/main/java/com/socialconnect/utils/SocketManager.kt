package com.socialconnect.utils

import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import java.net.URISyntaxException

class SocketManager {
    private var socket: Socket? = null
    private var isConnected = false
    
    fun connect(baseUrl: String = "http://10.0.2.2:3000") {
        try {
            val options = IO.Options().apply {
                forceNew = true
                reconnection = true
                timeout = 20000
            }
            
            socket = IO.socket(baseUrl, options)
            
            socket?.on(Socket.EVENT_CONNECT) {
                isConnected = true
                onConnectCallback?.invoke()
            }
            
            socket?.on(Socket.EVENT_DISCONNECT) {
                isConnected = false
                onDisconnectCallback?.invoke()
            }
            
            socket?.on(Socket.EVENT_CONNECT_ERROR) { args ->
                isConnected = false
                onErrorCallback?.invoke(args[0]?.toString() ?: "Connection error")
            }
            
            socket?.on("receive_message") { args ->
                try {
                    val data = args[0] as JSONObject
                    val message = MessageData(
                        senderId = data.getInt("senderId"),
                        message = data.getJSONObject("message"),
                        timestamp = data.getString("timestamp")
                    )
                    onMessageCallback?.invoke(message)
                } catch (e: Exception) {
                    onErrorCallback?.invoke("Error parsing message: ${e.message}")
                }
            }
            
            socket?.on("user_typing") { args ->
                try {
                    val data = args[0] as JSONObject
                    val typingData = TypingData(
                        senderId = data.getInt("senderId"),
                        isTyping = data.getBoolean("isTyping")
                    )
                    onTypingCallback?.invoke(typingData)
                } catch (e: Exception) {
                    onErrorCallback?.invoke("Error parsing typing data: ${e.message}")
                }
            }
            
            socket?.connect()
        } catch (e: URISyntaxException) {
            onErrorCallback?.invoke("Invalid URL: ${e.message}")
        }
    }
    
    fun disconnect() {
        socket?.disconnect()
        socket = null
        isConnected = false
    }
    
    fun joinUserRoom(userId: Int) {
        if (isConnected) {
            socket?.emit("join", userId)
        }
    }
    
    fun sendMessage(receiverId: Int, senderId: Int, message: JSONObject) {
        if (isConnected) {
            val data = JSONObject().apply {
                put("receiverId", receiverId)
                put("senderId", senderId)
                put("message", message)
            }
            socket?.emit("send_message", data)
        }
    }
    
    fun sendTypingStatus(receiverId: Int, senderId: Int, isTyping: Boolean) {
        if (isConnected) {
            val data = JSONObject().apply {
                put("receiverId", receiverId)
                put("senderId", senderId)
                put("isTyping", isTyping)
            }
            socket?.emit("typing", data)
        }
    }
    
    // Callbacks
    private var onConnectCallback: (() -> Unit)? = null
    private var onDisconnectCallback: (() -> Unit)? = null
    private var onErrorCallback: ((String) -> Unit)? = null
    private var onMessageCallback: ((MessageData) -> Unit)? = null
    private var onTypingCallback: ((TypingData) -> Unit)? = null
    
    fun setOnConnectCallback(callback: () -> Unit) {
        onConnectCallback = callback
    }
    
    fun setOnDisconnectCallback(callback: () -> Unit) {
        onDisconnectCallback = callback
    }
    
    fun setOnErrorCallback(callback: (String) -> Unit) {
        onErrorCallback = callback
    }
    
    fun setOnMessageCallback(callback: (MessageData) -> Unit) {
        onMessageCallback = callback
    }
    
    fun setOnTypingCallback(callback: (TypingData) -> Unit) {
        onTypingCallback = callback
    }
    
    fun isSocketConnected(): Boolean = isConnected
}

data class MessageData(
    val senderId: Int,
    val message: JSONObject,
    val timestamp: String
)

data class TypingData(
    val senderId: Int,
    val isTyping: Boolean
)

