package com.concurrency;

import org.junit.jupiter.api.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionServiceTest {

    @Test
    public void testAccountCredit() {
        Account account = new Account();
        account.setAccountNumber("10000011");
        account.creditAccount(5d);
        Assertions.assertTrue(account.getBalance()>0d, "Balance must be greater than 0");
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (int i=0;i<1000;i++) {
            executorService.execute(()->account.creditAccount(5d));
            executorService.execute(()->account.debitAccount(5d));
        }

        System.out.println("Balance is " + account.getBalance());
        executorService.shutdown();
        Assertions.assertTrue(account.getBalance() == 5d, "Balance must be 5");

//        Map<String, String> transactionHistory = Collections.synchronizedMap(new HashMap<String, String>());
    }

}
