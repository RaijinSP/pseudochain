package com.raijin.blockchain.storage;



import com.raijin.blockchain.messaging.Author;
import com.raijin.blockchain.messaging.Message;
import com.raijin.blockchain.messaging.MessageFactory;

import java.util.ArrayList;
import java.util.List;

public class MessageClient {

    private final Author author;
    private final List<Message> messages = new ArrayList<>();

    MessageClient(Author author) {
        this.author = author;
    }

    protected void addMessage(String message) {
        try {
            Message m = MessageFactory.create(author.getName() + ": " + message, author.getKeyPath());
            if (m.getId() == messages.size())
                messages.add(m);
        } catch (Exception x) {
            System.err.println("Unable to create message...");
        }
    }

    public void verify() {
        List<Message> verified = new ArrayList<>();

        for (Message m: messages) {
            if (m.verifyMessage(author.getPk())) verified.add(m);
        }

        if (messages.size() != verified.size()) {
            messages.retainAll(verified);
        }
    }

    public void clear() {
        this.messages.clear();
    }

    public Author getAuthor() {
        return this.author;
    }

    public List<Message> getMessages() {
        return this.messages;
    }
}
