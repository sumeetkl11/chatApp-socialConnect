const express = require('express');
const { body, validationResult } = require('express-validator');
const { executeQuery } = require('../utils/database');
const { authenticateToken, optionalAuth } = require('../middleware/auth');

const router = express.Router();

// Create a new post
router.post('/', authenticateToken, [
  body('content')
    .isLength({ min: 1, max: 2000 })
    .withMessage('Post content must be between 1 and 2000 characters'),
  body('imageUrl')
    .optional()
    .isURL()
    .withMessage('Image URL must be valid')
], async (req, res) => {
  try {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({
        success: false,
        message: 'Validation failed',
        errors: errors.array()
      });
    }

    const { content, imageUrl } = req.body;

    // Create post
    const result = await executeQuery(
      'INSERT INTO posts (user_id, content, image_url) VALUES (?, ?, ?)',
      [req.user.id, content, imageUrl || null]
    );

    // Get the created post with user info
    const newPost = await executeQuery(`
      SELECT 
        p.*,
        u.username,
        u.full_name,
        u.profile_picture
      FROM posts p
      JOIN users u ON p.user_id = u.id
      WHERE p.id = ?
    `, [result.insertId]);

    res.status(201).json({
      success: true,
      message: 'Post created successfully',
      data: newPost[0]
    });
  } catch (error) {
    console.error('Create post error:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to create post'
    });
  }
});

// Get posts feed (friends' posts + own posts)
router.get('/feed', authenticateToken, async (req, res) => {
  try {
    const { limit = 20, offset = 0 } = req.query;

    const posts = await executeQuery(`
      SELECT 
        p.*,
        u.username,
        u.full_name,
        u.profile_picture,
        CASE WHEN l.user_id IS NOT NULL THEN 1 ELSE 0 END as is_liked_by_user
      FROM posts p
      JOIN users u ON p.user_id = u.id
      LEFT JOIN likes l ON p.id = l.post_id AND l.user_id = ?
      WHERE p.user_id IN (
        SELECT CASE 
          WHEN user_id = ? THEN friend_id 
          WHEN friend_id = ? THEN user_id 
        END as friend_id
        FROM friends 
        WHERE (user_id = ? OR friend_id = ?) AND status = 'accepted'
        UNION
        SELECT ? as user_id
      )
      ORDER BY p.created_at DESC
      LIMIT ? OFFSET ?
    `, [req.user.id, req.user.id, req.user.id, req.user.id, req.user.id, req.user.id, parseInt(limit), parseInt(offset)]);

    res.json({
      success: true,
      data: posts
    });
  } catch (error) {
    console.error('Get feed error:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to get feed'
    });
  }
});

// Get user's posts
router.get('/user/:userId', optionalAuth, async (req, res) => {
  try {
    const { userId } = req.params;
    const { limit = 20, offset = 0 } = req.query;

    const posts = await executeQuery(`
      SELECT 
        p.*,
        u.username,
        u.full_name,
        u.profile_picture,
        CASE WHEN l.user_id IS NOT NULL THEN 1 ELSE 0 END as is_liked_by_user
      FROM posts p
      JOIN users u ON p.user_id = u.id
      LEFT JOIN likes l ON p.id = l.post_id AND l.user_id = ?
      WHERE p.user_id = ?
      ORDER BY p.created_at DESC
      LIMIT ? OFFSET ?
    `, [req.user ? req.user.id : null, userId, parseInt(limit), parseInt(offset)]);

    res.json({
      success: true,
      data: posts
    });
  } catch (error) {
    console.error('Get user posts error:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to get user posts'
    });
  }
});

// Get single post
router.get('/:id', optionalAuth, async (req, res) => {
  try {
    const { id } = req.params;

    const posts = await executeQuery(`
      SELECT 
        p.*,
        u.username,
        u.full_name,
        u.profile_picture,
        CASE WHEN l.user_id IS NOT NULL THEN 1 ELSE 0 END as is_liked_by_user
      FROM posts p
      JOIN users u ON p.user_id = u.id
      LEFT JOIN likes l ON p.id = l.post_id AND l.user_id = ?
      WHERE p.id = ?
    `, [req.user ? req.user.id : null, id]);

    if (posts.length === 0) {
      return res.status(404).json({
        success: false,
        message: 'Post not found'
      });
    }

    res.json({
      success: true,
      data: posts[0]
    });
  } catch (error) {
    console.error('Get post error:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to get post'
    });
  }
});

// Like/Unlike a post
router.post('/:id/like', authenticateToken, async (req, res) => {
  try {
    const { id } = req.params;
    const postId = parseInt(id);

    // Check if post exists
    const posts = await executeQuery('SELECT id FROM posts WHERE id = ?', [postId]);
    if (posts.length === 0) {
      return res.status(404).json({
        success: false,
        message: 'Post not found'
      });
    }

    // Check if already liked
    const existingLike = await executeQuery(
      'SELECT id FROM likes WHERE user_id = ? AND post_id = ?',
      [req.user.id, postId]
    );

    if (existingLike.length > 0) {
      // Unlike the post
      await executeQuery(
        'DELETE FROM likes WHERE user_id = ? AND post_id = ?',
        [req.user.id, postId]
      );

      // Update likes count
      await executeQuery(
        'UPDATE posts SET likes_count = likes_count - 1 WHERE id = ?',
        [postId]
      );

      res.json({
        success: true,
        message: 'Post unliked successfully',
        data: { liked: false }
      });
    } else {
      // Like the post
      await executeQuery(
        'INSERT INTO likes (user_id, post_id) VALUES (?, ?)',
        [req.user.id, postId]
      );

      // Update likes count
      await executeQuery(
        'UPDATE posts SET likes_count = likes_count + 1 WHERE id = ?',
        [postId]
      );

      res.json({
        success: true,
        message: 'Post liked successfully',
        data: { liked: true }
      });
    }
  } catch (error) {
    console.error('Like post error:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to like/unlike post'
    });
  }
});

// Add comment to a post
router.post('/:id/comments', authenticateToken, [
  body('content')
    .isLength({ min: 1, max: 500 })
    .withMessage('Comment must be between 1 and 500 characters')
], async (req, res) => {
  try {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({
        success: false,
        message: 'Validation failed',
        errors: errors.array()
      });
    }

    const { id } = req.params;
    const { content } = req.body;
    const postId = parseInt(id);

    // Check if post exists
    const posts = await executeQuery('SELECT id FROM posts WHERE id = ?', [postId]);
    if (posts.length === 0) {
      return res.status(404).json({
        success: false,
        message: 'Post not found'
      });
    }

    // Create comment
    const result = await executeQuery(
      'INSERT INTO comments (user_id, post_id, content) VALUES (?, ?, ?)',
      [req.user.id, postId, content]
    );

    // Update comments count
    await executeQuery(
      'UPDATE posts SET comments_count = comments_count + 1 WHERE id = ?',
      [postId]
    );

    // Get the created comment with user info
    const newComment = await executeQuery(`
      SELECT 
        c.*,
        u.username,
        u.full_name,
        u.profile_picture
      FROM comments c
      JOIN users u ON c.user_id = u.id
      WHERE c.id = ?
    `, [result.insertId]);

    res.status(201).json({
      success: true,
      message: 'Comment added successfully',
      data: newComment[0]
    });
  } catch (error) {
    console.error('Add comment error:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to add comment'
    });
  }
});

// Get comments for a post
router.get('/:id/comments', optionalAuth, async (req, res) => {
  try {
    const { id } = req.params;
    const { limit = 20, offset = 0 } = req.query;
    const postId = parseInt(id);

    // Check if post exists
    const posts = await executeQuery('SELECT id FROM posts WHERE id = ?', [postId]);
    if (posts.length === 0) {
      return res.status(404).json({
        success: false,
        message: 'Post not found'
      });
    }

    const comments = await executeQuery(`
      SELECT 
        c.*,
        u.username,
        u.full_name,
        u.profile_picture
      FROM comments c
      JOIN users u ON c.user_id = u.id
      WHERE c.post_id = ?
      ORDER BY c.created_at ASC
      LIMIT ? OFFSET ?
    `, [postId, parseInt(limit), parseInt(offset)]);

    res.json({
      success: true,
      data: comments
    });
  } catch (error) {
    console.error('Get comments error:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to get comments'
    });
  }
});

// Delete a post (only by the author)
router.delete('/:id', authenticateToken, async (req, res) => {
  try {
    const { id } = req.params;
    const postId = parseInt(id);

    // Check if post exists and user is the author
    const posts = await executeQuery(
      'SELECT id FROM posts WHERE id = ? AND user_id = ?',
      [postId, req.user.id]
    );

    if (posts.length === 0) {
      return res.status(404).json({
        success: false,
        message: 'Post not found or you are not authorized to delete it'
      });
    }

    // Delete the post (cascade will handle likes and comments)
    await executeQuery('DELETE FROM posts WHERE id = ?', [postId]);

    res.json({
      success: true,
      message: 'Post deleted successfully'
    });
  } catch (error) {
    console.error('Delete post error:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to delete post'
    });
  }
});

module.exports = router;

