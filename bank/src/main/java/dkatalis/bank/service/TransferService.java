package dkatalis.bank.service;

import dkatalis.bank.model.Transfer;

import java.util.List;

public interface TransferService {

    long createTransfer(String customerName,
                        String toCustomerName,
                        double amount,
                        String refId);

    Transfer getTransfer(long transferId);

    List<Transfer> getTransferList();
}
