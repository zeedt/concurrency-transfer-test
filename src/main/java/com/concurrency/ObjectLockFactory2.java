package com.concurrency;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class ObjectLockFactory2 {

    private static Map<ObjectLock2, ObjectLock2> map = new WeakHashMap<>(Collections.synchronizedMap(new HashMap<>()));

    public static synchronized ObjectLock2 getKey(ObjectLock2 objectLock2) {
        ObjectLock2 lock2 = map.get(objectLock2);
        if (lock2 == null) {
            lock2 = objectLock2;
            map.put(lock2, lock2);
        }
        return lock2;
    }

}
