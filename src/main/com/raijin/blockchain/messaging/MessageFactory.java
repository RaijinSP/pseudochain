package com.raijin.blockchain.messaging;

import java.util.concurrent.atomic.AtomicLong;

public class MessageFactory {

    private static final AtomicLong id = new AtomicLong(1);

    public static Message create(String message, String keyPath) throws Exception {
        return new Message(id.incrementAndGet(), message, keyPath);
    }

}
