package org.example.service;

public interface AccountService {
    /**
     * 转账
     * @param fromName
     * @param toName
     * @param money
     */
    void transfer(String fromName, String toName, int money);
}
