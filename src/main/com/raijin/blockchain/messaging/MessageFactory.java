package com.raijin.blockchain.messaging;

public class MessageFactory {
    public static synchronized Message create(String message, String keyPath) throws Exception {
        return new Message(message, keyPath);
    }
}
