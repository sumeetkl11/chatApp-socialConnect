# 🔌 API Documentation - SocialConnect

## Base URL
```
http://localhost:3000/api
```

## Authentication
Most endpoints require authentication using JWT tokens. Include the token in the Authorization header:
```
Authorization: Bearer <your_jwt_token>
```

## Response Format
All API responses follow this format:
```json
{
  "success": true|false,
  "message": "Description of the result",
  "data": {}, // Present only on success
  "errors": [] // Present only on validation errors
}
```

---

## 🔐 Authentication Endpoints

### POST /auth/register
Register a new user account.

**Request Body:**
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "fullName": "John Doe"
}
```

**Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "user": {
      "id": 1,
      "username": "john_doe",
      "email": "john@example.com",
      "full_name": "John Doe",
      "bio": null,
      "profile_picture": null,
      "created_at": "2024-01-01T00:00:00.000Z"
    },
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

### POST /auth/login
Login with email and password.

**Request Body:**
```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "user": {
      "id": 1,
      "username": "john_doe",
      "email": "john@example.com",
      "full_name": "John Doe",
      "bio": null,
      "profile_picture": null,
      "is_online": false,
      "last_seen": "2024-01-01T00:00:00.000Z",
      "created_at": "2024-01-01T00:00:00.000Z"
    },
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

### GET /auth/me
Get current user profile (requires authentication).

**Response:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "full_name": "John Doe",
    "bio": "Software developer",
    "profile_picture": "https://example.com/avatar.jpg",
    "is_online": true,
    "last_seen": "2024-01-01T00:00:00.000Z",
    "created_at": "2024-01-01T00:00:00.000Z"
  }
}
```

### PUT /auth/profile
Update user profile (requires authentication).

**Request Body:**
```json
{
  "fullName": "John Smith",
  "bio": "Updated bio"
}
```

### PUT /auth/change-password
Change user password (requires authentication).

**Request Body:**
```json
{
  "currentPassword": "oldpassword123",
  "newPassword": "newpassword123"
}
```

---

## 👥 User Management Endpoints

### GET /users/search
Search for users by username or full name.

**Query Parameters:**
- `q` (required): Search query (minimum 2 characters)
- `limit` (optional): Number of results (default: 20)
- `offset` (optional): Pagination offset (default: 0)

**Example:**
```
GET /users/search?q=john&limit=10&offset=0
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 2,
      "username": "john_doe",
      "full_name": "John Doe",
      "bio": "Software developer",
      "profile_picture": "https://example.com/avatar.jpg",
      "is_online": true,
      "last_seen": "2024-01-01T00:00:00.000Z",
      "friendship_status": "accepted"
    }
  ]
}
```

### GET /users/:id
Get user profile by ID.

**Response:**
```json
{
  "success": true,
  "data": {
    "id": 2,
    "username": "jane_smith",
    "full_name": "Jane Smith",
    "bio": "Designer",
    "profile_picture": "https://example.com/avatar2.jpg",
    "is_online": false,
    "last_seen": "2024-01-01T00:00:00.000Z",
    "created_at": "2024-01-01T00:00:00.000Z",
    "friendship_status": "pending"
  }
}
```

### POST /users/:id/friend-request
Send a friend request to another user.

**Response:**
```json
{
  "success": true,
  "message": "Friend request sent successfully"
}
```

### PUT /users/:id/accept-friend
Accept a friend request.

**Response:**
```json
{
  "success": true,
  "message": "Friend request accepted successfully"
}
```

### DELETE /users/:id/reject-friend
Reject a friend request.

**Response:**
```json
{
  "success": true,
  "message": "Friend request rejected successfully"
}
```

### DELETE /users/:id/unfriend
Remove a friend.

**Response:**
```json
{
  "success": true,
  "message": "Friend removed successfully"
}
```

### GET /users/friends/list
Get list of friends.

**Query Parameters:**
- `status` (optional): Friend status (default: "accepted")
- `limit` (optional): Number of results (default: 50)
- `offset` (optional): Pagination offset (default: 0)

### GET /users/friends/requests
Get friend requests.

**Query Parameters:**
- `type` (optional): "received" or "sent" (default: "received")
- `limit` (optional): Number of results (default: 20)
- `offset` (optional): Pagination offset (default: 0)

---

## 📝 Posts Endpoints

### POST /posts
Create a new post.

**Request Body:**
```json
{
  "content": "This is my first post!",
  "imageUrl": "https://example.com/image.jpg"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Post created successfully",
  "data": {
    "id": 1,
    "user_id": 1,
    "content": "This is my first post!",
    "image_url": "https://example.com/image.jpg",
    "likes_count": 0,
    "comments_count": 0,
    "created_at": "2024-01-01T00:00:00.000Z",
    "updated_at": "2024-01-01T00:00:00.000Z",
    "username": "john_doe",
    "full_name": "John Doe",
    "profile_picture": "https://example.com/avatar.jpg"
  }
}
```

### GET /posts/feed
Get posts feed (friends' posts + own posts).

**Query Parameters:**
- `limit` (optional): Number of results (default: 20)
- `offset` (optional): Pagination offset (default: 0)

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "user_id": 1,
      "content": "This is my first post!",
      "image_url": null,
      "likes_count": 5,
      "comments_count": 2,
      "created_at": "2024-01-01T00:00:00.000Z",
      "updated_at": "2024-01-01T00:00:00.000Z",
      "username": "john_doe",
      "full_name": "John Doe",
      "profile_picture": "https://example.com/avatar.jpg",
      "is_liked_by_user": true
    }
  ]
}
```

### GET /posts/user/:userId
Get posts by a specific user.

**Query Parameters:**
- `limit` (optional): Number of results (default: 20)
- `offset` (optional): Pagination offset (default: 0)

### GET /posts/:id
Get a specific post by ID.

### POST /posts/:id/like
Like or unlike a post.

**Response:**
```json
{
  "success": true,
  "message": "Post liked successfully",
  "data": {
    "liked": true
  }
}
```

### POST /posts/:id/comments
Add a comment to a post.

**Request Body:**
```json
{
  "content": "Great post!"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Comment added successfully",
  "data": {
    "id": 1,
    "user_id": 2,
    "post_id": 1,
    "content": "Great post!",
    "created_at": "2024-01-01T00:00:00.000Z",
    "username": "jane_smith",
    "full_name": "Jane Smith",
    "profile_picture": "https://example.com/avatar2.jpg"
  }
}
```

### GET /posts/:id/comments
Get comments for a post.

**Query Parameters:**
- `limit` (optional): Number of results (default: 20)
- `offset` (optional): Pagination offset (default: 0)

### DELETE /posts/:id
Delete a post (only by the author).

---

## 💬 Messaging Endpoints

### POST /messages/send
Send a message to another user.

**Request Body:**
```json
{
  "receiverId": 2,
  "content": "Hello! How are you?",
  "messageType": "text"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Message sent successfully",
  "data": {
    "id": 1,
    "sender_id": 1,
    "receiver_id": 2,
    "content": "Hello! How are you?",
    "message_type": "text",
    "is_read": false,
    "created_at": "2024-01-01T00:00:00.000Z",
    "sender_username": "john_doe",
    "sender_full_name": "John Doe",
    "sender_profile_picture": "https://example.com/avatar.jpg"
  }
}
```

### GET /messages/conversation/:userId
Get conversation between current user and another user.

**Query Parameters:**
- `limit` (optional): Number of results (default: 50)
- `offset` (optional): Pagination offset (default: 0)

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "sender_id": 1,
      "receiver_id": 2,
      "content": "Hello! How are you?",
      "message_type": "text",
      "is_read": true,
      "created_at": "2024-01-01T00:00:00.000Z",
      "sender_username": "john_doe",
      "sender_full_name": "John Doe",
      "sender_profile_picture": "https://example.com/avatar.jpg"
    }
  ]
}
```

### GET /messages/conversations
Get all conversations for current user.

**Query Parameters:**
- `limit` (optional): Number of results (default: 20)
- `offset` (optional): Pagination offset (default: 0)

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "user_id": 2,
      "username": "jane_smith",
      "full_name": "Jane Smith",
      "profile_picture": "https://example.com/avatar2.jpg",
      "is_online": true,
      "last_seen": "2024-01-01T00:00:00.000Z",
      "last_message": "Hello! How are you?",
      "last_message_time": "2024-01-01T00:00:00.000Z",
      "last_message_type": "text",
      "is_read": true,
      "last_message_sender_id": 1,
      "unread_count": 0
    }
  ]
}
```

### PUT /messages/mark-read/:userId
Mark messages from a specific user as read.

**Response:**
```json
{
  "success": true,
  "message": "Messages marked as read",
  "data": {
    "updatedCount": 3
  }
}
```

### GET /messages/unread-count
Get total unread message count.

**Response:**
```json
{
  "success": true,
  "data": {
    "unreadCount": 5
  }
}
```

### GET /messages/search
Search messages.

**Query Parameters:**
- `q` (required): Search query (minimum 2 characters)
- `userId` (optional): Search in specific conversation
- `limit` (optional): Number of results (default: 20)
- `offset` (optional): Pagination offset (default: 0)

### DELETE /messages/:id
Delete a message (only by the sender).

---

## 🔌 WebSocket Events

### Connection
Connect to the Socket.IO server:
```javascript
const socket = io('http://localhost:3000');
```

### Events

#### Join User Room
```javascript
socket.emit('join', userId);
```

#### Send Message
```javascript
socket.emit('send_message', {
  receiverId: 2,
  senderId: 1,
  message: {
    content: "Hello!",
    type: "text"
  }
});
```

#### Receive Message
```javascript
socket.on('receive_message', (data) => {
  console.log('New message:', data);
});
```

#### Typing Indicator
```javascript
// Send typing status
socket.emit('typing', {
  receiverId: 2,
  senderId: 1,
  isTyping: true
});

// Receive typing status
socket.on('user_typing', (data) => {
  console.log('User typing:', data);
});
```

---

## 🚨 Error Codes

### HTTP Status Codes
- `200` - Success
- `201` - Created
- `400` - Bad Request (validation errors)
- `401` - Unauthorized (invalid/missing token)
- `403` - Forbidden (insufficient permissions)
- `404` - Not Found
- `500` - Internal Server Error

### Common Error Responses

#### Validation Error
```json
{
  "success": false,
  "message": "Validation failed",
  "errors": [
    {
      "field": "email",
      "message": "Please provide a valid email"
    }
  ]
}
```

#### Authentication Error
```json
{
  "success": false,
  "message": "Access token required"
}
```

#### Not Found Error
```json
{
  "success": false,
  "message": "User not found"
}
```

---

## 📊 Rate Limiting

The API implements rate limiting to prevent abuse:
- **Limit**: 100 requests per 15 minutes per IP
- **Headers**: Rate limit information is included in response headers

---

## 🔒 Security Features

1. **JWT Authentication**: Secure token-based authentication
2. **Password Hashing**: bcrypt with salt rounds of 12
3. **Input Validation**: Comprehensive validation using express-validator
4. **SQL Injection Protection**: Parameterized queries
5. **CORS**: Configurable cross-origin resource sharing
6. **Helmet**: Security headers middleware
7. **Rate Limiting**: Protection against brute force attacks

---

## 🧪 Testing

### Health Check
```
GET /api/health
```

**Response:**
```json
{
  "status": "OK",
  "message": "SocialConnect API is running!",
  "timestamp": "2024-01-01T00:00:00.000Z"
}
```

### Example cURL Commands

#### Register User
```bash
curl -X POST http://localhost:3000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "fullName": "Test User"
  }'
```

#### Login
```bash
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

#### Get Profile (with token)
```bash
curl -X GET http://localhost:3000/api/auth/me \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

