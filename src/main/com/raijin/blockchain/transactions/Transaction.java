package com.raijin.blockchain.transactions;

import com.raijin.blockchain.transactions.currency.Coin;
import com.raijin.blockchain.transactions.exceptions.InvalidBalanceException;

public class Transaction {

    private final Client sender;
    private final Client receiver;
    private final Coin coin;

    Transaction(Client sender, Client receiver, Coin coin) {
        this.sender = sender;
        this.receiver = receiver;
        this.coin = coin;
    }

    public void apply() throws InvalidBalanceException {
        sender.getBalance().decrease(coin);
        receiver.getBalance().increase(coin);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "sender=" + sender.getName() +
                ", receiver=" + receiver.getName() +
                ", coin=" + coin +
                '}';
    }
}
