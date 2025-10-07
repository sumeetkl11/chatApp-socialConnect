const express = require('express');
const { body, validationResult } = require('express-validator');
const { executeQuery } = require('../utils/database');
const { authenticateToken } = require('../middleware/auth');

const router = express.Router();

// Send a message
router.post('/send', authenticateToken, [
  body('receiverId')
    .isInt({ min: 1 })
    .withMessage('Receiver ID must be a valid integer'),
  body('content')
    .isLength({ min: 1, max: 2000 })
    .withMessage('Message content must be between 1 and 2000 characters'),
  body('messageType')
    .optional()
    .isIn(['text', 'image', 'file'])
    .withMessage('Message type must be text, image, or file')
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

    const { receiverId, content, messageType = 'text' } = req.body;

    // Check if receiver exists
    const receivers = await executeQuery('SELECT id FROM users WHERE id = ?', [receiverId]);
    if (receivers.length === 0) {
      return res.status(404).json({
        success: false,
        message: 'Receiver not found'
      });
    }

    // Check if users are friends (optional - you can remove this for open messaging)
    const friendship = await executeQuery(
      'SELECT * FROM friends WHERE ((user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)) AND status = ?',
      [req.user.id, receiverId, receiverId, req.user.id, 'accepted']
    );

    // Uncomment the following lines if you want to restrict messaging to friends only
    // if (friendship.length === 0) {
    //   return res.status(403).json({
    //     success: false,
    //     message: 'You can only message your friends'
    //   });
    // }

    // Create message
    const result = await executeQuery(
      'INSERT INTO messages (sender_id, receiver_id, content, message_type) VALUES (?, ?, ?, ?)',
      [req.user.id, receiverId, content, messageType]
    );

    // Get the created message with sender info
    const newMessage = await executeQuery(`
      SELECT 
        m.*,
        u.username as sender_username,
        u.full_name as sender_full_name,
        u.profile_picture as sender_profile_picture
      FROM messages m
      JOIN users u ON m.sender_id = u.id
      WHERE m.id = ?
    `, [result.insertId]);

    res.status(201).json({
      success: true,
      message: 'Message sent successfully',
      data: newMessage[0]
    });
  } catch (error) {
    console.error('Send message error:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to send message'
    });
  }
});

// Get conversation between two users
router.get('/conversation/:userId', authenticateToken, async (req, res) => {
  try {
    const { userId } = req.params;
    const { limit = 50, offset = 0 } = req.query;
    const otherUserId = parseInt(userId);

    // Check if other user exists
    const users = await executeQuery('SELECT id FROM users WHERE id = ?', [otherUserId]);
    if (users.length === 0) {
      return res.status(404).json({
        success: false,
        message: 'User not found'
      });
    }

    const messages = await executeQuery(`
      SELECT 
        m.*,
        u.username as sender_username,
        u.full_name as sender_full_name,
        u.profile_picture as sender_profile_picture
      FROM messages m
      JOIN users u ON m.sender_id = u.id
      WHERE (m.sender_id = ? AND m.receiver_id = ?) OR (m.sender_id = ? AND m.receiver_id = ?)
      ORDER BY m.created_at DESC
      LIMIT ? OFFSET ?
    `, [req.user.id, otherUserId, otherUserId, req.user.id, parseInt(limit), parseInt(offset)]);

    // Mark messages as read
    await executeQuery(
      'UPDATE messages SET is_read = TRUE WHERE sender_id = ? AND receiver_id = ? AND is_read = FALSE',
      [otherUserId, req.user.id]
    );

    res.json({
      success: true,
      data: messages.reverse() // Reverse to show oldest first
    });
  } catch (error) {
    console.error('Get conversation error:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to get conversation'
    });
  }
});

// Get all conversations for current user
router.get('/conversations', authenticateToken, async (req, res) => {
  try {
    const { limit = 20, offset = 0 } = req.query;

    const conversations = await executeQuery(`
      SELECT 
        u.id as user_id,
        u.username,
        u.full_name,
        u.profile_picture,
        u.is_online,
        u.last_seen,
        m.content as last_message,
        m.created_at as last_message_time,
        m.message_type as last_message_type,
        m.is_read,
        m.sender_id as last_message_sender_id,
        COUNT(CASE WHEN m.is_read = FALSE AND m.sender_id != ? THEN 1 END) as unread_count
      FROM users u
      JOIN (
        SELECT DISTINCT 
          CASE 
            WHEN sender_id = ? THEN receiver_id 
            WHEN receiver_id = ? THEN sender_id 
          END as other_user_id
        FROM messages 
        WHERE sender_id = ? OR receiver_id = ?
      ) conv ON u.id = conv.other_user_id
      LEFT JOIN messages m ON (
        (m.sender_id = ? AND m.receiver_id = u.id) OR 
        (m.sender_id = u.id AND m.receiver_id = ?)
      ) AND m.created_at = (
        SELECT MAX(created_at) 
        FROM messages 
        WHERE (sender_id = ? AND receiver_id = u.id) OR (sender_id = u.id AND receiver_id = ?)
      )
      GROUP BY u.id, u.username, u.full_name, u.profile_picture, u.is_online, u.last_seen, 
               m.content, m.created_at, m.message_type, m.is_read, m.sender_id
      ORDER BY m.created_at DESC
      LIMIT ? OFFSET ?
    `, [
      req.user.id, req.user.id, req.user.id, req.user.id, req.user.id,
      req.user.id, req.user.id, req.user.id, req.user.id,
      parseInt(limit), parseInt(offset)
    ]);

    res.json({
      success: true,
      data: conversations
    });
  } catch (error) {
    console.error('Get conversations error:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to get conversations'
    });
  }
});

// Mark messages as read
router.put('/mark-read/:userId', authenticateToken, async (req, res) => {
  try {
    const { userId } = req.params;
    const senderId = parseInt(userId);

    const result = await executeQuery(
      'UPDATE messages SET is_read = TRUE WHERE sender_id = ? AND receiver_id = ? AND is_read = FALSE',
      [senderId, req.user.id]
    );

    res.json({
      success: true,
      message: 'Messages marked as read',
      data: { updatedCount: result.affectedRows }
    });
  } catch (error) {
    console.error('Mark messages as read error:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to mark messages as read'
    });
  }
});

// Get unread message count
router.get('/unread-count', authenticateToken, async (req, res) => {
  try {
    const result = await executeQuery(
      'SELECT COUNT(*) as unread_count FROM messages WHERE receiver_id = ? AND is_read = FALSE',
      [req.user.id]
    );

    res.json({
      success: true,
      data: { unreadCount: result[0].unread_count }
    });
  } catch (error) {
    console.error('Get unread count error:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to get unread count'
    });
  }
});

// Delete a message (only by the sender)
router.delete('/:id', authenticateToken, async (req, res) => {
  try {
    const { id } = req.params;
    const messageId = parseInt(id);

    // Check if message exists and user is the sender
    const messages = await executeQuery(
      'SELECT id FROM messages WHERE id = ? AND sender_id = ?',
      [messageId, req.user.id]
    );

    if (messages.length === 0) {
      return res.status(404).json({
        success: false,
        message: 'Message not found or you are not authorized to delete it'
      });
    }

    // Delete the message
    await executeQuery('DELETE FROM messages WHERE id = ?', [messageId]);

    res.json({
      success: true,
      message: 'Message deleted successfully'
    });
  } catch (error) {
    console.error('Delete message error:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to delete message'
    });
  }
});

// Search messages
router.get('/search', authenticateToken, async (req, res) => {
  try {
    const { q, userId, limit = 20, offset = 0 } = req.query;

    if (!q || q.trim().length < 2) {
      return res.status(400).json({
        success: false,
        message: 'Search query must be at least 2 characters long'
      });
    }

    const searchTerm = `%${q.trim()}%`;
    let query, params;

    if (userId) {
      // Search in specific conversation
      query = `
        SELECT 
          m.*,
          u.username as sender_username,
          u.full_name as sender_full_name,
          u.profile_picture as sender_profile_picture
        FROM messages m
        JOIN users u ON m.sender_id = u.id
        WHERE ((m.sender_id = ? AND m.receiver_id = ?) OR (m.sender_id = ? AND m.receiver_id = ?))
          AND m.content LIKE ?
        ORDER BY m.created_at DESC
        LIMIT ? OFFSET ?
      `;
      params = [req.user.id, userId, userId, req.user.id, searchTerm, parseInt(limit), parseInt(offset)];
    } else {
      // Search in all user's messages
      query = `
        SELECT 
          m.*,
          u.username as sender_username,
          u.full_name as sender_full_name,
          u.profile_picture as sender_profile_picture
        FROM messages m
        JOIN users u ON m.sender_id = u.id
        WHERE (m.sender_id = ? OR m.receiver_id = ?) AND m.content LIKE ?
        ORDER BY m.created_at DESC
        LIMIT ? OFFSET ?
      `;
      params = [req.user.id, req.user.id, searchTerm, parseInt(limit), parseInt(offset)];
    }

    const messages = await executeQuery(query, params);

    res.json({
      success: true,
      data: messages
    });
  } catch (error) {
    console.error('Search messages error:', error);
    res.status(500).json({
      success: false,
      message: 'Failed to search messages'
    });
  }
});

module.exports = router;

