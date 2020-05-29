package com.raijin.blockchain;

import com.raijin.blockchain.messaging.Author;
import com.raijin.blockchain.messaging.AuthorFactory;
import com.raijin.blockchain.mining.Miner;
import com.raijin.blockchain.storage.Block;
import com.raijin.blockchain.storage.Blockchain;
import com.raijin.blockchain.storage.MessageClient;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
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

        Blockchain bc = Blockchain.getInstance();

        ExecutorService srv = Executors.newCachedThreadPool();

        int blocks = 5;

        for (int i = 0; i < 2; i++) {
            srv.submit(new Miner(bc, blocks));
        }
        Thread.sleep(1000);

        MessageClient mc0 = bc.createClient(AuthorFactory.getFactory().create("Tom"));
        MessageClient mc1 = bc.createClient(AuthorFactory.getFactory().create("Anna"));

        for (int i = 0; i < 10; i++) {
            bc.addMessage(mc0, "test message!!");
            Thread.sleep(2000);
            bc.addMessage(mc1, "another test message!!!");
            Thread.sleep(new Random().nextInt(4000));
        }

        srv.awaitTermination(120, TimeUnit.SECONDS);

        //int count = 5;
        for (Block b: bc.blockchain()) {
            //System.out.println(b);
            //if (count == 0) break;
            System.out.println("Block:");
            printBlockState(b);

            //count--;

        }

    }

    private static void printBlockState(Block b) {
        System.out.printf("Created by miner # %d\n", b.getMinerId());
        System.out.printf("Id: %d\n", b.getId());
        System.out.printf("Timestamp: %d\n", b.timeStamp());
        System.out.printf("Magic number: %d\n", b.getMagic());
        System.out.printf("Hash of the previous block:\n%s\n", b.getPrevHash());
        System.out.printf("Hash of the block:\n%s\n", b.getHash());
        System.out.printf("Block data: %s\n", b.getData());
        System.out.println("Block was generating for " + b.getSeconds() + " seconds");
        System.out.println(b.getModZ() + "\n");
    }
}
