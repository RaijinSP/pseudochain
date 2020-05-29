package com.raijin.blockchain.storage;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

import static com.raijin.blockchain.utils.HashUtils.applySha256;

public class Block implements Serializable {

    private static final long serialVersionUID = -1512361671436L;

    private final long id;

    private static int zeros = 0;

    private final long minerId;

    private final long timeStamp;

    private final long generationTime;

    private final String prevHash;

    private String hash;

    private int magic;

    private String modZ;

    private List<byte[]> data;

    @Override
    public String toString() {
        Map<String, Object> obj = new HashMap<>();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field: fields) {
            field.setAccessible(true);
            try {
                obj.put(field.getName(), field.get(this));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return obj.toString() + "\n";
    }

    Block(long id, long minerId, long time, long generated, String prev, String hash, int magic, List<byte[]> data, int zeros) {
        this.id = id;
        this.minerId = minerId;
        this.timeStamp = time;
        this.generationTime = generated;
        this.prevHash = prev;
        this.hash = hash;
        this.magic = magic;
        this.data = data;
        Block.zeros = zeros;
    }

    public Block(long id, String prevHash) {
        long start = System.nanoTime();
        this.minerId = Thread.currentThread().getId();
        this.id = id;
        this.timeStamp = new Date().getTime();
        this.prevHash = prevHash;
        hash(zeros);
        generationTime = System.nanoTime() - start;
    }

    public long getId() {
        return this.id;
    }

    public String getPrevHash() {
        return this.prevHash;
    }

    public long timeStamp() {
        return this.timeStamp;
    }

    public String getHash() {
        return this.hash;
    }

    public int getMagic() {
        return this.magic;
    }

    public long getSeconds() {
        return generationTime/1_000_000_000;
    }

    public long getMillis() {
        return generationTime/1_000_000;
    }

    public long getGenerationTime() {
        return this.generationTime;
    }

    public long getMinerId() {
        return this.minerId;
    }

    public String getModZ() {
        return this.modZ;
    }

    public String getData() {
        if (data.isEmpty()) return "no messages";
        StringBuilder sb = new StringBuilder();
        data.forEach((bytes -> sb.append(new String(bytes)).append("\n")));
        return sb.toString();
    }

    public void setData(List<byte[]> messages) {
        this.data = messages;
    }

    void calcZeros(long timeStamp) {
        synchronized (Block.class) {
            if (timeStamp < 1000 + 1000 * zeros) {
                zeros++;
                modZ = "N was increased to " + zeros;
            } else if (timeStamp > 20000 + 1000 * zeros) {
                zeros--;
                modZ = "N was decreased to " + zeros;
            } else {
                modZ = "N stays the same";
            }
        }
    }

    private void hash(int zeros) {

        String hashLine = id + "_" + timeStamp + "_" + prevHash + "_";

        String start = zeros(zeros);
        magic = new Random().nextInt();
        while (!(hash = applySha256(hashLine + magic)).startsWith(start)) {
            magic = new Random().nextInt();
        }
    }

    private static String zeros(int zeros) {
        StringBuilder sb = new StringBuilder("");
        while (zeros > 0) {
            sb.append("0");
            zeros--;
        }
        return sb.toString();
    }
}
