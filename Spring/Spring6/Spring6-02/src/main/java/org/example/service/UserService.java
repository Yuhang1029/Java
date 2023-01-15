package org.example.service;

import org.example.dao.UserDao;

public class UserService {
    private UserDao userDao;

    public UserService(){}

    public void save() {
        userDao.insert();
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
}
