package dkatalis.bank.service;

import dkatalis.bank.dao.TransactionDao;
import dkatalis.bank.enums.TransactionCode;
import dkatalis.bank.model.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionServiceImpl implements TransactionService {

    private final TransactionDao dao;

    public TransactionServiceImpl(TransactionDao dao) {
        this.dao = dao;
    }

    @Override
    public long createTransaction(String customerName,
                                  String toCustomerName,
                                  TransactionCode transactionCode,
                                  double amount,
                                  long tableId,
                                  String trxId) {
        final Transaction transaction = new Transaction();
        transaction.setCustomerName(customerName);
        transaction.setToCustomerName(toCustomerName);
        transaction.setTransactionCode(transactionCode);
        transaction.setAmount(amount);
        transaction.setTableId(tableId);
        transaction.setTrxId(trxId);
        return dao.createTransaction(transaction);
    }

    @Override
    public long createTransaction(String customerName,
                                  TransactionCode transactionCode,
                                  double amount,
                                  long tableId,
                                  String trxId) {
        return createTransaction(customerName, null, transactionCode, amount, tableId, trxId);
    }

    @Override
    public List<Transaction> getTransactionList() {
        return dao.getTransactionList();
    }

    @Override
    public List<Transaction> getTransactionList(String customerName) {
        final List<Transaction> transactionList = new ArrayList<>();
        for (Transaction transaction : getTransactionList()) {
            if (customerName.equals(transaction.getCustomerName()))
                transactionList.add(transaction);
        }
        return transactionList;
    }

    @Override
    public List<Transaction> getTransactionList(String customerName,
                                                TransactionCode transactionCode) {
        final List<Transaction> transactionList = new ArrayList<>();
        for (Transaction transaction : getTransactionList(customerName)) {
            if (transactionCode == transaction.getTransactionCode())
                transactionList.add(transaction);
        }
        return transactionList;
    }

    @Override
    public List<Transaction> getTransactionList(String customerName,
                                                TransactionCode transactionCode,
                                                long tableId) {
        final List<Transaction> transactionList = new ArrayList<>();
        for (Transaction transaction : getTransactionList(customerName, transactionCode)) {
            if (tableId == transaction.getTableId())
                transactionList.add(transaction);
        }
        return transactionList;
    }

    @Override
    public List<Transaction> getTransactionListByToCustomerName(String toCustomerName) {
        final List<Transaction> transactionList = new ArrayList<>();
        for (Transaction transaction : getTransactionList()) {
            if (toCustomerName.equals(transaction.getToCustomerName()))
                transactionList.add(transaction);
        }
        return transactionList;
    }

    @Override
    public List<Transaction> getTransactionListByToCustomerName(String toCustomerName,
                                                                TransactionCode transactionCode) {
        final List<Transaction> transactionList = new ArrayList<>();
        for (Transaction transaction : getTransactionListByToCustomerName(toCustomerName)) {
            if (transactionCode == transaction.getTransactionCode())
                transactionList.add(transaction);
        }
        return transactionList;
    }

    @Override
    public List<Transaction> getTransactionListByTableId(long tableId) {
        final List<Transaction> transactionList = new ArrayList<>();
        for (Transaction transaction : getTransactionList()) {
            if (tableId == transaction.getTableId())
                transactionList.add(transaction);
        }
        return transactionList;
    }

    @Override
    public double getBalanceAmount(String customerName) {
        double totalAmount = 0;
        for (Transaction transaction : getTransactionList(customerName)) {
            if (TransactionCode.DEPOSIT == transaction.getTransactionCode())
                totalAmount += transaction.getAmount();
            else if (TransactionCode.WITHDRAW == transaction.getTransactionCode() ||
                    TransactionCode.TRANSFER == transaction.getTransactionCode())
                totalAmount -= transaction.getAmount();
        }

        for (Transaction transaction : getTransactionListByToCustomerName(customerName)) {
            if (TransactionCode.TRANSFER == transaction.getTransactionCode())
                totalAmount += transaction.getAmount();
        }
        return totalAmount;
    }

    @Override
    public Map<String, Double> getTotalAmountByCustomerNameWithToCustomerNameMap(String customerName,
                                                                                 TransactionCode transactionCode) {
        final Map<String, Double> maps = new HashMap<>();
        for (Transaction transaction : getTransactionList(customerName, transactionCode)) {
            if (null == maps.get(transaction.getToCustomerName())) {
                maps.put(transaction.getToCustomerName(), transaction.getAmount());
            } else {
                final double amount = maps.get(transaction.getToCustomerName());
                maps.put(transaction.getToCustomerName(), amount + (transaction.getAmount()));
            }
        }
        return maps;
    }

    @Override
    public Map<String, Double> getTotalAmountByToCustomerNameWithCustomerNameMap(String toCustomerName,
                                                                                 TransactionCode transactionCode) {
        final Map<String, Double> maps = new HashMap<>();
        for (Transaction transaction : getTransactionListByToCustomerName(toCustomerName, transactionCode)) {
            if (null == maps.get(transaction.getCustomerName())) {
                maps.put(transaction.getCustomerName(), transaction.getAmount() * -1);
            } else {
                final double amount = maps.get(transaction.getCustomerName());
                maps.put(transaction.getCustomerName(), amount + (transaction.getAmount() * -1));
            }
        }
        return maps;
    }

    @Override
    public Map<String, Double> getTotalOwedAmount(String customerName) {
        // owed to
        final Map<String, Double> owedAmountToMap =
                getTotalAmountByCustomerNameWithToCustomerNameMap(customerName, TransactionCode.OWED);
        // owed from
        final Map<String, Double> owedAmountFromMap =
                getTotalAmountByToCustomerNameWithCustomerNameMap(customerName, TransactionCode.OWED);

        if (owedAmountToMap.isEmpty() && owedAmountFromMap.isEmpty())
            return new HashMap<>();

        final Map<String, Double> maps = new HashMap<>();

        if (!owedAmountToMap.isEmpty() && owedAmountFromMap.isEmpty())
            return owedAmountToMap;

        if (owedAmountToMap.isEmpty() && !owedAmountFromMap.isEmpty())
            return owedAmountFromMap;

        for (String customer : owedAmountToMap.keySet()) {
            if (null == owedAmountFromMap.get(customer))
                maps.put(customer, owedAmountToMap.get(customer));
            else
                maps.put(customer, owedAmountToMap.get(customer) + (owedAmountFromMap.get(customer)));
        }

        for (String toCustomer : owedAmountFromMap.keySet()) {
            if (null == owedAmountToMap.get(toCustomer))
                maps.put(toCustomer, owedAmountFromMap.get(toCustomer));
        }

        return maps;
    }

    @Override
    public void remove(String trxId) {
        dao.remove(trxId);
    }
}
