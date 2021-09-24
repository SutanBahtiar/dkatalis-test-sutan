package dkatalis.bank.dao;

import dkatalis.bank.model.Entry;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntryDaoImpl extends MapDao<Entry> implements EntryDao {

    public EntryDaoImpl(Map<Long, Entry> dataSource) {
        super(dataSource);
    }

    @Override
    public long createEntry(Entry entry) {
        entry.setEntryId(super.getId());
        entry.setCreatedDate(new Date());
        return super.save(entry, entry.getEntryId());
    }

    @Override
    public Entry getEntry(long entryId) {
        return super.get(entryId);
    }

    @Override
    public List<Entry> getEntryList() {
        return super.getList();
    }

    @Override
    public void remove(String trxId) {
        final Map<Long, Entry> entryMap = new HashMap<>();
        for (Entry entry : super.dataSource.values()) {
            if (trxId.equals(entry.getTrxId()))
                entryMap.put(entry.getEntryId(), entry);
        }

        if (!entryMap.isEmpty())
            entryMap.values().forEach(entry -> super.dataSource
                    .remove(entry.getEntryId(), entry));
    }

}
