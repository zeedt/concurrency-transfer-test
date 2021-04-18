package com.concurrency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoughTest {

    public static void main (String[] args) {
        Map<BankAccount, BankAccount> map = new HashMap<>();
        BankAccount o1 = new BankAccount(5, "22222");
        map.put(o1, o1);
        List<String> strings = new ArrayList<>();
    }
}
