package com.example.datphongks.data.models;

public class Room {
    private String id;
    private String hotelId;
    private String type;
    private String description;
    private int pricePerNight;
    private int capacity;
    private String imageUrl;

    public Room(String id, String hotelId, String type, String description, int pricePerNight, int capacity, String imageUrl) {
        this.id = id;
        this.hotelId = hotelId;
        this.type = type;
        this.description = description;
        this.pricePerNight = pricePerNight;
        this.capacity = capacity;
        this.imageUrl = imageUrl;
    }

    public String getId() { return id; }
    public String getHotelId() { return hotelId; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public int getPricePerNight() { return pricePerNight; }
    public int getCapacity() { return capacity; }
    public String getImageUrl() { return imageUrl; }
}