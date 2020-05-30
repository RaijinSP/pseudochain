package com.raijin.blockchain.transactions.currency;

public class VirtualCoin implements Coin {

    private final int quantity;

    public VirtualCoin(int quantity) {
        this.quantity = Math.abs(quantity);
    }

    @Override
    public int quantity() {
        return this.quantity;
    }

    @Override
    public String toString() {
        return quantity + " VC ";
    }
}
