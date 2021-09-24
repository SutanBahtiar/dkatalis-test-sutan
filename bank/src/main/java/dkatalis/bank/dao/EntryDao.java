package dkatalis.bank.dao;

import dkatalis.bank.model.Entry;

import java.util.List;

public interface EntryDao {

    long createEntry(Entry entry);

    Entry getEntry(long entryId);

    List<Entry> getEntryList();

    void remove(String trxId);
}
