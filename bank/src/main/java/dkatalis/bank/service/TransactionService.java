package dkatalis.bank.service;

import dkatalis.bank.enums.TransactionCode;
import dkatalis.bank.model.Entry;
import dkatalis.bank.model.Transaction;
import dkatalis.bank.model.Transfer;

import java.util.List;
import java.util.Map;

public interface TransactionService {

    long createTransaction(String customerName,
                           String toCustomerName,
                           TransactionCode transactionCode,
                           double amount,
                           long tableId,
                           String trxId);

    long createTransaction(String customerName,
                           TransactionCode transactionCode,
                           double amount,
                           long tableId,
                           String trxId);

    List<Transaction> getTransactionList();

    List<Transaction> getTransactionList(String customerName);

    List<Transaction> getTransactionList(String customerName,
                                         TransactionCode transactionCode);

    List<Transaction> getTransactionList(String customerName,
                                         TransactionCode transactionCode,
                                         long tableId);

    List<Transaction> getTransactionListByToCustomerName(String toCustomerName);

    List<Transaction> getTransactionListByToCustomerName(String toCustomerName,
                                                         TransactionCode transactionCode);

    /**
     * reference id from @{@link Entry} entryId or @{@link Transfer} transferId
     */
    List<Transaction> getTransactionListByTableId(long tableId);

    double getBalanceAmount(String customerName);

    Map<String, Double> getTotalAmountByCustomerNameWithToCustomerNameMap(String customerName,
                                                                          TransactionCode transactionCode);

    Map<String, Double> getTotalAmountByToCustomerNameWithCustomerNameMap(String toCustomerName,
                                                                          TransactionCode transactionCode);

    Map<String, Double> getTotalOwedAmount(String CustomerName);

    void remove(String trxId);
}
