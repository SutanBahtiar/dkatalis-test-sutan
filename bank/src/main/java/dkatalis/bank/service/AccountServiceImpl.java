package dkatalis.bank.service;

import dkatalis.bank.dao.AccountDao;
import dkatalis.bank.model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AccountServiceImpl implements AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

    private final AccountDao dao;

    public AccountServiceImpl(AccountDao dao) {
        this.dao = dao;
    }

    @Override
    public String createAccount(String customerName,
                                String refId) {
        if (null != getAccount(customerName))
            return null;

        if (log.isDebugEnabled())
            log.debug("{}, create account.. customerName:{}", refId, customerName);

        final Account account = new Account();
        account.setCustomerName(customerName);
        account.setRefId(refId);
        return dao.createAccount(account);
    }

    @Override
    public Account getAccount(String customerName) {
        return dao.getAccount(customerName);
    }

    @Override
    public List<Account> getAccountList() {
        return dao.getAccountList();
    }
}
