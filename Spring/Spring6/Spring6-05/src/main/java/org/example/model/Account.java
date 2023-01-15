package org.example.model;


public class Account {
    private String accountName;
    private int balance;

    public String getAccountName() {
        return accountName;
    }

    public int getBalance() {
        return balance;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountName='" + accountName + '\'' +
                ", balance=" + balance +
                '}';
    }
}
