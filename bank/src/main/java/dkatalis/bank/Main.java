package dkatalis.bank;

import dkatalis.bank.dao.AccountDao;
import dkatalis.bank.dao.AccountDaoImpl;
import dkatalis.bank.dao.EntryDao;
import dkatalis.bank.dao.EntryDaoImpl;
import dkatalis.bank.dao.TransactionDao;
import dkatalis.bank.dao.TransactionDaoImpl;
import dkatalis.bank.dao.TransferDao;
import dkatalis.bank.dao.TransferDaoImpl;
import dkatalis.bank.model.Account;
import dkatalis.bank.model.Entry;
import dkatalis.bank.model.Transaction;
import dkatalis.bank.model.Transfer;
import dkatalis.bank.service.AccountService;
import dkatalis.bank.service.AccountServiceImpl;
import dkatalis.bank.service.AtmService;
import dkatalis.bank.service.AtmServiceImpl;
import dkatalis.bank.service.EntryService;
import dkatalis.bank.service.EntryServiceImpl;
import dkatalis.bank.service.TransactionService;
import dkatalis.bank.service.TransactionServiceImpl;
import dkatalis.bank.service.TransferService;
import dkatalis.bank.service.TransferServiceImpl;
import dkatalis.bank.socket.Server;

import java.util.HashMap;
import java.util.Map;

public class Main {

    private static final int PORT = 6666;

    public static void main(String[] args) {

        final Map<String, Account> accountMap = new HashMap<>();
        final AccountDao accountDao = new AccountDaoImpl(accountMap);
        final AccountService accountService = new AccountServiceImpl(accountDao);

        final Map<Long, Transaction> transactionMap = new HashMap<>();
        final TransactionDao transactionDao = new TransactionDaoImpl(transactionMap);
        final TransactionService transactionService = new TransactionServiceImpl(transactionDao);

        final Map<Long, Entry> entryMap = new HashMap<>();
        final EntryDao entryDao = new EntryDaoImpl(entryMap);
        final EntryService entryService = new EntryServiceImpl(entryDao, transactionService);

        final Map<Long, Transfer> transferMap = new HashMap<>();
        final TransferDao transferDao = new TransferDaoImpl(transferMap);
        final TransferService transferService = new TransferServiceImpl(transferDao, transactionService);

        final AtmService atmService = new AtmServiceImpl(accountService, entryService,
                transferService, transactionService);
        final Server server = new Server(atmService);
        server.start(PORT);
    }
}
