package dkatalis.bank.service;

import dkatalis.bank.dao.EntryDao;
import dkatalis.bank.dao.EntryDaoImpl;
import dkatalis.bank.dao.TransactionDao;
import dkatalis.bank.dao.TransactionDaoImpl;
import dkatalis.bank.enums.TransactionCode;
import dkatalis.bank.model.Entry;
import dkatalis.bank.model.Transaction;
import dkatalis.bank.util.Generator;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class EntryServiceTest {

    private EntryService entryService;
    private TransactionService transactionService;

    @Before
    public void init() {
        final Map<Long, Transaction> transactionMap = new HashMap<>();
        final TransactionDao transactionDao = new TransactionDaoImpl(transactionMap);
        transactionService = new TransactionServiceImpl(transactionDao);

        final Map<Long, Entry> entryMap = new HashMap<>();
        final EntryDao entryDao = new EntryDaoImpl(entryMap);
        entryService = new EntryServiceImpl(entryDao, transactionService);
    }

    @Test
    public void testCreateDeposit() {
        final String refId = Generator.generateRefId();

        // alice deposit 100
        final String customerName = "alice";
        final double amount = 100;
        final long entryId = entryService.createEntry(customerName, TransactionCode.DEPOSIT, amount, refId);
        final Entry entry = entryService.getEntry(entryId);
        assertEquals(customerName, entry.getCustomerName());
        assertEquals(TransactionCode.DEPOSIT, entry.getTransactionCode());
        assertEquals(amount, entry.getAmount(), 0);
        assertEquals(refId, entry.getRefId());

        final List<Transaction> transactionList = transactionService.getTransactionListByTableId(entryId);
        assertEquals(1, transactionList.size());
        assertEquals(customerName, transactionList.get(0).getCustomerName());
        assertEquals(TransactionCode.DEPOSIT, transactionList.get(0).getTransactionCode());
        assertEquals(amount, transactionList.get(0).getAmount(), 0);
        assertEquals(entryId, transactionList.get(0).getTableId());
    }

    @Test
    public void testCreateWithdraw() {
        final String refId = Generator.generateRefId();

        // bob withdraw 50
        final String customerName = "bob";
        final double amount = 50;
        final long entryId = entryService.createEntry(customerName, TransactionCode.WITHDRAW, amount, refId);
        final Entry entry = entryService.getEntry(entryId);
        assertEquals(customerName, entry.getCustomerName());
        assertEquals(TransactionCode.WITHDRAW, entry.getTransactionCode());
        assertEquals(amount, entry.getAmount(), 0);
        assertEquals(refId, entry.getRefId());

        final List<Transaction> transactionList = transactionService.getTransactionListByTableId(entryId);
        assertEquals(1, transactionList.size());
        assertEquals(customerName, transactionList.get(0).getCustomerName());
        assertEquals(TransactionCode.WITHDRAW, transactionList.get(0).getTransactionCode());
        assertEquals(amount, transactionList.get(0).getAmount(), 0);
        assertEquals(entryId, transactionList.get(0).getTableId());
    }

}
