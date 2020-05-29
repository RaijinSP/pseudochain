package com.raijin.blockchain.storage;

import com.raijin.blockchain.messaging.Author;
import com.raijin.blockchain.messaging.Message;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Blockchain {

    private static long lastBlockId;

    private static long nextBlockId;

    private static final BlockReader READER = new BlockReader();

    private static final Blockchain INSTANCE = new Blockchain();

    private final Map<Long, Block> blocks = new LinkedHashMap<>();

    private final Set<MessageClient> activeClients = new HashSet<>();

    public static Blockchain getInstance() {
        return INSTANCE;
    }

    private static boolean validateHash(String rhash, String lhash) {
        return rhash.equals(lhash);
    }

    private Blockchain() {
        lastBlockId = 1;
        nextBlockId = 1;
        //tryReadExisting();
    }

    private void tryReadExisting() {
        try {
            Set<Block> existing = READER.read();

            if (!existing.isEmpty()) {
                blocks.clear();
                blocks.putAll(existing.stream().collect(Collectors.toMap(Block::getId, Function.identity())));

                if (validate()) {
                    lastBlockId = blocks.keySet().stream().mapToLong(Long::longValue).max().getAsLong();
                    nextBlockId = lastBlockId + 1;
                }
            }

            if (!validate()) blocks.clear();

        } catch (Exception ignored) {
        }
    }

    private void writeNewBlock(Block b) throws IOException {
        READER.writeBlock(b);
    }

    public Block create() {
        String prevHash = blocks.isEmpty() ? "0" : blocks.get(lastBlockId).getHash();
        return new Block(nextBlockId, prevHash);
    }

    public synchronized void add(Block b) {
        //System.out.printf("THread %s adding block with id %d\n", Thread.currentThread().getName(), nextBlockId);
        verifySignature();
        b.setData(obtainBytesFromMessages());
        blocks.put(nextBlockId, b);
        b.calcZeros(b.getGenerationTime()/1_000_000);
        lastBlockId = nextBlockId;
        nextBlockId++;
        clearAllMessages();
//        try {
//            writeNewBlock(b);
//        } catch (Exception ignored) {
//        }
    }

    public boolean validate() {
        Block lastBlock = null;
        for (Block b: blocks.values()) {
            if (lastBlock == null) {
                if (!validateHash(b.getPrevHash(), "0")) return false;
            } else {
                if (!validateHash(b.getPrevHash(), lastBlock.getHash())) return false;
            }
            lastBlock = b;
        }
        return true;
    }

    public Collection<Block> blockchain() {
        return blocks.values();
    }

    public synchronized boolean validateNew(Block b) {
        String prevHash = blocks.isEmpty() ? "0" : blocks.get(lastBlockId).getHash();
        return b.getPrevHash().equals(prevHash);
    }

    public void addMessage(MessageClient cli, String message) {
        if (cli != null)
            cli.addMessage(message);
    }

    public MessageClient createClient(Author author) {
        MessageClient cli = new MessageClient(author);
        activeClients.add(cli);
        return cli;
    }

    private void verifySignature() {
        activeClients.forEach(MessageClient::verify);
    }

    private void clearAllMessages() {
        activeClients.forEach(MessageClient::clear);
    }

    private List<byte[]> obtainBytesFromMessages() {
        return activeClients.stream()
                .flatMap(cli -> cli.getMessages().stream())
                .map(m -> m.getMessage().get(Message.ORIGINAL))
                .collect(Collectors.toList());
    }

}