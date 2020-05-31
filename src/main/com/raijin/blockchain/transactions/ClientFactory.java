package com.raijin.blockchain.transactions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.HashSet;
import java.util.Set;

public class ClientFactory {

    private static String keyPathBase;

    public static void setPath(String path) {
        if (keyPathBase == null) keyPathBase = path;
    }

    private static final ClientFactory factory = new ClientFactory();

    private final KeyPairGenerator generator;

    private final Set<Client> authors = new HashSet<>();

    public static ClientFactory getFactory() {
        return factory;
    }

    private ClientFactory() {
        try {
            this.generator = KeyPairGenerator.getInstance("RSA");
            this.generator.initialize(512);
        } catch (NoSuchAlgorithmException x) {
            throw new RuntimeException(x);
        }
    }

    public Client create(String username) throws IOException {

        KeyPair pair = generator.generateKeyPair();
        PrivateKey privateKey = pair.getPrivate();

        String keyPath = keyPathBase + username + ".key";

        writeKeyToFile(keyPath, privateKey.getEncoded());
        Client created = new Client(username, pair.getPublic(), keyPath);
        if (authors.add(created)) {
            return created;
        }
        System.err.println("Unable to add author - already exists!");
        return null;
    }

    private void writeKeyToFile(String path, byte[] key) throws IOException {
        File f = new File(path);
        f.getParentFile().mkdirs();

        FileOutputStream out = new FileOutputStream(f);
        out.write(key);
        out.flush();
        out.close();
    }
}
