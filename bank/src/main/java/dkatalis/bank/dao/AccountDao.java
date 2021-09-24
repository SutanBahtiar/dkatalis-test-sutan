package dkatalis.bank.dao;

import dkatalis.bank.model.Account;

import java.util.List;

public interface AccountDao {

    String createAccount(Account account);

    Account getAccount(String customerName);

    List<Account> getAccountList();
}
