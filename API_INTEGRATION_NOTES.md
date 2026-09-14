# API Integration Notes

This project is currently built with a Frontend-first approach using dummy data. To integrate with a real Backend API, follow the instructions below.

## Architecture Pattern
The app uses the **Repository Pattern** with Interfaces. All UI components (ViewModels) depend on interfaces, making it easy to swap dummy implementations with real API clients (e.g., Retrofit).

## Repositories to Implement
Implement the following interfaces in `com.example.datphongks.data.repository` using your API client:

### 1. HotelRepository
- `List<Hotel> getHotels()`: Should fetch all hotels or featured hotels.
- `Hotel getHotelById(String id)`: Fetch detailed info for a specific hotel.
- `List<Room> getRoomsForHotel(String hotelId)`: Fetch available room types for a hotel.
- `void toggleFavorite(String hotelId)`: Sync favorite status with the server.
- `List<Hotel> getFavoriteHotels()`: Fetch user's saved hotels.

### 2. BookingRepository
- `List<Booking> getBookingsByStatus(String status)`: Fetch user bookings filtered by status (Upcoming, Completed, Cancelled).
- `void createBooking(Booking booking)`: Submit a new booking to the server.

### 3. UserRepository
- `User getCurrentUser()`: Fetch current logged-in user profile.
- `void logout()`: Invalidate session on the server.

## Data Models
The models in `com.example.datphongks.data.models` (Hotel, Room, Booking, User) are designed to match standard JSON structures.
- **Dates**: Currently handled as Strings. Recommend using ISO 8601 format.
- **Prices**: Handled as integers (cents or whole currency units).
- **IDs**: Handled as Strings.

## Current Dummy Usage
- **Auth**: `AuthViewModel` uses local validation. Mock success is returned immediately.
- **Images**: Using `https://picsum.photos` for placeholders.
- **Search**: `SearchViewModel` filters a local list of 8 hotels.

## Steps to Integrate API
1. Add **Retrofit** or **Volley** to `build.gradle.kts`.
2. Create `ApiHotelRepositoryImpl` that implements `HotelRepository`.
3. In ViewModels, replace `new DummyHotelRepository()` with your new API implementation (ideally using Hilt/Dagger for DI).
