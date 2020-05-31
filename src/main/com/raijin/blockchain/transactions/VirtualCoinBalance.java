package com.raijin.blockchain.transactions;


import com.raijin.blockchain.transactions.currency.Coin;
import com.raijin.blockchain.transactions.currency.VirtualCoin;
import com.raijin.blockchain.transactions.exceptions.InvalidBalanceException;

public class VirtualCoinBalance implements Balance {

    private Coin balance;

    public VirtualCoinBalance() {
        this.balance = new VirtualCoin(0);
    }

    @Override
    public Coin getBalance() {
        return balance;
    }

    @Override
    public void increase(Coin diff) {
        balance = new VirtualCoin(balance.quantity() + diff.quantity());
    }

    @Override
    public void decrease(Coin diff) throws InvalidBalanceException {
        if (diff.quantity() > balance.quantity()) throw new InvalidBalanceException("Operation cannot be completed: not enough currency!");
        balance = new VirtualCoin(balance.quantity() - diff.quantity());
    }

    @Override
    public String toString() {
        return "VirtualCoinBalance{" +
                "balance=" + balance +
                '}';
    }
}