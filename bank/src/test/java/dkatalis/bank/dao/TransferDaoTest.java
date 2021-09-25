package dkatalis.bank.dao;

import dkatalis.bank.model.Transfer;
import dkatalis.bank.util.Generator;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TransferDaoTest {

    private TransferDao transferDao;

    @Before
    public void init() {
        final Map<Long, Transfer> transferMap = new HashMap<>();
        transferDao = new TransferDaoImpl(transferMap);
    }

    @Test
    public void testTransfer() {
        final Transfer transfer = new Transfer();
        transfer.setCustomerName("bob");
        transfer.setAmount(100);
        transfer.setTrxId(Generator.generateTrxId());
        transfer.setRefId(Generator.generateRefId());
        transfer.setToCustomerName("alice");
        transferDao.createTransfer(transfer);

        final List<Transfer> transferList = transferDao.getTransferList();
        assertEquals(transfer.getCustomerName(), transferList.get(0).getCustomerName());
        assertEquals(transfer.getAmount(), transferList.get(0).getAmount(), 0);
        assertEquals(new Date().getTime(), transferList.get(0).getCreatedDate().getTime(), 10);

        transferDao.remove(transfer.getTrxId());
        assertTrue(transferDao.getTransferList().isEmpty());
    }
}
