package dkatalis.atm.helper;

import dkatalis.atm.enums.Commands;
import org.apache.commons.lang3.StringUtils;

public class CommandsHelper {

    private static final String SPACE = " ";
    private static final String SEPARATOR = "|";
    private static final String UNKNOWN_CUSTOMER_NAME = "-";

    public static Message setMessage(String customerName,
                                     String message) {
        if (null == message)
            return new Message(null, setHelpMessage(customerName, null));

        if (!message.contains(SPACE)) {
            if (message.equalsIgnoreCase(Commands.ADMIN.name()))
                return new Message(customerName + SEPARATOR + Commands.ADMIN.name(), null);


            if (isCorrect(message)
                    && message.equalsIgnoreCase(Commands.LOGOUT.name())
                    && !customerName.equals(UNKNOWN_CUSTOMER_NAME))
                return new Message(customerName + SEPARATOR + Commands.LOGOUT.name(), null);

            return new Message(null, setHelpMessage(customerName, Commands.LOGOUT));
        }

        final String[] messages = message.trim().split(SPACE);
        final String commands = messages[0];

        if (!isCorrect(commands))
            return new Message(null, setHelpMessage(customerName, null));

        // ex: login alice
        if (commands.equalsIgnoreCase(Commands.LOGIN.name())) {
            if (messages.length != 2)
                return new Message(null, setHelpMessage(customerName, Commands.LOGIN));

            final String customerNameParam = messages[1];
            if (!isCorrect(customerNameParam))
                return new Message(null, setHelpMessage(customerName, Commands.LOGIN));

            return new Message(customerName + SEPARATOR + Commands.LOGIN.name() + SEPARATOR + customerNameParam);
        }
        // ex : deposit 100
        else if (commands.equalsIgnoreCase(Commands.DEPOSIT.name())) {
            if (messages.length != 2 || customerName.equals(UNKNOWN_CUSTOMER_NAME))
                return new Message(null, setHelpMessage(customerName, Commands.DEPOSIT));

            final String depositAmount = messages[1];
            if (!isCorrectAmount(depositAmount))
                return new Message(null, setHelpMessage(customerName, Commands.DEPOSIT));

            return new Message(customerName + SEPARATOR + Commands.DEPOSIT.name() + SEPARATOR + depositAmount);
        }
        // ex : withdraw 100
        else if (commands.equalsIgnoreCase(Commands.WITHDRAW.name())) {
            if (messages.length != 2 || customerName.equals(UNKNOWN_CUSTOMER_NAME))
                return new Message(null, setHelpMessage(customerName, Commands.WITHDRAW));

            final String withdrawAmount = messages[1];
            if (!isCorrectAmount(withdrawAmount))
                return new Message(null, setHelpMessage(customerName, Commands.WITHDRAW));

            return new Message(customerName + SEPARATOR + Commands.WITHDRAW.name() + SEPARATOR + withdrawAmount);
        }
        // ex: transfer bob 50
        else if (commands.equalsIgnoreCase(Commands.TRANSFER.name())) {
            if (messages.length != 3 || customerName.equals(UNKNOWN_CUSTOMER_NAME))
                return new Message(null, setHelpMessage(customerName, Commands.TRANSFER));

            final String toCustomerName = messages[1];
            final String transferAmount = messages[2];
            if (!isCorrect(toCustomerName) || !isCorrectAmount(transferAmount))
                return new Message(null, setHelpMessage(customerName, Commands.TRANSFER));

            return new Message(customerName + SEPARATOR + Commands.TRANSFER.name() + SEPARATOR + toCustomerName + '|' + transferAmount);
        }

        return new Message(null, setHelpMessage(customerName, null));
    }

    public static String setHelpMessage(String customerName,
                                        Commands commands) {
        if (UNKNOWN_CUSTOMER_NAME.equals(customerName))
            return setHelpMessage(Commands.LOGIN);

        if (commands.equals(Commands.LOGIN))
            return setHelpMessage(Commands.LOGOUT);

        return setHelpMessage(commands);
    }

    public static String setHelpMessage(Commands commands) {
        switch (commands) {
            case LOGIN:
                return MessageHelper.helpLoginMessage();
            case LOGOUT:
                return MessageHelper.helpLogoutMessage();
            case DEPOSIT:
                return MessageHelper.helpDepositMessage();
            case TRANSFER:
                return MessageHelper.helpTransferMessage();
            case WITHDRAW:
                return MessageHelper.helpWithdrawMessage();
            default:
                return MessageHelper.helpMessage();
        }
    }

    public static boolean isCorrect(String commands) {
        return StringUtils.isAlpha(commands);
    }

    public static boolean isCorrectAmount(String amount) {
        if (!StringUtils.isNumeric(amount))
            return false;

        try {
            Double.parseDouble(amount);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static class Message {
        private final String message;
        private String helpMessage;

        public Message(String message,
                       String helpMessage) {
            this.message = message;
            this.helpMessage = helpMessage;
        }

        public Message(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public String getHelpMessage() {
            return helpMessage;
        }

        @Override
        public String toString() {
            return "Message{" +
                    "message='" + message + '\'' +
                    ", helpMessage='" + helpMessage + '\'' +
                    '}';
        }
    }

}
