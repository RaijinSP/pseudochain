package com.raijin.blockchain.mining;

import com.raijin.blockchain.storage.Block;
import com.raijin.blockchain.storage.Blockchain;

public class Miner extends Thread {

    private final Blockchain current;
    private final int blocks;

    public Miner (Blockchain bc, int blocks) {
        this.current = bc;
        this.blocks = blocks;
    }

    @Override
    public void run() {
        for (int i = 0; i < blocks; i++) {
            Block next;
            do {
                next = current.create();
            } while (!current.validateNew(next));
            current.add(next);
            int left = blocks - i;
        }
    }

    @Override
    public long getId() {
        return super.getId();
    }
}
