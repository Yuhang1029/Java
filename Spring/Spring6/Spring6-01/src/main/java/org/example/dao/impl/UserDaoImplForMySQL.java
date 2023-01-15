package org.example.dao.impl;

import org.example.dao.UserDao;

public class UserDaoImplForMySQL implements UserDao {
    @Override
    public void deleteById() {
        System.out.println("Delete Now!!!");
    }
}
