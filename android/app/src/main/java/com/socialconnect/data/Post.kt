package com.socialconnect.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
    val id: Int,
    val userId: Int,
    val content: String,
    val imageUrl: String? = null,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val createdAt: String,
    val updatedAt: String? = null,
    val username: String? = null,
    val fullName: String? = null,
    val profilePicture: String? = null,
    val isLikedByUser: Boolean = false
) : Parcelable

@Parcelize
data class CreatePostRequest(
    val content: String,
    val imageUrl: String? = null
) : Parcelable

@Parcelize
data class PostResponse(
    val success: Boolean,
    val message: String? = null,
    val data: Post? = null,
    val errors: List<ValidationError>? = null
) : Parcelable

@Parcelize
data class PostListResponse(
    val success: Boolean,
    val message: String? = null,
    val data: List<Post>? = null,
    val errors: List<ValidationError>? = null
) : Parcelable

@Parcelize
data class LikeResponse(
    val success: Boolean,
    val message: String? = null,
    val data: LikeData? = null,
    val errors: List<ValidationError>? = null
) : Parcelable

@Parcelize
data class LikeData(
    val liked: Boolean
) : Parcelable

@Parcelize
data class Comment(
    val id: Int,
    val userId: Int,
    val postId: Int,
    val content: String,
    val createdAt: String,
    val username: String? = null,
    val fullName: String? = null,
    val profilePicture: String? = null
) : Parcelable

@Parcelize
data class CreateCommentRequest(
    val content: String
) : Parcelable

@Parcelize
data class CommentResponse(
    val success: Boolean,
    val message: String? = null,
    val data: Comment? = null,
    val errors: List<ValidationError>? = null
) : Parcelable

@Parcelize
data class CommentListResponse(
    val success: Boolean,
    val message: String? = null,
    val data: List<Comment>? = null,
    val errors: List<ValidationError>? = null
) : Parcelable

