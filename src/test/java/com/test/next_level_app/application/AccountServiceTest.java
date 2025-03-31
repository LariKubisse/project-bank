package com.test.next_level_app.application;

import com.test.next_level_app.domain.Account;
import com.test.next_level_app.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class AccountServiceTest {

    private AccountService accountService;

    @BeforeEach
    public void setup() {
        accountService = new AccountService();
    }

    @Test
    public void testDeposit_NewAccount() {
        String destination = "12345";
        int amount = 100;
        Map<String, Object> result = accountService.deposit(destination, amount);
        assertNotNull(result);
        assertEquals(destination, ((Map<String, Object>) result.get(Constants.DESTINATION)).get(Constants.ID));
        assertEquals(amount, ((Map<String, Object>) result.get(Constants.DESTINATION)).get(Constants.BALANCE));
    }

    @Test
    public void testDeposit_ExistingAccount() {
        String destination = "12345";
        int initialAmount = 100;
        accountService.deposit(destination, initialAmount);
        int additionalAmount = 50;
        Map<String, Object> result = accountService.deposit(destination, additionalAmount);
        assertNotNull(result);
        assertEquals(destination, ((Map<String, Object>) result.get(Constants.DESTINATION)).get(Constants.ID));
        assertEquals(initialAmount + additionalAmount, ((Map<String, Object>) result.get(Constants.DESTINATION)).get(Constants.BALANCE));
    }

    @Test
    public void testWithdraw_ExistingAccount() {
        String origin = "12345";
        int initialAmount = 100;
        accountService.deposit(origin, initialAmount);
        int withdrawAmount = 50;
        Map<String, Object> result = accountService.withdraw(origin, withdrawAmount);
        assertNotNull(result);
        assertEquals(origin, ((Map<String, Object>) result.get(Constants.ORIGIN)).get(Constants.ID));
        assertEquals(initialAmount - withdrawAmount, ((Map<String, Object>) result.get(Constants.ORIGIN)).get(Constants.BALANCE));
    }

    @Test
    public void testWithdraw_NonExistingAccount() {
        String origin = "12345";
        int withdrawAmount = 50;
        Map<String, Object> result = accountService.withdraw(origin, withdrawAmount);
        assertNull(result);
    }

    @Test
    public void testWithdraw_InsufficientFunds() {
        String origin = "12345";
        int initialAmount = 100;
        accountService.deposit(origin, initialAmount);
        int withdrawAmount = 150;
        Map<String, Object> result = accountService.withdraw(origin, withdrawAmount);
        assertNull(result);
    }

    @Test
    public void testTransfer_ExistingAccounts() {
        String origin = "12345";
        String destination = "67890";
        int initialAmount = 100;
        accountService.deposit(origin, initialAmount);
        int transferAmount = 50;
        Map<String, Object> result = accountService.transfer(origin, destination, transferAmount);
        assertNotNull(result);
        assertEquals(origin, ((Map<String, Object>) result.get(Constants.ORIGIN)).get(Constants.ID));
        assertEquals(initialAmount - transferAmount, ((Map<String, Object>) result.get(Constants.ORIGIN)).get(Constants.BALANCE));
        assertEquals(destination, ((Map<String, Object>) result.get(Constants.DESTINATION)).get(Constants.ID));
        assertEquals(transferAmount, ((Map<String, Object>) result.get(Constants.DESTINATION)).get(Constants.BALANCE));
    }

    @Test
    public void testTransfer_NonExistingOriginAccount() {
        String origin = "12345";
        String destination = "67890";
        int transferAmount = 50;
        Map<String, Object> result = accountService.transfer(origin, destination, transferAmount);
        assertNull(result);
    }

    @Test
    public void testTransfer_NonExistingDestinationAccount() {
        String origin = "12345";
        String destination = "67890";
        int initialAmount = 100;
        accountService.deposit(origin, initialAmount);
        int transferAmount = 50;
        Map<String, Object> result = accountService.transfer(origin, destination, transferAmount);
        assertNotNull(result);
        assertEquals(origin, ((Map<String, Object>) result.get(Constants.ORIGIN)).get(Constants.ID));
        assertEquals(initialAmount - transferAmount, ((Map<String, Object>) result.get(Constants.ORIGIN)).get(Constants.BALANCE));
        assertEquals(destination, ((Map<String, Object>) result.get(Constants.DESTINATION)).get(Constants.ID));
        assertEquals(transferAmount, ((Map<String, Object>) result.get(Constants.DESTINATION)).get(Constants.BALANCE));
    }

    @Test
    public void testGetAccount_ExistingAccount() {
        String accountId = "12345";
        int initialAmount = 100;
        accountService.deposit(accountId, initialAmount);
        Optional<Account> account = accountService.getAccount(accountId);
        assertTrue(account.isPresent());
        assertEquals(accountId, account.get().getId());
        assertEquals(initialAmount, account.get().getBalance());
    }
}