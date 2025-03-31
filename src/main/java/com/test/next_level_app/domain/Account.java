package com.test.next_level_app.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Account {
    private String id;
    private int balance;

    public void deposit(int amount) {
        this.balance += amount;
    }

    public boolean withdraw(int amount) {
        if (balance >= amount) {
            this.balance -= amount;
            return true;
        }
        return false;
    }
}
