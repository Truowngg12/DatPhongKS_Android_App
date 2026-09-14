package com.example.datphongks.data.models;

import java.util.List;
import java.util.Objects;

public class Hotel {
    private String id;
    private String name;
    private String location;
    private String imageUrl;
    private float rating;
    private int pricePerNight;
    private boolean isFavorite;
    private List<String> amenities;

    public Hotel(String id, String name, String location, String imageUrl, float rating, int pricePerNight, boolean isFavorite, List<String> amenities) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.pricePerNight = pricePerNight;
        this.isFavorite = isFavorite;
        this.amenities = amenities;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getImageUrl() { return imageUrl; }
    public float getRating() { return rating; }
    public int getPricePerNight() { return pricePerNight; }
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
    public List<String> getAmenities() { return amenities; }
}