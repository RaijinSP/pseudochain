package com.raijin.blockchain.messaging;

import java.security.PublicKey;

public class Author {

    private final String name;

    private final String keyPath;

    private final PublicKey pk;

    Author(String name, PublicKey pk, String keyPath) {
        this.name = name;
        this.pk = pk;
        this.keyPath = keyPath;
    }

    public String getName() {
        return name;
    }

    public String getKeyPath() {
        return keyPath;
    }

    public PublicKey getPk() {
        return this.pk;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Author)) return false;

        Author author = (Author) obj;

        return this.name.equals(author.getName()) &&
               this.keyPath.equals(author.getKeyPath()) &&
               this.pk.equals(author.getPk());
    }
}
