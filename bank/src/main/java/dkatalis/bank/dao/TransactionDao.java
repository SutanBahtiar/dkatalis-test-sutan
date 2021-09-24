package dkatalis.bank.dao;

import dkatalis.bank.model.Transaction;

import java.util.List;

public interface TransactionDao {

    long createTransaction(Transaction transaction);

    Transaction getTransaction(long transactionId);

    List<Transaction> getTransactionList();

    void remove(String trxId);
}
