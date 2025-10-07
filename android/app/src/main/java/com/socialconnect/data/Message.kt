package com.socialconnect.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Message(
    val id: Int,
    val senderId: Int,
    val receiverId: Int,
    val content: String,
    val messageType: String = "text",
    val isRead: Boolean = false,
    val createdAt: String,
    val senderUsername: String? = null,
    val senderFullName: String? = null,
    val senderProfilePicture: String? = null
) : Parcelable

@Parcelize
data class SendMessageRequest(
    val receiverId: Int,
    val content: String,
    val messageType: String = "text"
) : Parcelable

@Parcelize
data class MessageResponse(
    val success: Boolean,
    val message: String? = null,
    val data: Message? = null,
    val errors: List<ValidationError>? = null
) : Parcelable

@Parcelize
data class MessageListResponse(
    val success: Boolean,
    val message: String? = null,
    val data: List<Message>? = null,
    val errors: List<ValidationError>? = null
) : Parcelable

@Parcelize
data class Conversation(
    val userId: Int,
    val username: String,
    val fullName: String,
    val profilePicture: String? = null,
    val isOnline: Boolean = false,
    val lastSeen: String? = null,
    val lastMessage: String? = null,
    val lastMessageTime: String? = null,
    val lastMessageType: String? = null,
    val isRead: Boolean = true,
    val lastMessageSenderId: Int? = null,
    val unreadCount: Int = 0
) : Parcelable

@Parcelize
data class ConversationListResponse(
    val success: Boolean,
    val message: String? = null,
    val data: List<Conversation>? = null,
    val errors: List<ValidationError>? = null
) : Parcelable

@Parcelize
data class UnreadCountResponse(
    val success: Boolean,
    val message: String? = null,
    val data: UnreadCountData? = null,
    val errors: List<ValidationError>? = null
) : Parcelable

@Parcelize
data class UnreadCountData(
    val unreadCount: Int
) : Parcelable

@Parcelize
data class MarkReadResponse(
    val success: Boolean,
    val message: String? = null,
    val data: MarkReadData? = null,
    val errors: List<ValidationError>? = null
) : Parcelable

@Parcelize
data class MarkReadData(
    val updatedCount: Int
) : Parcelable

