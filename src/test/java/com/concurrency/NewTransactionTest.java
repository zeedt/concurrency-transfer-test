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

    @RepeatedTest(500)
    public void testCredit() throws TransferException, InterruptedException {

        BankAccountRepository bankAccountRepository = new BankAccountRepositoryStub();
        BankAccountService bankAccountService = new BankAccountService(bankAccountRepository);
        for (int i=0;i<10;i++) {
            BankAccount bankAccount = new BankAccount();
            bankAccount.setAccountNumber("100000000"+i);
            bankAccount.setCurrentBalance(10000d);
            bankAccountService.saveBankAccount(bankAccount);
        }

        ExecutorService executorService = Executors.newFixedThreadPool(100);

        IntStream.range(0,1000).boxed().parallel().forEach((i)->{
            executorService.submit(()-> {
                try {
                    bankAccountService.transfer("1000000001", "1000000002", 5);
                    bankAccountService.transfer("1000000003", "1000000004", 2);
                    bankAccountService.transfer("1000000003", "1000000005", 2);
                } catch (TransferException e) {
                    e.printStackTrace();
                }
            });
        });

        executorService.shutdown();
        executorService.awaitTermination(2000, TimeUnit.MILLISECONDS);

        BankAccount sender = bankAccountService.getBankAccount("1000000001");
        BankAccount receiver = bankAccountService.getBankAccount("1000000002");
        Assertions.assertTrue(sender.getBalance()==5000, "Balance is " + sender.getBalance());
        Assertions.assertTrue(receiver.getBalance()==15000);
        BankAccount receiver3 = bankAccountService.getBankAccount("1000000004");
        Assertions.assertTrue(receiver3.getBalance()==12000);
        BankAccount receiver4 = bankAccountService.getBankAccount("1000000005");
        Assertions.assertTrue(receiver4.getBalance()==12000);
        BankAccount sender2 = bankAccountService.getBankAccount("1000000003");
        Assertions.assertTrue(sender2.getBalance()==6000, "Balance is " + sender.getBalance());


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


    @Test
    public void testMap() {
        HashMap<ObjectLock, ObjectLock> map = new HashMap<>();
        ObjectLock objectLock = new ObjectLock("1000000000");
        map.put(objectLock, objectLock);
        ObjectLock objectLock2 = new ObjectLock("1000000000");
        Assertions.assertTrue(map.get(objectLock2) != null);
        Assertions.assertTrue(map.get(objectLock) != null);
    }

}
