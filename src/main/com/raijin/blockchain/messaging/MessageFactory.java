package com.raijin.blockchain.messaging;

import com.raijin.blockchain.transactions.Transaction;

public class MessageFactory {

    public static Message create(String message, String keyPath) throws Exception {
        return new Message(message, keyPath);
    }

    public static Message create(Transaction transaction, String keyPath) throws Exception {
        return create(transaction.toString(), keyPath);
    }

}
