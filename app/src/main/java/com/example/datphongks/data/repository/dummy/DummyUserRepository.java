package com.example.datphongks.data.repository.dummy;

import com.example.datphongks.data.models.User;
import com.example.datphongks.data.repository.UserRepository;

public class DummyUserRepository implements UserRepository {

    @Override
    public User getCurrentUser() {
        return new User("u1", "John Doe", "john.doe@example.com", "https://i.pravatar.cc/150?u=u1");
    }

    @Override
    public void logout() {
        // Mock logout
    }
}