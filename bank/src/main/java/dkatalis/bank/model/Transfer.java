package dkatalis.bank.model;

import java.util.Date;

public class Transfer {

    public static final long TRANSFER_NOT_ALLOWED = -1L;

    private long transferId;
    private String customerName;
    private String toCustomerName;
    private double amount;
    private String trxId;
    private String refId;
    private Date createdDate;

    public Transfer() {
    }

    public long getTransferId() {
        return transferId;
    }

    public void setTransferId(long transferId) {
        this.transferId = transferId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "{" +
                "customerName='" + customerName + '\'' +
                ", toCustomerName='" + toCustomerName + '\'' +
                ", amount=" + amount +
                '}';
    }
}
