package dkatalis.bank.dao;

import dkatalis.bank.model.Transaction;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionDaoImpl extends MapDao<Transaction> implements TransactionDao {

    public TransactionDaoImpl(Map<Long, Transaction> dataSource) {
        super(dataSource);
    }

    @Override
    public long createTransaction(Transaction transaction) {
        transaction.setTransactionId(super.getId());
        transaction.setCreatedDate(new Date());
        return super.save(transaction, transaction.getTransactionId());
    }

    @Override
    public List<Transaction> getTransactionList() {
        return super.getList();
    }

    @Override
    public void remove(String trxId) {
        final Map<Long, Transaction> transactionMap = new HashMap<>();
        for (Transaction transaction : super.dataSource.values()) {
            if (trxId.equals(transaction.getTrxId()))
                transactionMap.put(transaction.getTransactionId(), transaction);
        }

        if (!transactionMap.isEmpty())
            transactionMap.values().forEach(transaction -> super.dataSource
                    .remove(transaction.getTransactionId(), transaction));
    }
}
