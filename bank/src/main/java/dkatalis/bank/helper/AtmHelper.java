package dkatalis.bank.helper;

import dkatalis.bank.enums.Commands;
import dkatalis.bank.model.Transaction;

import java.util.List;
import java.util.Map;

public class AtmHelper {

    public static final long AMOUNT_NOT_ELIGIBLE = -1;
    private static final String SEPARATOR = "\\|";
    private static final String SEPARATOR_USERNAME = "#";

    public static String createLogoutMessage(String customerName) {
        return "-" + SEPARATOR_USERNAME + "Good Bye, " + initCap(customerName) + "!";
    }

    public static String createTransferMessage(String toCustomerName,
                                               double transferAmount,
                                               double balanceAmount,
                                               double owedAmount,
                                               boolean isJustDeductOwed) {
        final String initCapToCustomerName = initCap(toCustomerName);
        final StringBuilder messageBuilder = new StringBuilder();
        if (!isJustDeductOwed) {
            messageBuilder
                    .append("Transferred $")
                    .append(transferAmount)
                    .append(" to ")
                    .append(initCapToCustomerName)
                    .append("\n")
                    .append("Your balance is $")
                    .append(balanceAmount);
        } else {
            messageBuilder
                    .append("Your balance is $")
                    .append(balanceAmount);
        }
        if (owedAmount != 0) {
            messageBuilder
                    .append("\n")
                    .append("Owed $")
                    .append(owedAmount < 0 ? owedAmount * -1 : owedAmount)
                    .append(owedAmount < 0 ? " to " : " from ")
                    .append(initCapToCustomerName);
        }
        return messageBuilder.toString();
    }

    public static String createDepositMessage(double balanceAmount,
                                              List<Transaction> owedTransferList,
                                              Map<String, Double> owedAmountMap) {
        final StringBuilder messageBuilder = new StringBuilder();
        for (Transaction transaction : owedTransferList) {
            final String initCapToCustomerName = initCap(transaction.getToCustomerName());
            messageBuilder
                    .append("Transferred $")
                    .append(transaction.getAmount())
                    .append(" to ")
                    .append(initCapToCustomerName)
                    .append("\n")
                    .append("Your balance is $")
                    .append(balanceAmount < 0 ? 0 : balanceAmount);
        }
        if (!owedAmountMap.isEmpty()) {
            for (String toCustomerName : owedAmountMap.keySet()) {
                final double owedAmount = owedAmountMap.get(toCustomerName);
                final String initCapToCustomerName = initCap(toCustomerName);
                messageBuilder
                        .append("\n")
                        .append("Owed $")
                        .append(owedAmount < 0 ? owedAmount * -1 : owedAmount)
                        .append(owedAmount < 0 ? " to " : " from ")
                        .append(initCapToCustomerName);
            }
        }
        return messageBuilder.toString();
    }

    public static String createDepositMessage(double balance) {
        return "Your balance is $" + balance;
    }

    public static String createLoginMessage(String customerName,
                                            double balance,
                                            Map<String, Double> owedAmountMap) {
        final String initCapCustomerName = initCap(customerName);
        final StringBuilder messageBuilder = new StringBuilder();
        messageBuilder
                .append(customerName)
                .append(SEPARATOR_USERNAME)
                .append("Hello, ")
                .append(initCapCustomerName)
                .append("!\n")
                .append("Your balance is $")
                .append(balance);
        if (!owedAmountMap.isEmpty()) {
            for (String toCustomerName : owedAmountMap.keySet()) {
                final double owedAmount = owedAmountMap.get(toCustomerName);
                final String initCapToCustomerName = initCap(toCustomerName);
                messageBuilder
                        .append("\n")
                        .append("Owed $")
                        .append(owedAmount < 0 ? owedAmount * -1 : owedAmount)
                        .append(owedAmount < 0 ? " to " : " from ")
                        .append(initCapToCustomerName);
            }
        }
        return messageBuilder.toString();
    }

    public static String initCap(String customerName) {
        return customerName.substring(0, 1).toUpperCase()
                + customerName.substring(1);
    }

    public static Commands commands(String[] messages) {
        return Commands.valueOf(getCommands(messages));
    }

    public static String getCommands(String[] messages) {
        return messages[1];
    }

    public static String customerName(String[] messages) {
        return messages[0].toLowerCase();
    }

    public static String customerNameParam(String[] messages) {
        return messages[2].toLowerCase();
    }

    public static String toCustomerName(String[] messages) {
        return messages[2].toLowerCase();
    }

    public static double amount(String[] messages) {
        return parseDouble(messages[2]);
    }

    public static double transferAmount(String[] messages) {
        return parseDouble(messages[3]);
    }

    public static double parseDouble(String amount) {
        try {
            return Double.parseDouble(amount);
        } catch (NumberFormatException e) {
            return AMOUNT_NOT_ELIGIBLE;
        }
    }

    public static String[] messages(String message) {
        return message.split(SEPARATOR);
    }
}
