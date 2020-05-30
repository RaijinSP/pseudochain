package com.raijin.blockchain.transactions.exceptions;

public class InvalidBalanceException extends Exception {

    public InvalidBalanceException(String message) {
        super(message);
    }
}
