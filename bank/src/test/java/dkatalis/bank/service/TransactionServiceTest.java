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
import org.junit.Before;
import org.junit.Test;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

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

        Map<String, Double> totalOweAmountToMap = transactionService.getTotalOwedAmountMap("alice");
        // alice owed to bob 10
        assertEquals(-10, totalOweAmountToMap.get("bob"), 0);

        Map<String, Double> totalOweAmountFromMap = transactionService.getTotalOwedAmountMap("bob");
        // bob owed from alice 10
        assertEquals(10, totalOweAmountFromMap.get("alice"), 0);
    }

    @Test
    public void testTransfer() {
        final String refId = Generator.generateRefId();

        // alice transfer to bob 50
        final String customerName = "alice";
        final String toCustomerName = "bob";
        final double amount = 40;

        createDepositAndWithdraw();

        final long transferId = transferService.createTransfer(customerName, toCustomerName, amount, refId);
        final Transfer transfer = transferService.getTransfer(transferId);
        assertEquals(customerName, transfer.getCustomerName());
        assertEquals(toCustomerName, transfer.getToCustomerName());
        assertEquals(amount, transfer.getAmount(), 0);
        assertEquals(refId, transfer.getRefId());

        final double balanceAmount = transactionService.getBalanceAmount(customerName);
        assertEquals(10, balanceAmount, 0);

//        showTable();
    }

    public void createDepositAndWithdraw() {
        final String refId = Generator.generateRefId();

        // alice deposit 100
        final String customerName = "alice";
        final double amount = 100;
        final long entryId = entryService.createDeposit(customerName, amount, refId);
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

        createWithdraw();
    }

    public void createWithdraw() {
        final String refId = Generator.generateRefId();

        // bob withdraw 50
        final String customerName = "alice";
        final double amount = 50;
        final long entryId = entryService.createWithdraw(customerName, amount, refId);
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

    public void showTable() {
        final List<Transaction> sortTransaction = transactionService.getTransactionList();
        sortTransaction.sort(Comparator.comparing(Transaction::getTransactionId));

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
        for (Transaction transaction : sortTransaction) {
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

        final List<Entry> sortEntry = entryService.getEntryList();
        sortEntry.sort(Comparator.comparing(Entry::getEntryId));

        System.out.printf("%-20s %-15s %-15s %-10s %-15s %-15s %-20s\n"
                , "EntryId"
                , "CustomerName"
                , "TransactionCode"
                , "Amount"
                , "TrxId"
                , "RefId"
                , "CreatedDate"
        );
        for (Entry entry : sortEntry) {
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

        final List<Transfer> sortTransfer = transferService.getTransferList();
        sortTransfer.sort(Comparator.comparing(Transfer::getTransferId));

        System.out.printf("%-20s %-15s %-15s %-10s %-15s %-15s %-20s\n"
                , "TransferId"
                , "CustomerName"
                , "ToCustomerName"
                , "Amount"
                , "TrxId"
                , "RefId"
                , "CreatedDate"
        );

        for (Transfer transfer : transferService.getTransferList()) {
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
    }
}
