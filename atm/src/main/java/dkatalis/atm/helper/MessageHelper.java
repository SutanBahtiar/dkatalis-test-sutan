package dkatalis.atm.helper;

public class MessageHelper {

    public static String helpMessage() {
        return "Usage:\n" +
                "\tdeposit [amount]\n" +
                "\t  [amount] <numeric>\n" +
                "\t  example: deposit 1000\n" +
                "\twithdraw [amount]\n" +
                " \t  [amount] <numeric>\n" +
                " \t  example: withdraw 1500\n" +
                "\ttransfer [target] [amount]\n" +
                " \t  [target] <customer name target>\n" +
                " \t  [amount] <numeric>\n" +
                " \t  example : transfer Alice 150\n" +
                "\tlogout\n" +
                " \t  close your session\n";
    }

    public static String helpLoginMessage() {
        return "Usage:\n" +
                "\tlogin [name]\n" +
                "\t  [name] <alphabet>\n" +
                "\t  example: login Alice\n";
    }

    public static String helpLogoutMessage() {
        return "Usage:\n" +
                "\tlogout\n";
    }

    public static String helpTransferMessage() {
        return "Usage:\n" +
                "\ttransfer [target] [amount]\n" +
                " \t  [target] <customer name target>\n" +
                " \t  [amount] <numeric>\n" +
                " \t  example : transfer Alice 150\n";
    }

    public static String helpWithdrawMessage() {
        return "Usage:\n" +
                "\twithdraw [amount]\n" +
                " \t  [amount] <numeric>\n" +
                " \t  example: withdraw 1500\n";
    }

    public static String helpDepositMessage() {
        return "Usage:\n" +
                "\tdeposit [amount]\n" +
                "\t  [amount] <numeric>\n" +
                "\t  example: deposit 1000\n";
    }

}
