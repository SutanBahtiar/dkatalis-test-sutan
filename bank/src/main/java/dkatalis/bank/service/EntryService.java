package dkatalis.bank.service;

import dkatalis.bank.enums.TransactionCode;
import dkatalis.bank.model.Entry;

import java.util.List;

public interface EntryService {

    long createEntry(String customerName,
                     TransactionCode transactionCode,
                     double amount,
                     String refId);

    Entry getEntry(long entryId);

    List<Entry> getEntryList();

    List<Entry> getEntryList(String customerName);

    double getTotalAmount(String customerName);
}
