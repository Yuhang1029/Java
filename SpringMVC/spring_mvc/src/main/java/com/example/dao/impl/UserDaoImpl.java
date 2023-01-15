package com.example.dao.impl;

import com.example.dao.UserDao;
import com.example.model.User;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("userDao")
public class UserDaoImpl implements UserDao {

    @Resource(name = "jdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<User> getAllUsers() {
        String sql = "select name, age, city from user";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));
    }

    @Override
    public User getUserById(int id) {
        String sql = "select name, age, city from user where id = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), id);
    }

    @Override
    public User getUserByName(String name) {
        String sql = "select name, age, city from user where name = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), name);
    }

    @Override
    public List<User> findUsersInSameCity(String city) {
        String sql = "select name, age, city from user where city = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class), city);
    }
}
