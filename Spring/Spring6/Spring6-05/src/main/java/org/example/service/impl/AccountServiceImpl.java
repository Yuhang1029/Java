package org.example.service.impl;

import jakarta.annotation.Resource;
import org.example.dao.AccountDao;
import org.example.model.Account;
import org.example.service.AccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service("accountService")
public class AccountServiceImpl implements AccountService {
    @Resource(name = "accountDao")
    private AccountDao accountDao;

    @Override
    public void transfer(String fromName, String toName, int money) {
        // 查询账户余额是否充足
        Account fromAccount = accountDao.selectByAccountName(fromName);
        if (fromAccount.getBalance() < money) {
            throw new RuntimeException("账户余额不足");
        }

        // 余额充足，开始转账
        Account toAct = accountDao.selectByAccountName(toName);
        fromAccount.setBalance(fromAccount.getBalance() - money);
        toAct.setBalance(toAct.getBalance() + money);
        int count = accountDao.update(fromAccount);

        // 模拟异常
        String s = null;
        s.toString();
        count += accountDao.update(toAct);
        if (count != 2) {
            throw new RuntimeException("转账失败，请联系银行");
        }
    }


}
