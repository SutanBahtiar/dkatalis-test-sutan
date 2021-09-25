package dkatalis.bank.dao;

import dkatalis.bank.model.Account;
import dkatalis.bank.util.Generator;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AccountDaoTest {

    private AccountDao accountDao;

    @Before
    public void init() {
        final Map<String, Account> accountMap = new HashMap<>();
        accountDao = new AccountDaoImpl(accountMap);
    }

    @Test
    public void testAccount() {
        final Account account = new Account();
        account.setCustomerName("alice");
        account.setRefId(Generator.generateRefId());
        final String customerName = accountDao.createAccount(account);
        assertEquals(customerName, account.getCustomerName());

        final List<Account> accountList = accountDao.getAccountList();
        assertEquals(customerName, accountList.get(0).getCustomerName());
        assertEquals(account.getRefId(), accountList.get(0).getRefId());
        assertEquals(new Date().getTime(), accountList.get(0).getCreatedDate().getTime(), 10);

        assertNull(accountDao.createAccount(account));
    }
}
