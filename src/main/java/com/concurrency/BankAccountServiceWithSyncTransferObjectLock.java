package com.concurrency;

import com.concurrency.exception.TransferException;

public class BankAccountServiceWithSyncTransferObjectLock {
    private final BankAccountRepository bankAccountRepository;

    public BankAccountServiceWithSyncTransferObjectLock(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    public void saveBankAccount(BankAccount bankAccount) {
        this.bankAccountRepository.createBankAccount(bankAccount);
    }

    public void transfer(String senderAccountNumber, String receiverAccountNumber, int amount) throws TransferException {

        BankAccount senderBank = this.bankAccountRepository.getAccount(senderAccountNumber);
        BankAccount receiverBank = this.bankAccountRepository.getAccount(receiverAccountNumber);
        if (senderBank == null || receiverBank == null)
            return;
        synchronized (ObjectLockFactory.getKey(senderAccountNumber, receiverAccountNumber)) {
//        synchronized (ObjectLockFactory2.getKey(new ObjectLock2(senderAccountNumber, receiverAccountNumber))) {
                if (senderBank.getBalance() < amount) {
                    throw new TransferException("Insufficient Balance");
                }
                senderBank.setBalance(senderBank.getBalance() - amount);
                receiverBank.setBalance(receiverBank.getBalance() + amount);
                this.bankAccountRepository.updateBankAccount(senderBank);
                this.bankAccountRepository.updateBankAccount(receiverBank);
        }
    }

    public BankAccount getBankAccount(String accountNumber) throws TransferException {
        BankAccount bankAccount = bankAccountRepository.getAccount(accountNumber);
        if (bankAccount == null)
            throw new TransferException("Bank account not found");
        return bankAccount;
    }
}
