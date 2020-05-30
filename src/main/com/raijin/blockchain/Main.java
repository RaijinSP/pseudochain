package com.raijin.blockchain;

import com.raijin.blockchain.mining.Miner;
import com.raijin.blockchain.storage.Block;
import com.raijin.blockchain.storage.Blockchain;
import com.raijin.blockchain.storage.MessageClient;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    /**
     *
     * How to execute:
     * 1. Get blockchain instance
     * 2. Create Miners and add them to thread pool or just starting as single threads
     * Optional:
     * 3. Create message clients (guys who will store data in blocks)
     * 4. no result will be visible until output will be finished
     */

    public static void main(String[] args) throws Exception {


    }
}
