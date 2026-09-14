package com.example.datphongks.data.repository;

import com.example.datphongks.data.models.Booking;
import java.util.List;

public interface BookingRepository {
    List<Booking> getBookingsByStatus(String status);
    void createBooking(Booking booking);
}