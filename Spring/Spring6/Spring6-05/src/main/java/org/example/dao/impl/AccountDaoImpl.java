package org.example.dao.impl;

import jakarta.annotation.Resource;
import org.example.dao.AccountDao;
import org.example.model.Account;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("accountDao")
public class AccountDaoImpl implements AccountDao {

    @Resource(name = "jdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Override
    public Account selectByAccountName(String actno) {
        String sql = "select account_name, balance from account where account_name = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Account.class), actno);
    }

    @Override
    public int update(Account act) {
        String sql = "update account set balance = ? where account_name = ?";
        return jdbcTemplate.update(sql, act.getBalance(), act.getAccountName());
    }
}
