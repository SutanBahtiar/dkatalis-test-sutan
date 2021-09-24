package dkatalis.bank.dao;

import dkatalis.bank.model.Transfer;

import java.util.List;

public interface TransferDao {

    long createTransfer(Transfer transfer);

    Transfer getTransfer(long transferId);

    List<Transfer> getTransferList();

    void remove(String trxId);
}
