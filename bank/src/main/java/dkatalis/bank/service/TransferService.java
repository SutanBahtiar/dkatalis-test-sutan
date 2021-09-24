package dkatalis.bank.service;

import dkatalis.bank.model.Transfer;

import java.util.List;
import java.util.Map;

public interface TransferService {

    long createTransfer(String customerName,
                        String toCustomerName,
                        double amount,
                        String refId);

    Transfer getTransfer(long transferId);

    List<Transfer> getTransferList();

    List<Transfer> getTransferList(String customerName);

    List<Transfer> getTransferList(String customerName,
                                   String toCustomerName);

    double getTotalAmount(String customerName);

    double getTotalAmount(String customerName,
                          String toCustomerName);

    Map<String, Double> getTotalAmountWithToCustomerNameMap(String customerName);
}
