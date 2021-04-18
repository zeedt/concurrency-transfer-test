package com.concurrency;

import java.util.Objects;

public class ObjectLock2 {

    private String sender;
    private String  receiver;

    public ObjectLock2(String sender, String receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectLock2 that = (ObjectLock2) o;
        return Objects.equals(sender, that.sender) || Objects.equals(receiver, that.receiver)
                || Objects.equals(receiver, that.sender) || Objects.equals(sender, that.receiver);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender);
    }
}
