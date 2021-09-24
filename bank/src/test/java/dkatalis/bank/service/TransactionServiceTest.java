package dkatalis.bank.service;

import dkatalis.bank.dao.EntryDao;
import dkatalis.bank.dao.EntryDaoImpl;
import dkatalis.bank.dao.TransactionDao;
import dkatalis.bank.dao.TransactionDaoImpl;
import dkatalis.bank.dao.TransferDao;
import dkatalis.bank.dao.TransferDaoImpl;
import dkatalis.bank.enums.TransactionCode;
import dkatalis.bank.model.Entry;
import dkatalis.bank.model.Transaction;
import dkatalis.bank.model.Transfer;
import dkatalis.bank.util.Generator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TransactionServiceTest {

    private TransactionService transactionService;
    private EntryService entryService;
    private TransferService transferService;

    @Before
    public void init() {
        final Map<Long, Transaction> transactionMap = new HashMap<>();
        final TransactionDao transactionDao = new TransactionDaoImpl(transactionMap);
        transactionService = new TransactionServiceImpl(transactionDao);

        final Map<Long, Entry> entryMap = new HashMap<>();
        final EntryDao entryDao = new EntryDaoImpl(entryMap);
        entryService = new EntryServiceImpl(entryDao, transactionService);

        final Map<Long, Transfer> transferMap = new HashMap<>();
        final TransferDao transferDao = new TransferDaoImpl(transferMap);
        transferService = new TransferServiceImpl(transferDao, transactionService);
    }

    @Test
    public void testGetTotalOwedAmount() {
        final long tableId = Generator.generateId();
        final String trxId = Generator.generateTrxId();

        // alice owed to bob 100
        transactionService.createTransaction("alice", "bob", TransactionCode.OWED, -100, tableId, trxId);

        // alice clear owed to bob 50
        transactionService.createTransaction("alice", "bob", TransactionCode.OWED, 50, tableId, trxId);

        // bob owed to alice 40
        transactionService.createTransaction("bob", "alice", TransactionCode.OWED, -40, tableId, trxId);

        Map<String, Double> totalOweAmountToMap = transactionService.getTotalOwedAmount("alice");
        // alice owed to bob 10
        Assert.assertEquals(-10, totalOweAmountToMap.get("bob"), 0);

        Map<String, Double> totalOweAmountFromMap = transactionService.getTotalOwedAmount("bob");
        // bob owed from alice 10
        Assert.assertEquals(10, totalOweAmountFromMap.get("alice"), 0);
    }
}
