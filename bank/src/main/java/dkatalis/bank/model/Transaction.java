package dkatalis.bank.model;

import dkatalis.bank.enums.TransactionCode;

import java.util.Date;

public class Transaction {

    public static final long ERROR_SAVE_DATA = -1L;

    private long transactionId;
    private String customerName;
    private TransactionCode transactionCode;
    private String toCustomerName;
    private double amount;
    private String trxId;
    private long tableId;
    private Date createdDate;

    public Transaction() {
    }

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public TransactionCode getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(TransactionCode transactionCode) {
        this.transactionCode = transactionCode;
    }

    public String getToCustomerName() {
        return toCustomerName;
    }

    public void setToCustomerName(String toCustomerName) {
        this.toCustomerName = toCustomerName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTrxId() {
        return trxId;
    }

    public void setTrxId(String trxId) {
        this.trxId = trxId;
    }

    /**
     * @return reference id from @{@link Entry} entryId or @{@link Transfer} transferId
     */
    public long getTableId() {
        return tableId;
    }

    /**
     * @param tableId reference id from @{@link Entry} entryId or @{@link Transfer} transferId
     */
    public void setTableId(long tableId) {
        this.tableId = tableId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
