<div align="center">
  <img src="path/to/your/logo.png" alt="InstaFoodies Logo" width="200"/>
  <h1>InstaFoodies: Social Media Platform for Food Enthusiasts</h1>
  <p>Welcome to InstaFoodies, where culinary creativity meets social networking!</p>
</div>

## 🍔 Introducing InstaFoodies

Discover and share mouthwatering recipes from around the world. Join a community of food enthusiasts, upload your culinary creations, and connect with fellow foodies.

## Table of Contents

1. [Introduction](#introduction)
2. [Features](#features)
3. [Architecture](#architecture)
4. [Modules](#modules)
   1. [User Authentication and Registration](#user-authentication-and-registration)
   2. [Post Creation and Editing](#post-creation-and-editing)
   3. [Feed Display and Navigation](#feed-display-and-navigation)
   4. [User Profile and Settings](#user-profile-and-settings)
   5. [Search Functionality](#search-functionality)
   6. [Notifications](#notifications)
   7. [Chat Messaging](#chat-messaging)
   8. [Content Recognition and Reporting](#content-recognition-and-reporting)
   9. [Recipe Web Scraping and Integration](#recipe-web-scraping-and-integration)
5. [Component Design](#component-design)
6. [Human Interface Design](#human-interface-design)
7. [Getting Started](#getting-started)
8. [Contributing](#contributing)
9. [Attribution](#attribution)
10. [License](#license)

## 🌟 Features

- 📸 Upload food photos with detailed recipe descriptions.
- 👥 Follow users and curate your personalized recipe feed.
- 🗓️ Plan your weekly recipes and export your cart as a WhatsApp message.
- 🔔 Receive real-time notifications for likes, comments, and follows.
- 💬 Chat with friends and fellow food lovers.
- 🔍 Search for users and recipes with ease.
- 🔍 Use content recognition to ensure the quality of shared recipes.
- 🔗 Seamlessly integrate scraped recipes from external websites.

## 🏗️ Architecture

InstaFoodies is built using the Model-View-ViewModel (MVVM) architectural pattern. This ensures clean code organization and easy maintenance.

- **Model:** Handles server communication and data logic.
- **View:** Presents the Android UI and user interface elements.
- **ViewModel:** Prepares data for the UI and contains UI-related logic.

## 🧩 Modules

### User Authentication and Registration

Create accounts securely with email, username, and password. Verify authentication via email confirmation.

### Post Creation and Editing

Craft and manage recipe posts with images, captions, ingredients, and cooking instructions.

### Feed Display and Navigation

Explore a chronological feed of posts, engage through likes, comments, and shares.

### User Profile and Settings

Customize profiles, including pictures and bios.

### Search Functionality

Discover users and content seamlessly.

### Notifications

Stay updated with real-time notifications.

### Chat Messaging

Engage in direct conversations with friends and foodies.

### Content Recognition and Reporting

Ensure recipe content quality with Firebase ML Kit's object detection.

### Recipe Web Scraping and Integration

Integrate diverse recipes from external websites into the app's database.


## 🌈 Human Interface Design

### Overview of User Interface

The user interface (UI) of the application is designed to be intuitive, user-friendly, and visually appealing.

It consists of various screens that promote seamless navigation and interaction. The main screens include:

- **Feed:** Displays a curated list of recipe posts from followed users.
- **Profile:** Shows the user's recipe posts and personal information.
- **Search:** Allows users to search for specific recipes or users.
- **Upload Update:** Provides a convenient way to upload and add new recipe posts.
- **Notifications:** Presents notifications for new posts or interactions.

### Screen Objects and Actions

1. **Entry Screen:**
   - Login Button: Initiates Google login action.

2. **Main Feed Screen:**
   - Recycler View: Displays a list of recipe post items.
   - Search Bar: Enables users to search for recipes.
   - Like Button: Allows users to like posts.
   - Comment Button: Allows users to comment on posts.
   - Weekly Plan Button: Helps users plan their recipes for the week.
   - Side Menu Button: Provides access to additional actions (e.g., profile search, add a post, apply as a chef, view other profiles).

3. **Full Details Post Screen:**
   - Text Views: Display recipe details.
   - WhatsApp Button: Shares recipe details via WhatsApp.

4. **Post Upload Screen:**
   - List View: Allows users to add ingredients.
   - Text Fields: Enable users to provide recipe directions, calories, images, etc.

5. **Profile Page Screen:**
   - Recipe Feed: Displays the user's recipe uploads.
   - Friends Count View: Shows the number of friends/followers.
   - Profile Picture View: Displays the user's profile picture.
   - Editable Profile Elements: Allows users to update profile details by clicking on them.

## 🚀 Getting Started

To get started with InstaFoodies, follow these steps:

1. Clone the repository: `git clone https://github.com/yourusername/InstaFoodies.git`
2. Open the project in Android Studio.
3. Build and run the app on an emulator or device.
4. Enjoy the world of culinary exploration and connection!

## 🌐 Contributing

We welcome contributions! For guidelines, please refer to [Contributing Guidelines](CONTRIBUTING.md).

## © Attribution

We respect copyright and intellectual property rights. Proper attribution to original sources is important.

## 📜 License

InstaFoodies is released under the [MIT License](LICENSE). You are free to use, modify, and distribute this software while respecting the terms of the license.

Join us on InstaFoodies, where the joy of cooking meets the power of social connection! 🍳📸🥗

