package dkatalis.bank.service;

import dkatalis.bank.enums.Commands;
import dkatalis.bank.enums.TransactionCode;
import dkatalis.bank.helper.AtmHelper;
import dkatalis.bank.model.Entry;
import dkatalis.bank.model.Transaction;
import dkatalis.bank.model.Transfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static dkatalis.bank.model.Transfer.TRANSFER_NOT_ALLOWED;

public class AtmServiceImpl implements AtmService {

    private static final Logger log = LoggerFactory.getLogger(AtmServiceImpl.class);
    private static final String UNKNOWN_CUSTOMER_NAME = "-";

    private final AccountService accountService;
    private final EntryService entryService;
    private final TransferService transferService;
    private final TransactionService transactionService;

    public AtmServiceImpl(AccountService accountService,
                          EntryService entryService,
                          TransferService transferService,
                          TransactionService transactionService) {
        this.accountService = accountService;
        this.entryService = entryService;
        this.transferService = transferService;
        this.transactionService = transactionService;
    }

    @Override
    public String login(String message,
                        String refId) {
        if (log.isDebugEnabled())
            log.debug("{}, call login commands, message:{}", refId, message);

        // expected message: customerNameLogin|Commands.LOGIN|customerName
        final String[] messages = AtmHelper.messages(message);
        if (messages.length < 3)
            return null;

        // get commands
        final Commands commands = AtmHelper.commands(messages);
        if (commands != Commands.LOGIN)
            return null;

        // get customerName login
        final String customerNameLogin = AtmHelper.customerName(messages);
        // check customerName its already logged on or not
        if (!UNKNOWN_CUSTOMER_NAME.equals(customerNameLogin))
            return null;

        // get customerName param
        final String customerName = AtmHelper.customerNameParam(messages);

        // check customerName param have account or not, if not create the account
        if (null == accountService.getAccount(customerName))
            if (null == accountService.createAccount(customerName, refId))
                return null;

        return doLogin(customerName);
    }

    public String doLogin(String customerName) {
        final double balanceAmount = transactionService.getBalanceAmount(customerName);
        final Map<String, Double> owedAmountMap = transactionService.getTotalOwedAmountMap(customerName);
        return AtmHelper.createLoginMessage(customerName, balanceAmount, owedAmountMap);
    }

    @Override
    public synchronized String deposit(String message,
                                       String refId) {
        if (log.isDebugEnabled())
            log.debug("{}, call deposit commands, message:{}", refId, message);

        // expected message: customerNameLogin|Commands.DEPOSIT|amount
        final String[] messages = AtmHelper.messages(message);
        if (messages.length < 3)
            return null;

        // get commands
        final Commands commands = AtmHelper.commands(messages);
        if (commands != Commands.DEPOSIT)
            return null;

        // get customerName login
        final String customerNameLogin = AtmHelper.customerName(messages);
        // check customerName its already logged on or not
        if (UNKNOWN_CUSTOMER_NAME.equals(customerNameLogin))
            return null;

        // check account
        if (null == accountService.getAccount(customerNameLogin))
            return null;

        // get amount
        final double amount = AtmHelper.amount(messages);
        if (amount == AtmHelper.AMOUNT_NOT_ELIGIBLE)
            return null;

        return doDeposit(customerNameLogin, amount, refId);
    }

    public String doDeposit(String customerName,
                            double amount,
                            String refId) {
        // save deposit
        final long entryId = entryService.createDeposit(customerName, amount, refId);

        // balance amount after deposit
        final double balanceAmount = transactionService.getBalanceAmount(customerName);

        // get transferred owed
        final List<Transaction> owedTransferList = transactionService.getTransactionList(customerName, TransactionCode.OWED, entryId);
        if (!owedTransferList.isEmpty()) {
            final Map<String, Double> owedAmountMap = transactionService.getTotalOwedAmountMap(customerName);
            return AtmHelper.createDepositMessage(balanceAmount, owedTransferList, owedAmountMap);
        }

        return AtmHelper.createDepositMessage(balanceAmount);
    }

    @Override
    public String withdraw(String message,
                           String refId) {
        if (log.isDebugEnabled())
            log.debug("{}, call withdraw commands, message:{}", refId, message);

        // expected message: customerNameLogin|Commands.WITHDRAW|amount
        final String[] messages = AtmHelper.messages(message);
        if (messages.length < 3)
            return null;

        // get commands
        final Commands commands = AtmHelper.commands(messages);
        if (commands != Commands.WITHDRAW)
            return null;

        // get customerName login
        final String customerNameLogin = AtmHelper.customerName(messages);
        // check customerName its already logged on or not
        if (UNKNOWN_CUSTOMER_NAME.equals(customerNameLogin))
            return null;

        // check account
        if (null == accountService.getAccount(customerNameLogin))
            return null;

        // get amount
        final double amount = AtmHelper.amount(messages);
        if (amount == AtmHelper.AMOUNT_NOT_ELIGIBLE)
            return null;

        return doWithdraw(customerNameLogin, amount, refId);
    }

    public String doWithdraw(String customerName,
                             double amount,
                             String refId) {
        // save withdraw
        final long entryId = entryService.createWithdraw(customerName, amount, refId);
        if (TRANSFER_NOT_ALLOWED != entryId) {
            final double balanceAmount = transactionService.getBalanceAmount(customerName);
            return AtmHelper.createWithdrawMessage(balanceAmount);
        }

        if (log.isDebugEnabled())
            log.debug("{}, balance is not sufficient..", refId);

        return AtmHelper.createWithdrawMessage(0);
    }

    @Override
    public synchronized String transfer(String message,
                                        String refId) {
        if (log.isDebugEnabled())
            log.debug("{}, call transfer commands, message:{}", refId, message);

        // expected message: customerNameLogin|Commands.TRANSFER|toCustomerName|amount
        final String[] messages = AtmHelper.messages(message);
        if (messages.length < 4)
            return null;

        // get commands
        final Commands commands = AtmHelper.commands(messages);
        if (commands != Commands.TRANSFER)
            return null;

        // get customerName login
        final String customerNameLogin = AtmHelper.customerName(messages);
        // check customerName its already logged on or not
        if (UNKNOWN_CUSTOMER_NAME.equals(customerNameLogin))
            return null;

        // check account
        if (null == accountService.getAccount(customerNameLogin))
            return null;

        // get to customerName
        final String toCustomerName = AtmHelper.toCustomerName(messages);
        // check account
        if (null == accountService.getAccount(toCustomerName))
            return null;

        if (customerNameLogin.equalsIgnoreCase(toCustomerName))
            return null;

        // get amount
        final double amount = AtmHelper.transferAmount(messages);
        if (amount == AtmHelper.AMOUNT_NOT_ELIGIBLE)
            return null;

        return doTransfer(customerNameLogin, toCustomerName, amount, refId);
    }

    public String doTransfer(String customerName,
                             String toCustomerName,
                             double amount,
                             String refId) {
        // check balance amount first
        double balanceAmount = transactionService.getBalanceAmount(customerName);

        // check owed before transaction
        double owedAmount = 0;
        Map<String, Double> mapAmountMap = transactionService.getTotalOwedAmountMap(customerName);
        if (!mapAmountMap.isEmpty())
            owedAmount = mapAmountMap.get(toCustomerName);

        // save transfer
        final long transferId = transferService.createTransfer(customerName, toCustomerName, amount, refId);
        if (TRANSFER_NOT_ALLOWED == transferId)
            return AtmHelper.createDepositMessage(balanceAmount);

        // balanceAmount > amount ? amount : balanceAmount;
        final double transferAmount = Math.min(balanceAmount, amount);

        final boolean isJustDeductOwed = owedAmount > transferAmount;

        // balance amount after transfer
        balanceAmount = transactionService.getBalanceAmount(customerName);

        // check owed after transaction
        mapAmountMap = transactionService.getTotalOwedAmountMap(customerName);
        if (!mapAmountMap.isEmpty())
            owedAmount = mapAmountMap.get(toCustomerName);

        return AtmHelper.createTransferMessage(toCustomerName, transferAmount, balanceAmount, owedAmount, isJustDeductOwed);
    }

    @Override
    public String logout(String message,
                         String refId) {
        if (log.isDebugEnabled())
            log.debug("{}, call logout commands, message:{}", refId, message);

        // expected message: customerNameLogin|Commands.LOGOUT
        final String[] messages = AtmHelper.messages(message);
        if (messages.length < 2)
            return null;

        // get customerName login
        final String customerNameLogin = AtmHelper.customerName(messages);
        // check customerName its already logged on or not
        if (UNKNOWN_CUSTOMER_NAME.equals(customerNameLogin))
            return null;

        return AtmHelper.createLogoutMessage(customerNameLogin);
    }

    @Override
    public String admin(String message,
                        String refId) {
        if (log.isDebugEnabled())
            log.debug("{}, call admin commands, message:{}", refId, message);

        // expected message: customerNameLogin|Commands.ADMIN
        final String[] messages = AtmHelper.messages(message);
        if (messages.length < 2)
            return null;

        // get commands
        final Commands commands = AtmHelper.commands(messages);
        if (commands != Commands.ADMIN)
            return null;

        showTable();

        return null;
    }

    public void showTable() {
        final List<Transaction> sortTransaction = transactionService.getTransactionList();
        sortTransaction.sort(Comparator.comparing(Transaction::getTransactionId));

        System.out.println();
        System.out.println("[TRANSACTION]");
        System.out.printf("%-20s %-15s %-15s %-15s %-10s %-15s %-20s %-20s\n"
                , "TransactionId"
                , "CustomerName"
                , "ToCustomerName"
                , "TransactionCode"
                , "Amount"
                , "TrxId"
                , "Table Id"
                , "CreatedDate"
        );
        for (Transaction transaction : sortTransaction) {
            System.out.printf("%-20s %-15s %-15s %-15s %-10s %-15s %-20s %-20s\n"
                    , transaction.getTransactionId()
                    , transaction.getCustomerName()
                    , transaction.getToCustomerName()
                    , transaction.getTransactionCode().name()
                    , transaction.getAmount()
                    , transaction.getTrxId()
                    , transaction.getTableId()
                    , transaction.getCreatedDate()
            );
        }

        System.out.println();
        System.out.println("[ENTRY]");

        final List<Entry> sortEntry = entryService.getEntryList();
        sortEntry.sort(Comparator.comparing(Entry::getEntryId));

        System.out.printf("%-20s %-15s %-15s %-10s %-15s %-15s %-20s\n"
                , "EntryId"
                , "CustomerName"
                , "TransactionCode"
                , "Amount"
                , "TrxId"
                , "RefId"
                , "CreatedDate"
        );
        for (Entry entry : sortEntry) {
            System.out.printf("%-20s %-15s %-15s %-10s %-15s %-15s %-20s\n"
                    , entry.getEntryId()
                    , entry.getCustomerName()
                    , entry.getTransactionCode().name()
                    , entry.getAmount()
                    , entry.getTrxId()
                    , entry.getRefId()
                    , entry.getCreatedDate()
            );
        }

        System.out.println();
        System.out.println("[TRANSFER]");

        final List<Transfer> sortTransfer = transferService.getTransferList();
        sortTransfer.sort(Comparator.comparing(Transfer::getTransferId));

        System.out.printf("%-20s %-15s %-15s %-10s %-15s %-15s %-20s\n"
                , "TransferId"
                , "CustomerName"
                , "ToCustomerName"
                , "Amount"
                , "TrxId"
                , "RefId"
                , "CreatedDate"
        );

        for (Transfer transfer : transferService.getTransferList()) {
            System.out.printf("%-20s %-15s %-15s %-10s %-15s %-15s %-20s\n"
                    , transfer.getTransferId()
                    , transfer.getCustomerName()
                    , transfer.getToCustomerName()
                    , transfer.getAmount()
                    , transfer.getTrxId()
                    , transfer.getRefId()
                    , transfer.getCreatedDate()
            );
        }

        System.out.println();
    }
}
