package com.adanilov.oms.domain;

import java.util.UUID;

public class Quote {
    public String quoteId;

    public Quote(int price, int volume, Side side) {
        this.quoteId = UUID.randomUUID().toString();
    }

    public Quote() {
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Quote{");
        sb.append("quoteId='").append(quoteId).append('\'');
        return sb.toString();
    }
}
