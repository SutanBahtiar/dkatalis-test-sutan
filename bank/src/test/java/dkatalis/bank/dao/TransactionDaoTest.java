package dkatalis.bank.dao;

import dkatalis.bank.enums.TransactionCode;
import dkatalis.bank.model.Transaction;
import dkatalis.bank.util.Generator;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TransactionDaoTest {

    private TransactionDao transactionDao;

    @Before
    public void init() {
        final Map<Long, Transaction> transactionMap = new HashMap<>();
        transactionDao = new TransactionDaoImpl(transactionMap);
    }

    @Test
    public void testTransaction() {
        final Transaction transaction = new Transaction();
        transaction.setCustomerName("alice");
        transaction.setTransactionCode(TransactionCode.TRANSFER);
        transaction.setAmount(100);
        transaction.setTrxId(Generator.generateTrxId());
        transaction.setTableId(1);
        transactionDao.createTransaction(transaction);

        final List<Transaction> transactionList = transactionDao.getTransactionList();
        assertEquals(new Date().getTime(), transactionList.get(0).getCreatedDate().getTime(), 10);

        assertEquals(TransactionCode.valueOf(TransactionCode.TRANSFER.getCode()), transaction.getTransactionCode());

        transactionDao.remove(transaction.getTrxId());
        assertTrue(transactionDao.getTransactionList().isEmpty());
    }
}
