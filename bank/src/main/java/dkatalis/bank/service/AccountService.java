package dkatalis.bank.service;

import dkatalis.bank.model.Account;

import java.util.List;

public interface AccountService {

    String createAccount(String customerName,
                         String refId);

    Account getAccount(String customerName);

    List<Account> getAccountList();
}
