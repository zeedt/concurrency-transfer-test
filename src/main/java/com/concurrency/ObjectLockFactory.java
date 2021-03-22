package com.concurrency;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class ObjectLockFactory {

    private static Map<ObjectLock, ObjectLock> map = new WeakHashMap<>(Collections.synchronizedMap(new HashMap<>()));

    public static synchronized ObjectLock getKey(String sender, String receiver) {
        ObjectLock senderLock = map.get(new ObjectLock(sender));
        ObjectLock receiverLock = map.get(new ObjectLock(receiver));

        if (senderLock == null && receiverLock == null) {
            senderLock = new ObjectLock(sender);
            receiverLock = new ObjectLock(receiver);
            map.put(senderLock, senderLock);
            map.put(receiverLock, receiverLock);
            return senderLock;
        } else if (senderLock == null && receiverLock != null) {
            senderLock = new ObjectLock(sender);
            map.put(senderLock, senderLock);
            return senderLock;
        } else if (senderLock != null && receiverLock == null) {
            receiverLock = new ObjectLock(receiver);
            map.put(receiverLock, receiverLock);
            return receiverLock;
        } else {
            return senderLock;
        }
    }

}
