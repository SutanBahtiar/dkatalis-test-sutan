package dkatalis.bank.service;

import dkatalis.bank.dao.TransferDao;
import dkatalis.bank.enums.TransactionCode;
import dkatalis.bank.model.Transaction;
import dkatalis.bank.model.Transfer;
import dkatalis.bank.util.Generator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static dkatalis.bank.model.Transfer.TRANSFER_NOT_ALLOWED;

public class TransferServiceImpl implements TransferService {

    private static final Logger log = LoggerFactory.getLogger(TransferServiceImpl.class);

    private final TransferDao dao;
    private final TransactionService transactionService;

    public TransferServiceImpl(TransferDao dao,
                               TransactionService transactionService) {
        this.dao = dao;
        this.transactionService = transactionService;
    }

    @Override
    public synchronized long createTransfer(String customerName,
                                            String toCustomerName,
                                            double amount,
                                            String refId) {
        final String trxId = Generator.generateTrxId();

        if (log.isDebugEnabled())
            log.debug("{}, create transfer.. trxId:{}, customerName:{}, toCustomerName:{}, amount:{}"
                    , refId, trxId, customerName, toCustomerName, amount);

        try {
            // check balance
            final double balanceAmount = transactionService.getBalanceAmount(customerName);
            if (balanceAmount < 1) {
                if (log.isDebugEnabled()) {
                    log.debug("{}, transfer not allowed", refId);
                }
                return TRANSFER_NOT_ALLOWED;
            }

            if (customerName.equalsIgnoreCase(toCustomerName))
                return TRANSFER_NOT_ALLOWED;

            // transfer amount
            double transferAmount = amount;

            final Transfer transfer = new Transfer();
            transfer.setCustomerName(customerName);
            transfer.setToCustomerName(toCustomerName);
            transfer.setAmount(transferAmount);
            transfer.setRefId(refId);
            transfer.setTrxId(trxId);

            // save transfer
            final long transferId = dao.createTransfer(transfer);

            // check owed
            final Map<String, Double> owedAmountMap = transactionService.
                    getTotalAmountByToCustomerNameWithCustomerNameMap(customerName, TransactionCode.OWED);
            if (!owedAmountMap.isEmpty()) {
                // compare owed amount and transfer amount
                double owedAmount = owedAmountMap.get(toCustomerName);
                double diffAmount = transferAmount - owedAmount;

                // use transfer amount if owed amount more than transfer amount
                if (diffAmount <= 0) {
                    owedAmount = transferAmount * -1;

                    // save transaction as deduction owed
                    transactionService.createTransaction(customerName, toCustomerName, TransactionCode.OWED, owedAmount, transferId, trxId);
                }

                // update transfer amount
                transferAmount = diffAmount;
            }

            // stop next step if not enough transfer amount
            if (transferAmount <= 0) return transferId;

            // if balance is not enough, create part of the transaction
            if (balanceAmount < transferAmount) {
                // save transaction with the balance amount
                transactionService.createTransaction(customerName, toCustomerName, TransactionCode.TRANSFER, balanceAmount, transferId, trxId);
                // save transfer with the drawback
                transactionService.createTransaction(customerName, toCustomerName, TransactionCode.OWED, balanceAmount - transferAmount, transferId, trxId);
                return transferId;
            }

            // save transaction
            transactionService.createTransaction(customerName, toCustomerName, TransactionCode.TRANSFER, transferAmount, transferId, trxId);

            return transferId;
        } catch (Exception e) {
            transactionService.remove(trxId);
            dao.remove(trxId);
            log.error(e.getMessage(), e);
        }

        return Transaction.ERROR_SAVE_DATA;
    }

    @Override
    public Transfer getTransfer(long transferId) {
        return dao.getTransfer(transferId);
    }

    @Override
    public List<Transfer> getTransferList() {
        return dao.getTransferList();
    }
}
