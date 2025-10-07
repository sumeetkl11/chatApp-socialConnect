# 🚀 SocialConnect Setup Guide

This guide will help you set up and run the SocialConnect application on your local machine.

## 📋 Prerequisites

### Backend Requirements
- **Node.js** (version 16 or higher)
- **MySQL** (version 8.0 or higher)
- **npm** or **yarn** package manager

### Android Requirements
- **Android Studio** (latest version)
- **Android SDK** (API level 24 or higher)
- **Java Development Kit (JDK)** 8 or higher

## 🗄️ Database Setup

### 1. Install MySQL
- Download and install MySQL from [mysql.com](https://dev.mysql.com/downloads/mysql/)
- Start MySQL service
- Create a new database named `socialconnect`

### 2. Configure Database
```sql
CREATE DATABASE socialconnect;
USE socialconnect;
```

### 3. Update Environment Variables
1. Copy `backend/env.example` to `backend/.env`
2. Update the database credentials in `.env`:
```env
DB_HOST=localhost
DB_USER=root
DB_PASSWORD=your_mysql_password
DB_NAME=socialconnect
PORT=3000
JWT_SECRET=your_super_secret_jwt_key_here
```

## 🔧 Backend Setup

### 1. Navigate to Backend Directory
```bash
cd backend
```

### 2. Install Dependencies
```bash
npm install
```

### 3. Start the Server
```bash
# Development mode (with auto-restart)
npm run dev

# Production mode
npm start
```

The backend server will start on `http://localhost:3000`

### 4. Test the API
Visit `http://localhost:3000/api/health` in your browser to verify the server is running.

## 📱 Android Setup

### 1. Open Android Studio
- Launch Android Studio
- Select "Open an existing project"
- Navigate to the `android` folder and open it

### 2. Sync Project
- Android Studio will automatically sync the Gradle files
- Wait for the sync to complete

### 3. Configure Network Access
The app is configured to connect to `http://10.0.2.2:3000` (Android emulator) by default.

**For Physical Device:**
1. Find your computer's IP address
2. Update `ApiClient.kt` in `android/app/src/main/java/com/socialconnect/data/api/ApiClient.kt`
3. Change the `BASE_URL` to your computer's IP address:
```kotlin
private const val BASE_URL = "http://192.168.1.100:3000/api/" // Replace with your IP
```

### 4. Run the App
- Connect an Android device or start an emulator
- Click the "Run" button in Android Studio
- The app will install and launch on your device

## 🧪 Testing the Application

### 1. Backend API Testing
Use tools like Postman or curl to test the API endpoints:

```bash
# Health check
curl http://localhost:3000/api/health

# Register a new user
curl -X POST http://localhost:3000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "fullName": "Test User"
  }'
```

### 2. Android App Testing
1. Launch the app
2. Try registering a new account
3. Login with your credentials
4. Navigate through the different screens

## 🔧 Troubleshooting

### Backend Issues

**Database Connection Error:**
- Verify MySQL is running
- Check database credentials in `.env`
- Ensure the `socialconnect` database exists

**Port Already in Use:**
- Change the port in `.env` file
- Kill any process using port 3000

**Module Not Found:**
- Run `npm install` again
- Clear npm cache: `npm cache clean --force`

### Android Issues

**Build Errors:**
- Clean and rebuild the project
- Invalidate caches: File → Invalidate Caches and Restart
- Update Android Studio and SDK

**Network Connection Issues:**
- Check if backend server is running
- Verify IP address in `ApiClient.kt`
- Ensure device and computer are on the same network

**Emulator Issues:**
- Create a new AVD (Android Virtual Device)
- Use API level 24 or higher
- Enable network access in emulator settings

## 📊 Project Structure

```
SocialConnect/
├── backend/                 # Node.js backend
│   ├── src/
│   │   ├── controllers/     # API controllers
│   │   ├── models/         # Database models
│   │   ├── routes/         # API routes
│   │   ├── middleware/     # Authentication & validation
│   │   └── utils/          # Helper functions
│   ├── database/           # Database schema
│   └── package.json
├── android/                # Android app
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── java/com/socialconnect/
│   │   │   │   ├── ui/     # UI screens
│   │   │   │   ├── data/   # Data models & API
│   │   │   │   └── utils/  # Helper functions
│   │   │   └── res/        # Resources
│   │   └── build.gradle
│   └── build.gradle
└── docs/                   # Documentation
```

## 🚀 Next Steps

1. **Complete the UI Implementation**: Add more screens and functionality
2. **Implement Real-time Features**: Add Socket.IO integration for live messaging
3. **Add Image Upload**: Implement file upload for profile pictures and posts
4. **Add Push Notifications**: Integrate Firebase Cloud Messaging
5. **Add Offline Support**: Implement local data caching
6. **Add Testing**: Write unit and integration tests

## 📚 Additional Resources

- [Node.js Documentation](https://nodejs.org/docs/)
- [Express.js Guide](https://expressjs.com/)
- [MySQL Documentation](https://dev.mysql.com/doc/)
- [Android Development Guide](https://developer.android.com/guide)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Retrofit Documentation](https://square.github.io/retrofit/)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

**Happy Coding! 🎉**

If you encounter any issues, please check the troubleshooting section or create an issue in the repository.

