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
<img width="345" height="672" alt="login" src="https://github.com/user-attachments/assets/5f29e8bf-5de4-4cfb-af75-8fd2f9e7f393" />

### Register
<img width="357" height="673" alt="register" src="https://github.com/user-attachments/assets/526540f4-4148-40f5-9471-20683f64568e" />


### Dashboard
<img width="503" height="935" alt="dash" src="https://github.com/user-attachments/assets/f02bb406-d999-4457-92f4-6ea8b7dedd46" />

### Profile
<img width="463" height="914" alt="prof" src="https://github.com/user-attachments/assets/5667ce97-658d-4e50-a3f9-2f10830270be" />

### Update Profile
<img width="454" height="900" alt="update prof" src="https://github.com/user-attachments/assets/29fd0237-1525-48b8-9eb4-4f33cf608c83" />

### Change Password
<img width="476" height="917" alt="update pass" src="https://github.com/user-attachments/assets/76409e4b-850a-405b-ab03-1957f8e50b0d" />

