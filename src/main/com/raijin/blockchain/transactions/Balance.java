package com.raijin.blockchain.transactions;


import com.raijin.blockchain.transactions.currency.Coin;
import com.raijin.blockchain.transactions.exceptions.InvalidBalanceException;

public interface Balance {

    Coin getBalance();

    void increase(Coin diff);

    void decrease(Coin diff) throws InvalidBalanceException;

}
