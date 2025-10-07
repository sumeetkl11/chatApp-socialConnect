# 📊 Database Schema - SocialConnect

## Overview
This document describes the MySQL database schema for the SocialConnect application. The database is designed to support user management, social features, and real-time messaging.

## Database: `socialconnect`

### Tables

#### 1. Users Table
Stores user account information and profile data.

```sql
CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) UNIQUE NOT NULL,
  email VARCHAR(100) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  full_name VARCHAR(100) NOT NULL,
  bio TEXT,
  profile_picture VARCHAR(255),
  is_online BOOLEAN DEFAULT FALSE,
  last_seen TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

**Fields:**
- `id`: Primary key, auto-increment
- `username`: Unique username (3-50 characters, alphanumeric + underscore)
- `email`: Unique email address
- `password`: Hashed password using bcrypt
- `full_name`: User's display name
- `bio`: Optional user biography
- `profile_picture`: URL to profile image
- `is_online`: Current online status
- `last_seen`: Last activity timestamp
- `created_at`: Account creation timestamp
- `updated_at`: Last update timestamp

#### 2. Posts Table
Stores user posts for the social feed.

```sql
CREATE TABLE posts (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  content TEXT NOT NULL,
  image_url VARCHAR(255),
  likes_count INT DEFAULT 0,
  comments_count INT DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

**Fields:**
- `id`: Primary key, auto-increment
- `user_id`: Foreign key to users table
- `content`: Post text content (1-2000 characters)
- `image_url`: Optional image URL
- `likes_count`: Cached count of likes
- `comments_count`: Cached count of comments
- `created_at`: Post creation timestamp
- `updated_at`: Last update timestamp

#### 3. Messages Table
Stores private messages between users.

```sql
CREATE TABLE messages (
  id INT AUTO_INCREMENT PRIMARY KEY,
  sender_id INT NOT NULL,
  receiver_id INT NOT NULL,
  content TEXT NOT NULL,
  message_type ENUM('text', 'image', 'file') DEFAULT 'text',
  is_read BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE
);
```

**Fields:**
- `id`: Primary key, auto-increment
- `sender_id`: Foreign key to users table (sender)
- `receiver_id`: Foreign key to users table (receiver)
- `content`: Message content (1-2000 characters)
- `message_type`: Type of message (text, image, file)
- `is_read`: Read status
- `created_at`: Message creation timestamp

#### 4. Friends Table
Manages friend relationships and friend requests.

```sql
CREATE TABLE friends (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  friend_id INT NOT NULL,
  status ENUM('pending', 'accepted', 'blocked') DEFAULT 'pending',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE,
  UNIQUE KEY unique_friendship (user_id, friend_id)
);
```

**Fields:**
- `id`: Primary key, auto-increment
- `user_id`: Foreign key to users table (requester)
- `friend_id`: Foreign key to users table (target)
- `status`: Friendship status (pending, accepted, blocked)
- `created_at`: Request creation timestamp
- `updated_at`: Last update timestamp

#### 5. Likes Table
Stores post likes.

```sql
CREATE TABLE likes (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  post_id INT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
  UNIQUE KEY unique_like (user_id, post_id)
);
```

**Fields:**
- `id`: Primary key, auto-increment
- `user_id`: Foreign key to users table
- `post_id`: Foreign key to posts table
- `created_at`: Like creation timestamp

#### 6. Comments Table
Stores post comments.

```sql
CREATE TABLE comments (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  post_id INT NOT NULL,
  content TEXT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);
```

**Fields:**
- `id`: Primary key, auto-increment
- `user_id`: Foreign key to users table
- `post_id`: Foreign key to posts table
- `content`: Comment content (1-500 characters)
- `created_at`: Comment creation timestamp

## Relationships

### One-to-Many Relationships
- **Users → Posts**: One user can have many posts
- **Users → Messages (as sender)**: One user can send many messages
- **Users → Messages (as receiver)**: One user can receive many messages
- **Users → Likes**: One user can like many posts
- **Users → Comments**: One user can comment on many posts
- **Posts → Likes**: One post can have many likes
- **Posts → Comments**: One post can have many comments

### Many-to-Many Relationships
- **Users ↔ Users (Friends)**: Users can have many friends through the friends table

## Indexes

### Primary Indexes
- All tables have auto-increment primary keys

### Unique Indexes
- `users.username` - Unique username constraint
- `users.email` - Unique email constraint
- `friends.unique_friendship` - Prevents duplicate friendships
- `likes.unique_like` - Prevents duplicate likes

### Foreign Key Indexes
- All foreign key columns are automatically indexed

## Data Integrity

### Constraints
- **NOT NULL**: Required fields are marked as NOT NULL
- **UNIQUE**: Username and email must be unique
- **ENUM**: Status fields use ENUM for data validation
- **FOREIGN KEY**: All relationships are enforced with foreign keys
- **CASCADE DELETE**: Related records are automatically deleted when parent is deleted

### Validation Rules
- Username: 3-50 characters, alphanumeric + underscore only
- Email: Valid email format
- Password: Minimum 6 characters (hashed with bcrypt)
- Post content: 1-2000 characters
- Comment content: 1-500 characters
- Message content: 1-2000 characters

## Performance Considerations

### Caching
- `posts.likes_count` and `posts.comments_count` are cached for performance
- Consider adding indexes on frequently queried columns

### Query Optimization
- Use LIMIT and OFFSET for pagination
- Index on `created_at` columns for time-based queries
- Consider composite indexes for complex queries

## Sample Data

### Users
```sql
INSERT INTO users (username, email, password, full_name, bio) VALUES
('john_doe', 'john@example.com', '$2a$12$...', 'John Doe', 'Software developer'),
('jane_smith', 'jane@example.com', '$2a$12$...', 'Jane Smith', 'Designer');
```

### Posts
```sql
INSERT INTO posts (user_id, content) VALUES
(1, 'Hello world! This is my first post.'),
(2, 'Working on a new design project today!');
```

### Friends
```sql
INSERT INTO friends (user_id, friend_id, status) VALUES
(1, 2, 'accepted');
```

## Migration Scripts

The database tables are automatically created when the server starts using the `initializeTables()` function in `src/utils/database.js`. This ensures the database schema is always up-to-date.

## Backup and Recovery

### Backup
```bash
mysqldump -u root -p socialconnect > socialconnect_backup.sql
```

### Restore
```bash
mysql -u root -p socialconnect < socialconnect_backup.sql
```

## Security Considerations

1. **Password Security**: All passwords are hashed using bcrypt with salt rounds of 12
2. **SQL Injection**: All queries use parameterized statements
3. **Data Validation**: Input validation is performed at the API level
4. **Access Control**: JWT tokens are used for authentication
5. **CORS**: Cross-origin requests are properly configured

