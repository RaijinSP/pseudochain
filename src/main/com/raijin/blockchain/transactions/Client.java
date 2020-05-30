package com.raijin.blockchain.transactions;

import java.security.PublicKey;

public class Client {

    private final String name;

    private final String keyPath;

    private final PublicKey pk;

    private final Balance balance;

    Client(String name, PublicKey pk, String keyPath) {
        this.name = name;
        this.pk = pk;
        this.keyPath = keyPath;
        this.balance = new VirtualCoinBalance();
    }

    public String getName() {
        return name;
    }

    public String getKeyPath() {
        return keyPath;
    }

    public PublicKey getPk() {
        return this.pk;
    }

    public Balance getBalance() {
        return this.balance;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Client)) return false;

        Client a = (Client) obj;

        return a.getName().equals(this.name);
    }
}
