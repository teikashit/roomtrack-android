# RoomTrack Android App

A property management Android application built with Kotlin and Supabase.

## Features
- Register new user account
- Login with email and password
- Dashboard (Landlord and Tenant views)
- View and Edit Profile
- Update Profile information
- Change Password

## Tech Stack
- Language: Kotlin
- Framework: Android SDK
- API: Retrofit2
- Backend: Supabase REST API
- Authentication: Supabase Auth (Bearer Token)

## API Endpoints
| Feature | Method | Endpoint |
|---|---|---|
| Login | POST | /auth/v1/token?grant_type=password |
| Register | POST | /auth/v1/signup |
| Get Profile | GET | /rest/v1/profiles |
| Update Profile | POST | /rest/v1/profiles |
| Change Password | PUT | /auth/v1/user |
| Dashboard | GET | /rest/v1/profiles |

## Screenshots

### Login
(screenshot here)

### Register
(screenshot here)

### Dashboard
(screenshot here)

### Profile
(screenshot here)

### Update Profile
(screenshot here)

### Change Password
(screenshot here)
