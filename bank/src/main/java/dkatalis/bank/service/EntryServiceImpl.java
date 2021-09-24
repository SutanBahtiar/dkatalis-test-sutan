package dkatalis.bank.service;

import dkatalis.bank.dao.EntryDao;
import dkatalis.bank.enums.TransactionCode;
import dkatalis.bank.model.Entry;
import dkatalis.bank.model.Transaction;
import dkatalis.bank.util.Generator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EntryServiceImpl implements EntryService {

    private static final Logger log = LoggerFactory.getLogger(EntryServiceImpl.class);

    private final EntryDao dao;
    private final TransactionService transactionService;

    public EntryServiceImpl(EntryDao dao,
                            TransactionService transactionService) {
        this.dao = dao;
        this.transactionService = transactionService;
    }

    @Override
    public synchronized long createEntry(String customerName,
                                         TransactionCode transactionCode,
                                         double amount,
                                         String refId) {
        final String trxId = Generator.generateTrxId();

        if (log.isDebugEnabled())
            log.debug("{}, create entry.. trxId:{}, customerName:{}, transaction code:{}, amount:{}"
                    , refId, trxId, customerName, transactionCode, amount);
        try {
            final Entry entry = new Entry();
            entry.setCustomerName(customerName);
            entry.setTransactionCode(transactionCode);
            entry.setAmount(amount);
            entry.setRefId(refId);
            entry.setTrxId(trxId);

            // save entry
            final long entryId = dao.createEntry(entry);

            // check owed
            final Map<String, Double> owedAmountMap = transactionService.getTotalOwedAmount(customerName);

            // return if not have owed
            if (owedAmountMap.isEmpty()) {
                // save transaction
                transactionService.createTransaction(customerName, transactionCode, amount, entryId, trxId);
                return entryId;
            }

            // owed must clear
            double transferAmount = amount;
            double totalOwedAmount = 0;
            for (String toCustomerName : owedAmountMap.keySet()) {
                if (totalOwedAmount >= amount) break;

                double owedAmount = owedAmountMap.get(toCustomerName);
                double diffAmount = transferAmount + owedAmount;

                if (diffAmount > 0) {
                    transferAmount = owedAmount * -1;
                }

                // save transaction to deduction the owed
                transactionService.createTransaction(customerName, toCustomerName, TransactionCode.OWED, transferAmount, entryId, trxId);
                // save as transfer to update the balance toCustomerName
                transactionService.createTransaction(customerName, toCustomerName, TransactionCode.TRANSFER, transferAmount, entryId, trxId);
                // save as withdraw(minus) to deduction the balance customerName
                transactionService.createTransaction(customerName, toCustomerName, TransactionCode.WITHDRAW, transferAmount * -1, entryId, trxId);
                // update amount
                totalOwedAmount += transferAmount;

                if (diffAmount <= 0) break;
            }

            final double diffAmountAndTotalOwed = amount - totalOwedAmount;
            if (diffAmountAndTotalOwed > 0)
                // save transaction as deposit
                transactionService.createTransaction(customerName, TransactionCode.DEPOSIT, diffAmountAndTotalOwed, entryId, trxId);

            return entryId;
        } catch (Exception e) {
            transactionService.remove(trxId);
            dao.remove(trxId);
            log.error(e.getMessage(), e);
        }
        return Transaction.ERROR_SAVE_DATA;
    }

    @Override
    public Entry getEntry(long entryId) {
        return dao.getEntry(entryId);
    }

    @Override
    public List<Entry> getEntryList() {
        return dao.getEntryList();
    }

    @Override
    public List<Entry> getEntryList(String customerName) {
        final List<Entry> entryList = new ArrayList<>();
        for (Entry entry : getEntryList()) {
            if (customerName.equals(entry.getCustomerName()))
                entryList.add(entry);
        }
        return entryList;
    }

    @Override
    public double getTotalAmount(String customerName) {
        double totalAmount = 0;
        for (Entry entry : getEntryList(customerName)) {
            if (TransactionCode.DEPOSIT == entry.getTransactionCode())
                totalAmount += entry.getAmount();
            else if (TransactionCode.WITHDRAW == entry.getTransactionCode())
                totalAmount -= entry.getAmount();
        }
        return totalAmount;
    }
}
