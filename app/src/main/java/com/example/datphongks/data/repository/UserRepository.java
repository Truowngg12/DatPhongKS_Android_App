package com.example.datphongks.data.repository;

import com.example.datphongks.data.models.User;

public interface UserRepository {
    User getCurrentUser();
    void logout();
}