package dkatalis.bank.service;

import dkatalis.bank.dao.TransactionDao;
import dkatalis.bank.dao.TransactionDaoImpl;
import dkatalis.bank.dao.TransferDao;
import dkatalis.bank.dao.TransferDaoImpl;
import dkatalis.bank.model.Transaction;
import dkatalis.bank.model.Transfer;
import org.junit.Before;

import java.util.HashMap;
import java.util.Map;

public class TransferServiceTest {

    private TransactionService transactionService;
    private TransferService transferService;

    @Before
    public void init() {
        final Map<Long, Transaction> transactionMap = new HashMap<>();
        final TransactionDao transactionDao = new TransactionDaoImpl(transactionMap);
        transactionService = new TransactionServiceImpl(transactionDao);

        final Map<Long, Transfer> transferMap = new HashMap<>();
        final TransferDao transferDao = new TransferDaoImpl(transferMap);
        transferService = new TransferServiceImpl(transferDao, transactionService);
    }

//    @Test
//    public void testTransfer() {
//        final String refId = Generator.generateRefId();
//        
//        // alice transfer to bob 100
//        final String customerName = "alice";
//        final String toCustomerName = "bob";
//        final double amount = 100;
//        final long transferId = transferService
//                .createTransfer(customerName, toCustomerName, amount, refId);
//        final Transfer transfer = transferService.getTransfer(transferId);
//        assertEquals(customerName, transfer.getCustomerName());
//        assertEquals(toCustomerName, transfer.getToCustomerName());
//        assertEquals(amount, transfer.getAmount(), 0);
//        assertEquals(refId, transfer.getRefId());
//
//        final List<Transaction> transactionList = transactionService.getTransactionListByRefId(transferId);
//        assertEquals(1, transactionList.size());
//        assertEquals(customerName, transactionList.get(0).getCustomerName());
//        assertEquals(toCustomerName, transactionList.get(0).getToCustomerName());
//        assertEquals(TransactionCode.TRANSFER, transactionList.get(0).getTransactionCode());
//        assertEquals(amount, transactionList.get(0).getAmount(), 0);
//        assertEquals(transferId, transactionList.get(0).getTableId());
//    }
}
