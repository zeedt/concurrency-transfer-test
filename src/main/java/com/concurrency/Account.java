package com.concurrency;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Account {
    final ReentrantReadWriteLock reentrantLock = new ReentrantReadWriteLock();
    private String accountNumber;
    private double balance;

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void creditAccount(double amount) {
        reentrantLock.writeLock().lock();
        try {
            this.balance = this.balance + amount;
        } finally {
            reentrantLock.writeLock().unlock();
        }
    }

    public double getBalance() {
        reentrantLock.readLock().lock();
        try {
            return this.balance;
        } finally {
            reentrantLock.readLock().unlock();
        }
    }

    public void debitAccount(double amount) {
        reentrantLock.writeLock().lock();
        try {
            this.balance = this.balance - amount;
        } finally {
            reentrantLock.writeLock().unlock();
        }
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}
