package com.concurrency;

import com.concurrency.exception.TransferException;

public interface BankAccountRepository {
    boolean createBankAccount(BankAccount bankAccount);

    BankAccount getAccount(String senderAccountNumber);

    void updateBankAccount(BankAccount bankAccount) throws TransferException;
}
