package com.example.datphongks.data.models;

public class Booking {
    private String id;
    private Hotel hotel;
    private Room room;
    private String checkInDate;
    private String checkOutDate;
    private int totalPrice;
    private String status; // Upcoming, Completed, Cancelled

    public Booking(String id, Hotel hotel, Room room, String checkInDate, String checkOutDate, int totalPrice, String status) {
        this.id = id;
        this.hotel = hotel;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public String getId() { return id; }
    public Hotel getHotel() { return hotel; }
    public Room getRoom() { return room; }
    public String getCheckInDate() { return checkInDate; }
    public String getCheckOutDate() { return checkOutDate; }
    public int getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }
}