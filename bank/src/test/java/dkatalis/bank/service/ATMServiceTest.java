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

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
    public void testLoginReturnNull() {
        final String refId = Generator.generateRefId();
        String message = "-" + '|' + Commands.LOGIN;
        assertNull(atmService.login(message, refId));
        message = "-" + '|' + Commands.LOGOUT + '|' + "alice";
        assertNull(atmService.login(message, refId));
        message = "alice" + '|' + Commands.LOGIN + '|' + "alice";
        assertNull(atmService.login(message, refId));
        accountService.createAccount("alice", refId);
        message = "alice" + '|' + Commands.LOGIN + '|' + null;
        assertNull(atmService.login(message, refId));
    }

    @Test
    public void testDepositReturnNull() {
        final String refId = Generator.generateRefId();
        String message = "-" + '|' + Commands.DEPOSIT;
        assertNull(atmService.deposit(message, refId));
        message = "-" + '|' + Commands.LOGOUT + '|' + "alice";
        assertNull(atmService.deposit(message, refId));
        message = "-" + '|' + Commands.DEPOSIT + '|' + "alice";
        assertNull(atmService.deposit(message, refId));
        message = "alice" + '|' + Commands.DEPOSIT + '|' + "100";
        assertNull(atmService.deposit(message, refId));
        message = "alice" + '|' + Commands.DEPOSIT + '|' + "aa";
        assertNull(atmService.deposit(message, refId));
    }

    @Test
    public void testWithdrawReturnNull() {
        final String refId = Generator.generateRefId();
        String message = "-" + '|' + Commands.WITHDRAW;
        assertNull(atmService.withdraw(message, refId));
        message = "-" + '|' + Commands.LOGOUT + '|' + "alice";
        assertNull(atmService.withdraw(message, refId));
        message = "-" + '|' + Commands.WITHDRAW + '|' + "alice";
        assertNull(atmService.withdraw(message, refId));
        message = "alice" + '|' + Commands.WITHDRAW + '|' + "100";
        assertNull(atmService.withdraw(message, refId));
        message = "alice" + '|' + Commands.WITHDRAW + '|' + "aa";
        assertNull(atmService.withdraw(message, refId));
    }

    @Test
    public void testTransferReturnNull() {
        final String refId = Generator.generateRefId();
        String message = "-" + '|' + Commands.TRANSFER;
        assertNull(atmService.transfer(message, refId));
        message = "-" + '|' + Commands.TRANSFER + '|' + "alice" + '|' + "alice";
        assertNull(atmService.transfer(message, refId));
        message = "alice" + '|' + Commands.LOGOUT + '|' + "alice" + '|' + "alice";
        assertNull(atmService.transfer(message, refId));
        message = "-" + '|' + Commands.TRANSFER + '|' + "alice" + '|' + "alice";
        assertNull(atmService.transfer(message, refId));
        message = "alice" + '|' + Commands.TRANSFER + '|' + "alice" + '|' + "alice";
        assertNull(atmService.transfer(message, refId));
    }

    @Test
    public void testTransaction() {
        final String refId = Generator.generateRefId();

        // login alice
        String customerName = "alice";
        String message = "-" + '|' + Commands.LOGIN + '|' + "alice";
        atmService.login(message, refId);
        assertEquals(0, getBalanceAmount(customerName), 0);

        // alice deposit 100
        message = "alice" + '|' + Commands.DEPOSIT + '|' + "100";
        atmService.deposit(message, refId);
        assertEquals(100, getBalanceAmount(customerName), 0);

        // login bob
        customerName = "bob";
        message = "-" + '|' + Commands.LOGIN + '|' + "bob";
        atmService.login(message, refId);
        assertEquals(0, getBalanceAmount(customerName), 0);

        // bob deposit 80
        message = "bob" + '|' + Commands.DEPOSIT + '|' + "80";
        atmService.deposit(message, refId);
        assertEquals(80, getBalanceAmount(customerName), 0);

        // bob transfer alice 50
        message = "bob" + '|' + Commands.TRANSFER + '|' + "alice" + '|' + "50";
        atmService.transfer(message, refId);
        assertEquals(30, getBalanceAmount(customerName), 0);

        // bob transfer alice 100
        message = "bob" + '|' + Commands.TRANSFER + '|' + "alice" + '|' + "100";
        atmService.transfer(message, refId);
        assertEquals(0, getBalanceAmount(customerName), 0);
        assertEquals(-70, getTotalOwedAmountMap(customerName).get("alice"), 0);

        // bob deposit 30
        message = "bob" + '|' + Commands.DEPOSIT + '|' + "30";
        atmService.deposit(message, refId);
        assertEquals(0, getBalanceAmount(customerName), 0);
        assertEquals(-40, getTotalOwedAmountMap(customerName).get("alice"), 0);

        // login alice
        customerName = "alice";
        message = "-" + '|' + Commands.LOGIN + '|' + "alice";
        atmService.login(message, refId);
        assertEquals(210, getBalanceAmount(customerName), 0);
        assertEquals(40, getTotalOwedAmountMap(customerName).get("bob"), 0);

        // alice transfer bob 30
        message = "alice" + '|' + Commands.TRANSFER + '|' + "bob" + '|' + "30";
        atmService.transfer(message, refId);
        assertEquals(210, getBalanceAmount(customerName), 0);
        assertEquals(10, getTotalOwedAmountMap(customerName).get("bob"), 0);

        // login bob
        customerName = "bob";
        message = "-" + '|' + Commands.LOGIN + '|' + "bob";
        atmService.login(message, refId);
        assertEquals(0, getBalanceAmount(customerName), 0);
        assertEquals(-10, getTotalOwedAmountMap(customerName).get("alice"), 0);

        // bob deposit 100
        message = "bob" + '|' + Commands.DEPOSIT + '|' + "100";
        atmService.deposit(message, refId);
        assertEquals(90, getBalanceAmount(customerName), 0);
        assertEquals(0, getTotalOwedAmountMap(customerName).get("alice"), 0);

//        showTable();
    }

    public Map<String, Double> getTotalOwedAmountMap(String customerName) {
        final Map<String, Double> totalOwedAmountMap = transactionService.getTotalOwedAmountMap(customerName);
//        System.out.println("totalOwedAmountMap" + totalOwedAmountMap);
        return totalOwedAmountMap;
    }

    public double getBalanceAmount(String customerName) {
        final double balanceAmount = transactionService.getBalanceAmount(customerName);
//        System.out.println("balance amount:" + balanceAmount);
        return balanceAmount;
    }

    public void showTable() {
        final List<Transaction> transactionList = transactionService.getTransactionList();
        transactionList.sort(Comparator.comparing(Transaction::getTransactionId));

        System.out.println();
        System.out.println("[TRANSACTION]");
        System.out.printf("%-20s %-15s %-15s %-15s %-10s %-15s %-20s %-20s\n"
                , "TransactionId"
                , "CustomerName"
                , "ToCustomerName"
                , "TransactionCode"
                , "Amount"
                , "TrxId"
                , "Table Id"
                , "CreatedDate"
        );
        for (Transaction transaction : transactionList) {
            System.out.printf("%-20s %-15s %-15s %-15s %-10s %-15s %-20s %-20s\n"
                    , transaction.getTransactionId()
                    , transaction.getCustomerName()
                    , transaction.getToCustomerName()
                    , transaction.getTransactionCode().name()
                    , transaction.getAmount()
                    , transaction.getTrxId()
                    , transaction.getTableId()
                    , transaction.getCreatedDate()
            );
        }

        System.out.println();
        System.out.println("[ENTRY]");

        final List<Entry> entryList = entryService.getEntryList();
        entryList.sort(Comparator.comparing(Entry::getEntryId));

        System.out.printf("%-20s %-15s %-15s %-10s %-15s %-15s %-20s\n"
                , "EntryId"
                , "CustomerName"
                , "TransactionCode"
                , "Amount"
                , "TrxId"
                , "RefId"
                , "CreatedDate"
        );
        for (Entry entry : entryList) {
            System.out.printf("%-20s %-15s %-15s %-10s %-15s %-15s %-20s\n"
                    , entry.getEntryId()
                    , entry.getCustomerName()
                    , entry.getTransactionCode().name()
                    , entry.getAmount()
                    , entry.getTrxId()
                    , entry.getRefId()
                    , entry.getCreatedDate()
            );
        }

        System.out.println();
        System.out.println("[TRANSFER]");

        final List<Transfer> transferList = transferService.getTransferList();
        transferList.sort(Comparator.comparing(Transfer::getTransferId));

        System.out.printf("%-20s %-15s %-15s %-10s %-15s %-15s %-20s\n"
                , "TransferId"
                , "CustomerName"
                , "ToCustomerName"
                , "Amount"
                , "TrxId"
                , "RefId"
                , "CreatedDate"
        );

        for (Transfer transfer : transferList) {
            System.out.printf("%-20s %-15s %-15s %-10s %-15s %-15s %-20s\n"
                    , transfer.getTransferId()
                    , transfer.getCustomerName()
                    , transfer.getToCustomerName()
                    , transfer.getAmount()
                    , transfer.getTrxId()
                    , transfer.getRefId()
                    , transfer.getCreatedDate()
            );
        }

        System.out.println();

        final List<Account> accountList = accountService.getAccountList();
        accountList.sort(Comparator.comparing(Account::getCreatedDate));

        System.out.printf("%-20s %-15s %-20s\n"
                , "TransferId"
                , "CustomerName"
                , "ToCustomerName"
                , "Amount"
                , "TrxId"
                , "RefId"
                , "CreatedDate"
        );

        for (Account account : accountList) {
            System.out.printf("%-20s %-15s %-20s\n"
                    , account.getCustomerName()
                    , account.getRefId()
                    , account.getCreatedDate()
            );
        }

        System.out.println();
    }
}
