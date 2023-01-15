package org.example.dao;

import org.example.model.Account;

public interface AccountDao {

    /**
     * 根据账号查询余额
     * @param accountName
     * @return
     */
    Account selectByAccountName(String accountName);

    /**
     * 更新账户
     * @param act
     * @return
     */
    int update(Account act);

}
