package com.raijin.blockchain.utils;


import com.raijin.blockchain.storage.Block;

public class IOUtils {

    public static void printBlockState(Block b) {
        System.out.printf("Created by miner # %d\n", b.getMinerId());
        System.out.printf("Id: %d\n", b.getId());
        System.out.printf("Timestamp: %d\n", b.timeStamp());
        System.out.printf("Magic number: %d\n", b.getMagic());
        System.out.printf("Hash of the previous block:\n%s\n", b.getPrevHash());
        System.out.printf("Hash of the block:\n%s\n", b.getHash());
        System.out.printf("Block data:\n%s", b.getData());
        System.out.println("Block was generating for " + b.getSeconds() + " seconds");
        System.out.println(b.getModZ() + "\n");
    }

}
