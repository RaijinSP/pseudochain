package com.raijin.blockchain;

import com.raijin.blockchain.messaging.Author;
import com.raijin.blockchain.messaging.AuthorFactory;
import com.raijin.blockchain.storage.Blockchain;
import com.raijin.blockchain.storage.MessageClient;

public class Main {
    public static void main(String[] args) throws Exception {

        Blockchain bc = Blockchain.getInstance();

        Author tom = AuthorFactory.getFactory().create("Tom");

        Author jerry = AuthorFactory.getFactory().create("jerry");

        MessageClient tomCli = bc.createClient(tom);
        MessageClient jerryClo = bc.createClient(jerry);

    }
}
