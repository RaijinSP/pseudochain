package com.raijin.blockchain.messaging;

import com.raijin.blockchain.ObjFactory;
import com.raijin.blockchain.configutils.Inject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

public class AuthorFactory {

    private static final AuthorFactory factory = ObjFactory.createObject(AuthorFactory.class);

    @Inject("keypath")
    private String keyPathBase;

    private final KeyPairGenerator generator;

    public static AuthorFactory getFactory() {
        return factory;
    }

    private AuthorFactory() {
        try {
            this.generator = KeyPairGenerator.getInstance("RSA");
            this.generator.initialize(512);
        } catch (NoSuchAlgorithmException x) {
            throw new RuntimeException(x);
        }
    }

    public Author create(String username) throws IOException {

        KeyPair pair = generator.generateKeyPair();
        PrivateKey privateKey = pair.getPrivate();

        String keyPath = keyPathBase + username + ".key";

        writeKeyToFile(keyPath, privateKey.getEncoded());

        return new Author(username, pair.getPublic(), keyPath);
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
