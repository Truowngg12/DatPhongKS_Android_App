package com.example.datphongks.data.repository.dummy;

import com.example.datphongks.data.models.Booking;
import com.example.datphongks.data.models.Hotel;
import com.example.datphongks.data.models.Room;
import com.example.datphongks.data.repository.BookingRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DummyBookingRepository implements BookingRepository {

    private List<Booking> bookings = new ArrayList<>();

    public DummyBookingRepository() {
        // Sample data
        Hotel hotel = new Hotel("1", "Khách sạn Grand Riviera", "Hà Nội, Việt Nam", "https://picsum.photos/seed/1/800/600", 4.9f, 500000, true, Arrays.asList("Hồ bơi", "Spa"));
        Room room = new Room("r1", "1", "Phòng Cao Cấp", "Phòng rộng rãi sang trọng", 500000, 2, "");
        bookings.add(new Booking("b1", hotel, room, "10/09/2026", "12/09/2026", 1000000, "Upcoming"));
    }

    @Override
    public List<Booking> getBookingsByStatus(String status) {
        List<Booking> result = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking.getStatus().equalsIgnoreCase(status)) {
                result.add(booking);
            }
        }
        return result;
    }

    @Override
    public void createBooking(Booking booking) {
        bookings.add(booking);
    }
}