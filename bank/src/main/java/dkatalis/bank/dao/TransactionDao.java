package dkatalis.bank.dao;

import dkatalis.bank.model.Transaction;

import java.util.List;

public interface TransactionDao {

    long createTransaction(Transaction transaction);

    List<Transaction> getTransactionList();

    void remove(String trxId);
}
