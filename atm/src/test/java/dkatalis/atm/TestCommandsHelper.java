package dkatalis.atm;

import dkatalis.atm.enums.Commands;
import dkatalis.atm.helper.CommandsHelper;
import dkatalis.atm.helper.MessageHelper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TestCommandsHelper {

    @Test
    public void testIsCorrect() {
        final String commands = "login";
        assertTrue(CommandsHelper.isCorrect(commands));

        final String amount = "100";
        assertTrue(CommandsHelper.isCorrectAmount(amount));

        assertTrue(CommandsHelper.parseDouble(amount));
        assertFalse(CommandsHelper.parseDouble(null));
        assertFalse(CommandsHelper.parseDouble("alice"));
    }

    @Test
    public void testSetMessage() {
        String customerName = "alice";
        String message = "login";
        assertEquals(MessageHelper.helpLogoutMessage(),
                CommandsHelper.setMessage(customerName, message).getHelpMessage());

        customerName = "alice";
        message = "login -";
        assertEquals(MessageHelper.helpLogoutMessage(),
                CommandsHelper.setMessage(customerName, message).getHelpMessage());

        customerName = "-";
        message = "logout";
        assertEquals(MessageHelper.helpLoginMessage(),
                CommandsHelper.setMessage(customerName, message).getHelpMessage());

        customerName = "-";
        message = "login -";
        assertEquals(MessageHelper.helpLoginMessage(),
                CommandsHelper.setMessage(customerName, message).getHelpMessage());

        customerName = "-";
        message = "deposit -";
        assertEquals(MessageHelper.helpLoginMessage(),
                CommandsHelper.setMessage(customerName, message).getHelpMessage());

        customerName = "-";
        message = "deposit 50";
        assertEquals(MessageHelper.helpLoginMessage(),
                CommandsHelper.setMessage(customerName, message).getHelpMessage());

        customerName = "-";
        message = "withdraw 50";
        assertEquals(MessageHelper.helpLoginMessage(),
                CommandsHelper.setMessage(customerName, message).getHelpMessage());

        customerName = "-";
        message = "transfer 50";
        assertEquals(MessageHelper.helpLoginMessage(),
                CommandsHelper.setMessage(customerName, message).getHelpMessage());

        customerName = "-";
        message = "transfer alice 50";
        assertEquals(MessageHelper.helpLoginMessage(),
                CommandsHelper.setMessage(customerName, message).getHelpMessage());

        customerName = "alice";
        message = "deposit -";
        assertEquals(MessageHelper.helpDepositMessage(),
                CommandsHelper.setMessage(customerName, message).getHelpMessage());

        customerName = "alice";
        message = "deposit bob 100";
        assertEquals(MessageHelper.helpDepositMessage(),
                CommandsHelper.setMessage(customerName, message).getHelpMessage());

        customerName = "-";
        message = "withdraw -";
        assertEquals(MessageHelper.helpLoginMessage(),
                CommandsHelper.setMessage(customerName, message).getHelpMessage());

        customerName = "alice";
        message = "withdraw -";
        assertEquals(MessageHelper.helpWithdrawMessage(),
                CommandsHelper.setMessage(customerName, message).getHelpMessage());

        customerName = "alice";
        message = "withdraw alice 100";
        assertEquals(MessageHelper.helpWithdrawMessage(),
                CommandsHelper.setMessage(customerName, message).getHelpMessage());

        customerName = "-";
        message = "transfer -";
        assertEquals(MessageHelper.helpLoginMessage(),
                CommandsHelper.setMessage(customerName, message).getHelpMessage());

        customerName = "alice";
        message = "transfer 100";
        assertEquals(MessageHelper.helpTransferMessage(),
                CommandsHelper.setMessage(customerName, message).getHelpMessage());

        customerName = "alice";
        message = "transfer 100 bob";
        assertEquals(MessageHelper.helpTransferMessage(),
                CommandsHelper.setMessage(customerName, message).getHelpMessage());

    }

    @Test
    public void testNullCommands() {
        assertEquals(MessageHelper.helpMessage(),
                CommandsHelper.setMessage("alice", null).getHelpMessage());

        assertNull(CommandsHelper.setMessage("alice", "admin").getHelpMessage());

        assertNull(CommandsHelper.setMessage("alice", "logout").getHelpMessage());

        assertEquals(MessageHelper.helpMessage(),
                CommandsHelper.setMessage("alice", "jakarta -").getHelpMessage());

        assertEquals(MessageHelper.helpMessage(),
                CommandsHelper.setMessage("alice", "- -").getHelpMessage());

        assertNull(CommandsHelper.setMessage("alice", "login a a").getMessage());

        assertEquals(MessageHelper.helpMessage(), CommandsHelper.setHelpMessage(null));
        assertEquals(MessageHelper.helpMessage(), CommandsHelper.setHelpMessage(Commands.ADMIN));
    }

    @Test
    public void testCommands() {
        assertEquals("alice|LOGIN|alice",
                CommandsHelper.setMessage("alice", "login alice").getMessage());

        assertEquals("alice|DEPOSIT|100",
                CommandsHelper.setMessage("alice", "deposit 100").getMessage());

        assertEquals("alice|WITHDRAW|100",
                CommandsHelper.setMessage("alice", "withdraw 100").getMessage());

        assertEquals("alice|TRANSFER|bob|100",
                CommandsHelper.setMessage("alice", "transfer bob 100").getMessage());

        assertEquals("alice|LOGOUT",
                CommandsHelper.setMessage("alice", "logout").getMessage());
    }
}
