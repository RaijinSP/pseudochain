package com.raijin.blockchain.messaging;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

public class Message {

    public static final int ORIGINAL = 0;
    public static final int ENCRYPTED = 1;
    private static final String ALGRTHM = "SHA1withRSA";

    private final long id;
    private final List<byte[]> list;

    Message(long id, String message, String keyFile) throws InvalidKeyException, Exception {
        this.id = id;
        list = new ArrayList<>();

        list.add(message.getBytes());
        list.add(sign(message, keyFile));
    }

    public byte[] sign(String data, String keyFile) throws InvalidKeyException, Exception {
        Signature rsa = Signature.getInstance(ALGRTHM);
        rsa.initSign(getPrivate(keyFile));
        rsa.update(data.getBytes(StandardCharsets.UTF_8));
        return rsa.sign();
    }

    public List<byte[]> getMessage() {
        return list;
    }

    public long getId() {
        return id;
    }

    public boolean verifyMessage(PublicKey key) {
        try {
            Signature sg = Signature.getInstance(ALGRTHM);
            sg.initVerify(key);
            sg.update(list.get(0));
            return sg.verify(list.get(1));
        } catch (Exception x) {
            System.err.println("Error occured while verification..." + x);
        }
        return false;
    }

    private PrivateKey getPrivate(String path) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] key = Files.readAllBytes(new File(path).toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(key);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePrivate(spec);
    }

}
