package com.test.next_level_app.application;

import com.test.next_level_app.domain.Account;
import com.test.next_level_app.util.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AccountService {

    private final Map<String, Account> accounts = new HashMap<>();

    public Map<String, Object> deposit(String destination, int amount) {
        Account account = accounts.get(destination);
        if (account == null) {
            account = new Account(destination, amount);
        } else {
            account.deposit(amount);
        }
        accounts.put(destination, account);
        return Map.of(Constants.DESTINATION, Map.of(Constants.ID, account.getId(), Constants.BALANCE, account.getBalance()));
    }

    public Map<String, Object> withdraw(String origin, int amount) {
        Account account = accounts.get(origin);
        if (account == null || !account.withdraw(amount)) {
            return null;
        }
        accounts.put(origin, account);
        return Map.of(Constants.ORIGIN, Map.of(Constants.ID, account.getId(), Constants.BALANCE, account.getBalance()));
    }

    public Map<String, Object> transfer(String origin, String destination, int amount) {
        Account originAccount = accounts.get(origin);
        Account destinationAccount = accounts.get(destination);

        if (originAccount == null || !originAccount.withdraw(amount)) {
            return null;
        }

        if (destinationAccount == null) {
            destinationAccount = new Account(destination, 0);
            accounts.put(destination, destinationAccount);
        }

        destinationAccount.deposit(amount);

        accounts.put(origin, originAccount);
        accounts.put(destination, destinationAccount);

        return Map.of(
                Constants.ORIGIN, Map.of(Constants.ID, originAccount.getId(), Constants.BALANCE, originAccount.getBalance()),
                Constants.DESTINATION, Map.of(Constants.ID, destinationAccount.getId(), Constants.BALANCE, destinationAccount.getBalance())
        );
    }

    public Optional<Account> getAccount(String accountId) {
        return Optional.ofNullable(accounts.get(accountId));
    }

    public void reset() {
        accounts.clear();
    }
}
