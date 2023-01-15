package com.example.dao;

import com.example.model.User;

import java.util.List;

public interface UserDao {
    List<User> getAllUsers();
    User getUserById(int id);
    User getUserByName(String name);
    List<User> findUsersInSameCity(String city);
}
