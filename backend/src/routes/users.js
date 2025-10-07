const express = require('express');
const { body, validationResult } = require('express-validator');
const { executeQuery } = require('../utils/database');
const { authenticateToken } = require('../middleware/auth');

const router = express.Router();

// Search users
router.get('/search', authenticateToken, async (req, res) => {
  try {
    const { q, limit = 20, offset = 0 } = req.query;

    if (!q || q.trim().length < 2) {
      return res.status(400).json({
        success: false,
        message: 'Search query must be at least 2 characters long'
      });
    }

    const searchTerm = `%${q.trim()}%`;
    
    const users = await executeQuery(`
      SELECT 
        u.id, 
        u.username, 
        u.full_name, 
        u.bio, 
        u.profile_picture,
        u.is_online,
        u.last_seen,
        f.status as friendship_status
      FROM users u
      LEFT JOIN friends f ON (
        (f.user_id = ? AND f.friend_id = u.id) OR 
        (f.friend_id = ? AND f.user_id = u.id)
      )
      WHERE u.id != ? 
        AND (u.username LIKE ? OR u.full_name LIKE ?)
      ORDER BY u.username
      LIMIT ? OFFSET ?
    `, [req.user.id, req.user.id, req.user.id, searchTerm, searchTerm, parseInt(limit), parseInt(offset)]);

    res.json({
      success: true,
      data: users
    });
  } catch (error) {
    console.error('Search users error:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to search users'
    });
  }
});

// Get user profile by ID
router.get('/:id', authenticateToken, async (req, res) => {
  try {
    const { id } = req.params;

    const users = await executeQuery(`
      SELECT 
        u.id, 
        u.username, 
        u.full_name, 
        u.bio, 
        u.profile_picture,
        u.is_online,
        u.last_seen,
        u.created_at,
        f.status as friendship_status
      FROM users u
      LEFT JOIN friends f ON (
        (f.user_id = ? AND f.friend_id = u.id) OR 
        (f.friend_id = ? AND f.user_id = u.id)
      )
      WHERE u.id = ?
    `, [req.user.id, req.user.id, id]);

    if (users.length === 0) {
      return res.status(404).json({
        success: false,
        message: 'User not found'
      });
    }

    res.json({
      success: true,
      data: users[0]
    });
  } catch (error) {
    console.error('Get user profile error:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to get user profile'
    });
  }
});

// Send friend request
router.post('/:id/friend-request', authenticateToken, async (req, res) => {
  try {
    const { id } = req.params;
    const friendId = parseInt(id);

    if (friendId === req.user.id) {
      return res.status(400).json({
        success: false,
        message: 'Cannot send friend request to yourself'
      });
    }

    // Check if user exists
    const users = await executeQuery('SELECT id FROM users WHERE id = ?', [friendId]);
    if (users.length === 0) {
      return res.status(404).json({
        success: false,
        message: 'User not found'
      });
    }

    // Check if friendship already exists
    const existingFriendship = await executeQuery(
      'SELECT * FROM friends WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)',
      [req.user.id, friendId, friendId, req.user.id]
    );

    if (existingFriendship.length > 0) {
      const friendship = existingFriendship[0];
      if (friendship.status === 'accepted') {
        return res.status(400).json({
          success: false,
          message: 'Already friends'
        });
      } else if (friendship.status === 'pending') {
        return res.status(400).json({
          success: false,
          message: 'Friend request already sent'
        });
      } else if (friendship.status === 'blocked') {
        return res.status(400).json({
          success: false,
          message: 'Cannot send friend request to blocked user'
        });
      }
    }

    // Create friend request
    await executeQuery(
      'INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, ?)',
      [req.user.id, friendId, 'pending']
    );

    res.json({
      success: true,
      message: 'Friend request sent successfully'
    });
  } catch (error) {
    console.error('Send friend request error:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to send friend request'
    });
  }
});

// Accept friend request
router.put('/:id/accept-friend', authenticateToken, async (req, res) => {
  try {
    const { id } = req.params;
    const friendId = parseInt(id);

    // Check if friend request exists
    const friendRequest = await executeQuery(
      'SELECT * FROM friends WHERE user_id = ? AND friend_id = ? AND status = ?',
      [friendId, req.user.id, 'pending']
    );

    if (friendRequest.length === 0) {
      return res.status(404).json({
        success: false,
        message: 'Friend request not found'
      });
    }

    // Accept the friend request
    await executeQuery(
      'UPDATE friends SET status = ? WHERE user_id = ? AND friend_id = ?',
      ['accepted', friendId, req.user.id]
    );

    res.json({
      success: true,
      message: 'Friend request accepted successfully'
    });
  } catch (error) {
    console.error('Accept friend request error:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to accept friend request'
    });
  }
});

// Reject friend request
router.delete('/:id/reject-friend', authenticateToken, async (req, res) => {
  try {
    const { id } = req.params;
    const friendId = parseInt(id);

    // Delete the friend request
    const result = await executeQuery(
      'DELETE FROM friends WHERE user_id = ? AND friend_id = ? AND status = ?',
      [friendId, req.user.id, 'pending']
    );

    if (result.affectedRows === 0) {
      return res.status(404).json({
        success: false,
        message: 'Friend request not found'
      });
    }

    res.json({
      success: true,
      message: 'Friend request rejected successfully'
    });
  } catch (error) {
    console.error('Reject friend request error:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to reject friend request'
    });
  }
});

// Remove friend
router.delete('/:id/unfriend', authenticateToken, async (req, res) => {
  try {
    const { id } = req.params;
    const friendId = parseInt(id);

    // Remove friendship (both directions)
    const result = await executeQuery(
      'DELETE FROM friends WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)',
      [req.user.id, friendId, friendId, req.user.id]
    );

    if (result.affectedRows === 0) {
      return res.status(404).json({
        success: false,
        message: 'Friendship not found'
      });
    }

    res.json({
      success: true,
      message: 'Friend removed successfully'
    });
  } catch (error) {
    console.error('Remove friend error:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to remove friend'
    });
  }
});

// Get friends list
router.get('/friends/list', authenticateToken, async (req, res) => {
  try {
    const { status = 'accepted', limit = 50, offset = 0 } = req.query;

    const friends = await executeQuery(`
      SELECT 
        u.id, 
        u.username, 
        u.full_name, 
        u.bio, 
        u.profile_picture,
        u.is_online,
        u.last_seen,
        f.created_at as friendship_date
      FROM friends f
      JOIN users u ON (
        CASE 
          WHEN f.user_id = ? THEN f.friend_id = u.id
          WHEN f.friend_id = ? THEN f.user_id = u.id
        END
      )
      WHERE (f.user_id = ? OR f.friend_id = ?) AND f.status = ?
      ORDER BY u.is_online DESC, u.last_seen DESC
      LIMIT ? OFFSET ?
    `, [req.user.id, req.user.id, req.user.id, req.user.id, status, parseInt(limit), parseInt(offset)]);

    res.json({
      success: true,
      data: friends
    });
  } catch (error) {
    console.error('Get friends list error:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to get friends list'
    });
  }
});

// Get friend requests (pending)
router.get('/friends/requests', authenticateToken, async (req, res) => {
  try {
    const { type = 'received', limit = 20, offset = 0 } = req.query;

    let query, params;

    if (type === 'received') {
      // Friend requests received by current user
      query = `
        SELECT 
          u.id, 
          u.username, 
          u.full_name, 
          u.bio, 
          u.profile_picture,
          u.is_online,
          u.last_seen,
          f.created_at as request_date
        FROM friends f
        JOIN users u ON f.user_id = u.id
        WHERE f.friend_id = ? AND f.status = ?
        ORDER BY f.created_at DESC
        LIMIT ? OFFSET ?
      `;
      params = [req.user.id, 'pending', parseInt(limit), parseInt(offset)];
    } else {
      // Friend requests sent by current user
      query = `
        SELECT 
          u.id, 
          u.username, 
          u.full_name, 
          u.bio, 
          u.profile_picture,
          u.is_online,
          u.last_seen,
          f.created_at as request_date
        FROM friends f
        JOIN users u ON f.friend_id = u.id
        WHERE f.user_id = ? AND f.status = ?
        ORDER BY f.created_at DESC
        LIMIT ? OFFSET ?
      `;
      params = [req.user.id, 'pending', parseInt(limit), parseInt(offset)];
    }

    const requests = await executeQuery(query, params);

    res.json({
      success: true,
      data: requests
    });
  } catch (error) {
    console.error('Get friend requests error:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to get friend requests'
    });
  }
});

module.exports = router;

