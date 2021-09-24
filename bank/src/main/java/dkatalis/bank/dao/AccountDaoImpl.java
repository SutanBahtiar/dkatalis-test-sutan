package dkatalis.bank.dao;

import dkatalis.bank.model.Account;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AccountDaoImpl implements AccountDao {

    private final Map<String, Account> dataSource;

    public AccountDaoImpl(Map<String, Account> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public String createAccount(Account account) {
        // double-check to make sure not duplicate
        if (null != dataSource.get(account.getCustomerName()))
            return null;

        account.setCreatedDate(new Date());
        dataSource.put(account.getCustomerName(), account);
        return getAccount(account.getCustomerName()).getCustomerName();
    }

    @Override
    public Account getAccount(String customerName) {
        return dataSource.get(customerName);
    }

    @Override
    public List<Account> getAccountList() {
        return new ArrayList<>(dataSource.values());
    }
}
