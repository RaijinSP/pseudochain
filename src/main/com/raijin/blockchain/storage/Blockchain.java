package com.raijin.blockchain.storage;

import com.raijin.blockchain.messaging.Message;
import com.raijin.blockchain.transactions.Client;
import com.raijin.blockchain.transactions.currency.Coin;
import com.raijin.blockchain.transactions.currency.VirtualCoin;
import com.raijin.blockchain.transactions.exceptions.TransactionException;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Blockchain {

    private static long lastBlockId;

    private static long nextBlockId;

    private static final BlockReader READER = new BlockReader();

    private static final Blockchain INSTANCE = new Blockchain();

    private final Map<Long, Block> blocks = new LinkedHashMap<>();

    private final Map<Client, MessageClient> activeClients = new ConcurrentHashMap<>();

    public static Blockchain getInstance() {
        return INSTANCE;
    }

    private static boolean validateHash(String rhash, String lhash) {
        return rhash.equals(lhash);
    }

    private final Coin reward = new VirtualCoin(100);

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
        return new Block(prevHash);
    }

    public synchronized void add(Block b) {
        //System.out.printf("THread %s adding block with id %d\n", Thread.currentThread().getName(), nextBlockId);
        Set<MessageClient> currentClients = new HashSet<>(activeClients.values());

        b.setData(obtainBytesFromMessages(verifySignature(currentClients)));
        b.setId(nextBlockId);
        b.calcZeros(b.getGenerationTime() / 1_000_000);

        blocks.put(nextBlockId, b);
        lastBlockId = nextBlockId;
        nextBlockId++;

        //System.out.printf("Thread %s exiting...\n", Thread.currentThread().getName());
        //IOUtils.printBlockState(b);
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

    public synchronized void createClient(Client author) {
        MessageClient cli = new MessageClient(author);
        activeClients.put(author, cli);
    }

    public void executeTransaction(Client sender, Client receiver, int quantity) {
        MessageClient mc = activeClients.get(sender);
        try {
            mc.performTransaction(receiver, new VirtualCoin(quantity));
        } catch (TransactionException e) {
            System.err.println("Unable to perform transaction...");
        }
    }

    public void reward(Client cli) {
        System.out.println(cli.getName() + " gaines reward for mining!");
        cli.getBalance().increase(reward);
    }

    public void removeClient(Client cli) {
        activeClients.remove(cli);
    }

    private List<Message> verifySignature(Set<MessageClient> currentClients) {
        return currentClients.stream().flatMap(cli -> cli.verifyAndClear().stream()).collect(Collectors.toList());
    }

    private List<byte[]> obtainBytesFromMessages(List<Message> messages) {
        return messages.stream().map(m -> m.getMessage().get(Message.ORIGINAL))
                .collect(Collectors.toList());
    }
}