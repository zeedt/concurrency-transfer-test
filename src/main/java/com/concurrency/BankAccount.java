package com.concurrency;

import java.util.Objects;

public class BankAccount {

    private double balance;
    private String accountNumber;

    public BankAccount(double balance, String accountNumber) {
        this.balance = balance;
        this.accountNumber = accountNumber;
    }

    public void setCurrentBalance(double amount) {
        this.balance = amount;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    @Override
    public boolean equals(Object o) {
        BankAccount that = (BankAccount) o;
        return Objects.equals(accountNumber, that.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }

    public synchronized void creditAccount(double amount) {
        this.balance = this.balance + amount;
    }

    public void initiateTransfer(double amount, BankAccount accountToCredit) {
        synchronized (this) {
            if (this.balance < amount) {
                return;
            }
            this.balance = this.balance - amount;
        }
        accountToCredit.creditAccount(amount);
    }
}
