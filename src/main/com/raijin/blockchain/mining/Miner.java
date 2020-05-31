package com.raijin.blockchain.mining;

import com.raijin.blockchain.storage.Block;
import com.raijin.blockchain.storage.Blockchain;
import com.raijin.blockchain.transactions.Client;
import com.raijin.blockchain.transactions.ClientFactory;

import java.io.IOException;

public class Miner extends Thread {

    private final Blockchain current;
    private final Client client;
    private final int blocks;

    public Miner(Blockchain bc, int blocks, Client client) {
        this.current = bc;
        this.blocks = blocks;
        this.client = client;
    }

    public Miner(Blockchain bc, int blocks) throws IOException {
        this.current = bc;
        this.blocks = blocks;
        this.client = ClientFactory.getFactory().create(super.getName());
    }

    public Client getClient() {
        return this.client;
    }

    @Override
    public void run() {
        for (int i = 0; i < blocks; i++) {
            Block next;
            do {
                next = current.create();
            } while (!current.validateNew(next));
            current.add(next);

            current.reward(client);
        }
    }

    @Override
    public long getId() {
        return super.getId();
    }

    @Override
    public String toString() {
        return "Miner{" +
                "client=" + client.getName() +
                ", blocks=" + blocks +
                '}';
    }
}
