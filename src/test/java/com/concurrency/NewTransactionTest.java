package com.concurrency;

import com.concurrency.exception.TransferException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class NewTransactionTest {

    @RepeatedTest(1000)
    public void testCredit() throws TransferException, InterruptedException {

        BankAccountRepository bankAccountRepository = new BankAccountRepositoryStub();
        BankAccountServiceWithSyncTransferMethod bankAccountService = new BankAccountServiceWithSyncTransferMethod(bankAccountRepository);
//        BankAccountServiceWithSyncTransferObjectLock bankAccountService = new BankAccountServiceWithSyncTransferObjectLock(bankAccountRepository);
//        BankAccountService bankAccountService = new BankAccountService(bankAccountRepository);
        for (int i=0;i<10;i++) {
            BankAccount bankAccount = new BankAccount(100000d, "100000000"+i);
            bankAccountService.saveBankAccount(bankAccount);
        }

        ExecutorService executorService = Executors.newFixedThreadPool(100);
        IntStream.range(0,10000).boxed().parallel().forEach((i)->{
            executorService.submit(()-> {
                try {
                    bankAccountService.transfer("1000000001", "1000000002", 5);
                    bankAccountService.transfer("1000000009", "1000000008", 5);
                    bankAccountService.transfer("1000000008", "1000000009", 5);
                    bankAccountService.transfer("1000000003", "1000000004", 2);
                    bankAccountService.transfer("1000000003", "1000000005", 2);
                } catch (TransferException e) {
                    e.printStackTrace();
                }
            });
        });

        executorService.shutdown();
        executorService.awaitTermination(20000, TimeUnit.MILLISECONDS);

        BankAccount account1 = bankAccountService.getBankAccount("1000000001");
        BankAccount account2 = bankAccountService.getBankAccount("1000000002");
        Assertions.assertTrue(account1.getBalance()==50000, "Balance is " + account1.getBalance());
        Assertions.assertTrue(account2.getBalance()==150000);
        BankAccount account4 = bankAccountService.getBankAccount("1000000004");
        Assertions.assertTrue(account4.getBalance()==120000);
        BankAccount account5 = bankAccountService.getBankAccount("1000000005");
        Assertions.assertTrue(account5.getBalance()==120000);
        BankAccount account3 = bankAccountService.getBankAccount("1000000003");
        Assertions.assertTrue(account3.getBalance()==60000, "Balance is " + account1.getBalance());
        BankAccount account8 = bankAccountService.getBankAccount("1000000008");
        Assertions.assertTrue(account8.getBalance()==100000, "Balance is " + account8.getBalance());

    }

    private class BankAccountRepositoryStub implements BankAccountRepository {

        final HashMap<String, BankAccount> bankAccounts = new HashMap<>();

        @Override
        public boolean createBankAccount(BankAccount bankAccount) {
            bankAccounts.put(bankAccount.getAccountNumber(), bankAccount);
            return true;
        }

        @Override
        public BankAccount getAccount(String senderAccountNumber) {
            return bankAccounts.get(senderAccountNumber);
        }

        @Override
        public void updateBankAccount(BankAccount bankAccount) throws TransferException {
            if (bankAccounts.get(bankAccount.getAccountNumber())==null)
                throw new TransferException("Bank account does not exist");
            bankAccounts.put(bankAccount.getAccountNumber(), bankAccount);
        }
    }

}
