package com.raijin.blockchain.mining;


import com.raijin.blockchain.storage.Blockchain;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MiningService {

    private final ExecutorService service = Executors.newCachedThreadPool();

    public void addMiners(int num, int blocks) {
        for (int i = 0; i < num; i++) {
            service.submit(new Miner(Blockchain.getInstance(), blocks));
        }
    }

}
