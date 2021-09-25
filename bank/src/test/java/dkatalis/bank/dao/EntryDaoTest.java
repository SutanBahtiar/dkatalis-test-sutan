package dkatalis.bank.dao;

import dkatalis.bank.enums.TransactionCode;
import dkatalis.bank.model.Entry;
import dkatalis.bank.util.Generator;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EntryDaoTest {

    private EntryDao entryDao;

    @Before
    public void init() {
        final Map<Long, Entry> entryMap = new HashMap<>();
        entryDao = new EntryDaoImpl(entryMap);
    }

    @Test
    public void testEntry() {
        final Entry entry = new Entry();
        entry.setCustomerName("alice");
        entry.setAmount(100);
        entry.setTransactionCode(TransactionCode.DEPOSIT);
        entry.setTrxId(Generator.generateTrxId());
        entryDao.createEntry(entry);

        final List<Entry> entryList = entryDao.getEntryList();
        assertEquals(entry.getCustomerName(), entryList.get(0).getCustomerName());
        assertEquals(entry.getAmount(), entryList.get(0).getAmount(), 0);
        assertEquals(new Date().getTime(), entryList.get(0).getCreatedDate().getTime(), 10);

        entryDao.remove(entryList.get(0).getTrxId());
        assertTrue(entryDao.getEntryList().isEmpty());
    }
}
