package com.raijin.blockchain.storage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class BlockReader {

    private static final String PATH = "C:\\Users\\raiji\\IdeaProjects\\Blockchain\\Blockchain\\task\\resources\\blockchain";

    public static final boolean EXISTS = true;

/*    private static String getFilePath() {
        ClassLoader cl = BlockReader.class.getClassLoader();
        String path = cl.getResourceAsStream(PATH);
        return path;
    }*/

    /*static {
        try {
            File file = new File(PATH);
            EXISTS = file.exists();
            System.out.println(EXISTS);
            if (!EXISTS) {
                file.createNewFile();
            }
        } catch (IOException x) {
            throw new RuntimeException(String.format("Unable to create blockchain storage using path: %s.", PATH));
        }
    }*/


    BlockReader() {
    }

    Set<Block> read() throws Exception {
        return read(PATH);
    }

    public Set<Block> read(String path) throws Exception {
        Boolean b = new Boolean("T");
        String[] blockExpr = new String(Files.readAllBytes(Paths.get(path))).split("\n");

        Set<Block> blockSet = new LinkedHashSet<>();



        for (String blockStr: blockExpr) {

            Map<String, String> fieldMap = map(blockStr);

            long id;

            long timeStamp;

            long generationTime;

            long minerId;

            String prevHash;

            String hash;

            int magic;

            int zeros;

            String data;

            try {
                id = Long.parseLong(fieldMap.get("id"));
                minerId = Long.parseLong(fieldMap.get("minerId"));
                timeStamp = Long.parseLong(fieldMap.get("timeStamp"));
                generationTime = Long.parseLong(fieldMap.get("generationTime"));
                prevHash = fieldMap.get("prevHash");
                hash = fieldMap.get("hash");
                magic = Integer.parseInt(fieldMap.get("magic"));
                zeros = Integer.parseInt(fieldMap.get("zeros"));
                data = fieldMap.get("data");
            } catch (Exception x) {
                throw new Exception("Unable to read blockchain from file, creating a new one...");
            }

            //blockSet.add(new Block(id, minerId, timeStamp, generationTime, prevHash, hash, magic, data, zeros));
        }

        return blockSet;
    }

    private Map<String, String> map(String row) {

        String[] fieldExp = row.replace("{", "").replace("}", "").split(", ");

        Map<String, String> objMap = new HashMap<>();

        for (String f: fieldExp) {
            String[] exp = f.split("=");
            String name = exp[0];
            String val = exp[1];
            objMap.put(name, val);
        }

        return objMap;
    }

    void writeBlock(Block b) throws IOException {
        writeBlock(b, PATH);
    }

    public void writeBlock(Block b, String path) throws IOException {
        Files.write(Paths.get(path), b.toString().getBytes(), StandardOpenOption.APPEND);
    }
}
