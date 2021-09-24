package dkatalis.bank.dao;

import dkatalis.bank.model.Transfer;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransferDaoImpl extends MapDao<Transfer> implements TransferDao {

    public TransferDaoImpl(Map<Long, Transfer> dataSource) {
        super(dataSource);
    }

    @Override
    public long createTransfer(Transfer transfer) {
        transfer.setTransferId(super.getId());
        transfer.setCreatedDate(new Date());
        return super.save(transfer, transfer.getTransferId());
    }

    @Override
    public Transfer getTransfer(long transferId) {
        return super.get(transferId);
    }

    @Override
    public List<Transfer> getTransferList() {
        return super.getList();
    }

    @Override
    public void remove(String trxId) {
        final Map<Long, Transfer> transferMap = new HashMap<>();
        for (Transfer transfer : super.dataSource.values()) {
            if (trxId.equals(transfer.getTrxId()))
                transferMap.put(transfer.getTransferId(), transfer);
        }

        if (!transferMap.isEmpty())
            transferMap.values().forEach(transfer -> super.dataSource
                    .remove(transfer.getTransferId(), transfer));
    }
}
