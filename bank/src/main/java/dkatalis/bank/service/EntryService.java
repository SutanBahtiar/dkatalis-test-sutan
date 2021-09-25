package dkatalis.bank.service;

import dkatalis.bank.model.Entry;

import java.util.List;

public interface EntryService {

    long createWithdraw(String customerName,
                        double amount,
                        String refId);

    long createDeposit(String customerName,
                       double amount,
                       String refId);

    Entry getEntry(long entryId);

    List<Entry> getEntryList();
}
