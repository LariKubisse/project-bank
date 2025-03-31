package com.test.next_level_app.controller;


import com.test.next_level_app.application.AccountService;
import com.test.next_level_app.util.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class AccountController {

    private final AccountService accountService = new AccountService();

    @PostMapping("/reset")
    public ResponseEntity<Void> reset() {
        try {
            accountService.reset();
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/balance")
    public ResponseEntity<Integer> getBalance(@RequestParam("account_id") String accountId) {
        try {
            return accountService.getAccount(accountId)
                    .map(account -> ResponseEntity.ok(account.getBalance()))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(0));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/event")
    public ResponseEntity<Map<String, Object>> handleEvent(@RequestBody Map<String, Object> event) {
        try {
            if (event == null || !event.containsKey(Constants.TYPE)) {
                return ResponseEntity.badRequest().build();
            }

            String type = (String) event.get(Constants.TYPE);

            switch (type) {
                case Constants.DEPOSIT:
                    return handleDeposit(event);
                case Constants.WITHDRAW:
                    return handleWithdraw(event);
                case Constants.TRANSFER:
                    return handleTransfer(event);
                default:
                    return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private ResponseEntity<Map<String, Object>> handleDeposit(Map<String, Object> event) {
        try {
            if (!event.containsKey(Constants.DESTINATION) || !event.containsKey(Constants.AMOUNT)) {
                return ResponseEntity.badRequest().build();
            }

            String destination = (String) event.get(Constants.DESTINATION);
            int amount = (Integer) event.get(Constants.AMOUNT);

            Map<String, Object> response = accountService.deposit(destination, amount);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private ResponseEntity<Map<String, Object>> handleWithdraw(Map<String, Object> event) {
        try {
            if (!event.containsKey(Constants.ORIGIN) || !event.containsKey(Constants.AMOUNT)) {
                return ResponseEntity.badRequest().build();
            }

            String origin = (String) event.get(Constants.ORIGIN);
            int amount = (Integer) event.get(Constants.AMOUNT);

            Map<String, Object> response = accountService.withdraw(origin, amount);
            if (response == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(Constants.ORIGIN, 0));
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private ResponseEntity<Map<String, Object>> handleTransfer(Map<String, Object> event) {
        try {
            if (!event.containsKey(Constants.ORIGIN) || !event.containsKey(Constants.DESTINATION) || !event.containsKey(Constants.AMOUNT)) {
                return ResponseEntity.badRequest().build();
            }

            String origin = (String) event.get(Constants.ORIGIN);
            String destination = (String) event.get(Constants.DESTINATION);
            int amount = (Integer) event.get(Constants.AMOUNT);

            Map<String, Object> response = accountService.transfer(origin, destination, amount);
            if (response == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(Constants.ORIGIN, 0));
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}