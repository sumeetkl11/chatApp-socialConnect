# 💬 SocialConnect

**A Social & Messaging App Using Kotlin, Node.js, and MySQL**

## 🎯 Project Overview

SocialConnect is a modern social networking and real-time messaging application designed to help users connect, chat, share posts, and stay in touch. Built with a secure backend powered by MySQL and Node.js, featuring a sleek mobile interface developed in Kotlin (Android).

## 🏗️ Project Structure

```
SocialConnect/
├── backend/                 # Node.js + Express backend
│   ├── src/
│   │   ├── controllers/     # API controllers
│   │   ├── models/         # Database models
│   │   ├── routes/         # API routes
│   │   ├── middleware/     # Authentication & validation
│   │   └── utils/          # Helper functions
│   ├── database/           # Database schema & migrations
│   └── package.json
├── android/                # Android app (Kotlin + Jetpack Compose)
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── java/com/socialconnect/
│   │   │   │   ├── ui/     # UI screens
│   │   │   │   ├── data/   # Data models & API
│   │   │   │   ├── utils/  # Helper functions
│   │   │   │   └── MainActivity.kt
│   │   │   └── res/        # Resources
│   │   └── build.gradle
│   └── build.gradle
└── docs/                   # Documentation
    ├── database-schema.md
    ├── api-documentation.md
    └── ui-mockups.md
```

## 🚀 Quick Start

### Backend Setup
```bash
cd backend
npm install
npm run dev
```

### Android Setup
1. Open Android Studio
2. Import the `android` folder
3. Sync Gradle files
4. Run the app

## 📱 Features

- ✅ User Authentication (Register/Login)
- ✅ Real-time Messaging
- ✅ Social Posts (Create, Like, Comment)
- ✅ Friend System
- ✅ Profile Management
- ✅ Modern UI/UX with Jetpack Compose

## 🛠️ Tech Stack

- **Backend**: Node.js, Express.js, MySQL, Socket.IO
- **Frontend**: Kotlin, Jetpack Compose, Retrofit, Material Design 3
- **Database**: MySQL with proper relationships
- **Real-time**: Socket.IO for instant messaging

## 📊 Development Progress

- [x] Project structure setup
- [ ] Database schema design
- [ ] Backend API development
- [ ] Android app development
- [ ] Real-time features
- [ ] Testing & polish

