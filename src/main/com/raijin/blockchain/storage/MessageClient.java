package com.raijin.blockchain.storage;


import com.raijin.blockchain.messaging.Message;
import com.raijin.blockchain.messaging.MessageFactory;
import com.raijin.blockchain.transactions.Client;
import com.raijin.blockchain.transactions.Transaction;
import com.raijin.blockchain.transactions.TransactionManager;
import com.raijin.blockchain.transactions.currency.Coin;
import com.raijin.blockchain.transactions.exceptions.TransactionException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MessageClient {

    private final Client client;



    private final List<Message> messages = new ArrayList<>();

    MessageClient(Client author) {
        this.client = author;
    }

    public List<Message> verifyAndClear() {

        List<Message> verified = new ArrayList<>();
        List<Message> current;

        synchronized (messages) {
            current = new ArrayList<>(messages);
            messages.clear();
        }

        for (Message m : current) {
            if (m.verifyMessage(client.getPk())) verified.add(m);
        }
        if (current.size() != verified.size()) {
            current.retainAll(verified);
        }
        return current;
    }

    public Client getClient() {
        return this.client;
    }

    public List<Message> getMessages() {
        return this.messages;
    }

    synchronized void performTransaction(Client receiver, Coin coin) throws TransactionException {

        Transaction transaction = TransactionManager.MANAGER.manageTransaction(client, receiver, coin);

        if (transaction == null) throw new TransactionException(String.format("Unable to perform transaction: low balance - + %d!", client.getBalance().getBalance().quantity()));
        try {
            Message message = MessageFactory.create(transaction, client.getKeyPath());
            messages.add(message);
            System.out.println("Transaction successfully submitted!");
            System.out.println(new String(message.getMessage().get(Message.ORIGINAL)));
        } catch (Exception x) {
            //TODO - enable transaction cancellation if we came here
            System.err.println("Unable to create message for transaction...");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageClient that = (MessageClient) o;
        return Objects.equals(client, that.client) &&
                Objects.equals(messages, that.messages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(client, messages);
    }
}
