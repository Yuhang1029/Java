package org.example.service.impl;

import org.example.dao.UserDao;
import org.example.dao.impl.UserDaoImplForMySQL;
import org.example.service.UserService;

public class UserServiceImpl implements UserService {
    private UserDao userDao = new UserDaoImplForMySQL();

    @Override
    public void deleteUser() {
        userDao.deleteById();
    }
}
