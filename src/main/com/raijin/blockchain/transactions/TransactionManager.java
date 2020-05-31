package com.raijin.blockchain.transactions;

import com.raijin.blockchain.transactions.currency.Coin;
import com.raijin.blockchain.transactions.exceptions.InvalidBalanceException;

public class TransactionManager {

    public static final TransactionManager MANAGER = new TransactionManager();

    public synchronized Transaction manageTransaction(Client sender, Client receiver, Coin diff) {
        Transaction transaction = new Transaction(sender, receiver, diff);

        try {
            transaction.apply();
        } catch (InvalidBalanceException e) {
            return null;
        }

        return transaction;
    }

}
