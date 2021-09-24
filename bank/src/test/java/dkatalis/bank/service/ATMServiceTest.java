package dkatalis.bank.service;

import dkatalis.bank.dao.AccountDao;
import dkatalis.bank.dao.AccountDaoImpl;
import dkatalis.bank.dao.EntryDao;
import dkatalis.bank.dao.EntryDaoImpl;
import dkatalis.bank.dao.TransactionDao;
import dkatalis.bank.dao.TransactionDaoImpl;
import dkatalis.bank.dao.TransferDao;
import dkatalis.bank.dao.TransferDaoImpl;
import dkatalis.bank.enums.Commands;
import dkatalis.bank.model.Account;
import dkatalis.bank.model.Entry;
import dkatalis.bank.model.Transaction;
import dkatalis.bank.model.Transfer;
import dkatalis.bank.util.Generator;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ATMServiceTest {

    private TransactionService transactionService;
    private EntryService entryService;
    private TransferService transferService;
    private AccountService accountService;
    private AtmService atmService;

    @Before
    public void init() {
        final Map<String, Account> accountMap = new HashMap<>();
        final AccountDao accountDao = new AccountDaoImpl(accountMap);
        accountService = new AccountServiceImpl(accountDao);

        final Map<Long, Transaction> transactionMap = new HashMap<>();
        final TransactionDao transactionDao = new TransactionDaoImpl(transactionMap);
        transactionService = new TransactionServiceImpl(transactionDao);

        final Map<Long, Entry> entryMap = new HashMap<>();
        final EntryDao entryDao = new EntryDaoImpl(entryMap);
        entryService = new EntryServiceImpl(entryDao, transactionService);

        final Map<Long, Transfer> transferMap = new HashMap<>();
        final TransferDao transferDao = new TransferDaoImpl(transferMap);
        transferService = new TransferServiceImpl(transferDao, transactionService);

        atmService = new AtmServiceImpl(accountService, entryService, transferService, transactionService);
    }

    @Test
    public void testTransaction() {
        final String refId = Generator.generateRefId();

//        // login alice
        String message = "-" + '|' + Commands.LOGIN + '|' + "alice";
        System.out.println("#> login alice");
        System.out.println(atmService.login(message, refId));
        System.out.println();

        // alice deposit 100
        message = "alice" + '|' + Commands.DEPOSIT + '|' + "100";
        System.out.println("#> alice deposit 100");
        System.out.println(atmService.deposit(message, refId));
        System.out.println();

        // login bob
        message = "-" + '|' + Commands.LOGIN + '|' + "bob";
        System.out.println("#> login bob");
        System.out.println(atmService.login(message, refId));
        System.out.println();

        // bob deposit 80
        message = "bob" + '|' + Commands.DEPOSIT + '|' + "80";
        System.out.println("#> bob deposit 80");
        System.out.println(atmService.deposit(message, refId));
        System.out.println();

        // bob transfer alice 50
        message = "bob" + '|' + Commands.TRANSFER + '|' + "alice" + '|' + "50";
        System.out.println("#> bob transfer alice 50");
        System.out.println(atmService.transfer(message, refId));
        System.out.println();

        // bob transfer alice 100
        message = "bob" + '|' + Commands.TRANSFER + '|' + "alice" + '|' + "100";
        System.out.println("#> bob transfer alice 100");
        System.out.println(atmService.transfer(message, refId));
        System.out.println();

//        // login alice
//        message = "-" + '|' + Commands.LOGIN + '|' + "alice";
//        System.out.println("#> login alice");
//        System.out.println(atmService.login(message, refId));
//        System.out.println();
//
//        // login bob
//        message = "-" + '|' + Commands.LOGIN + '|' + "bob";
//        System.out.println("#> login bob");
//        System.out.println(atmService.login(message, refId));
//        System.out.println();
//
        // bob deposit 30
        message = "bob" + '|' + Commands.DEPOSIT + '|' + "30";
        System.out.println("#> bob deposit 30");
        System.out.println(atmService.deposit(message, refId));
        System.out.println();

        // login alice
        message = "-" + '|' + Commands.LOGIN + '|' + "alice";
        System.out.println("#> login alice");
        System.out.println(atmService.login(message, refId));
        System.out.println();
//
//        // login bob
//        message = "-" + '|' + Commands.LOGIN + '|' + "bob";
//        System.out.println("#> login bob");
//        System.out.println(atmService.login(message, refId));
//        System.out.println();

        // alice transfer bob 30
        message = "alice" + '|' + Commands.TRANSFER + '|' + "bob" + '|' + "30";
        System.out.println("#> alice transfer bob 30");
        System.out.println(atmService.transfer(message, refId));
        System.out.println();

//        // login alice
//        message = "-" + '|' + Commands.LOGIN + '|' + "alice";
//        System.out.println("#> login alice");
//        System.out.println(atmService.login(message, refId));
//        System.out.println();
//
        // login bob
        message = "-" + '|' + Commands.LOGIN + '|' + "bob";
        System.out.println("#> login bob");
        System.out.println(atmService.login(message, refId));
        System.out.println();

        // bob deposit 100
        message = "bob" + '|' + Commands.DEPOSIT + '|' + "100";
        System.out.println("#> bob deposit 100");
        System.out.println(atmService.deposit(message, refId));
        System.out.println();

        System.out.println();
        System.out.println("[TRANSACTION]");

        final List<Transaction> sortTransaction = transactionService.getTransactionList();
        Collections.sort(sortTransaction, Comparator.comparing(Transaction::getTransactionId));

        System.out.printf("%-20s %-15s %-15s %-15s %-10s %-20s %-20s\n"
                , "TransactionId"
                , "CustomerName"
                , "ToCustomerName"
                , "TransactionCode"
                , "Amount"
                , "Table Id"
                , "CreatedDate"
        );
        for (Transaction transaction : sortTransaction) {
            System.out.printf("%-20s %-15s %-15s %-15s %-10s %-20s %-20s\n"
                    , transaction.getTransactionId()
                    , transaction.getCustomerName()
                    , transaction.getToCustomerName()
                    , transaction.getTransactionCode().name()
                    , transaction.getAmount()
                    , transaction.getTableId()
                    , transaction.getCreatedDate()
            );
        }

        System.out.println();
        System.out.println("[ENTRY]");

        final List<Entry> sortEntry = entryService.getEntryList();
        Collections.sort(sortEntry, Comparator.comparing(Entry::getEntryId));

        System.out.printf("%-20s %-15s %-15s %-10s %-20s\n"
                , "EntryId"
                , "CustomerName"
                , "TransactionCode"
                , "Amount"
                , "CreatedDate"
        );
        for (Entry entry : sortEntry) {
            System.out.printf("%-20s %-15s %-15s %-10s %-20s\n"
                    , entry.getEntryId()
                    , entry.getCustomerName()
                    , entry.getTransactionCode().name()
                    , entry.getAmount()
                    , entry.getCreatedDate()
            );
        }

        System.out.println();
        System.out.println("[TRANSFER]");

        final List<Transfer> sortTransfer = transferService.getTransferList();
        Collections.sort(sortTransfer, Comparator.comparing(Transfer::getTransferId));

        System.out.printf("%-20s %-15s %-15s %-10s %-20s\n"
                , "TransferId"
                , "CustomerName"
                , "ToCustomerName"
                , "Amount"
                , "CreatedDate"
        );

        for (Transfer transfer : transferService.getTransferList()) {
            System.out.printf("%-20s %-15s %-15s %-10s %-20s\n"
                    , transfer.getTransferId()
                    , transfer.getCustomerName()
                    , transfer.getToCustomerName()
                    , transfer.getAmount()
                    , transfer.getCreatedDate()
            );
        }
    }
}
