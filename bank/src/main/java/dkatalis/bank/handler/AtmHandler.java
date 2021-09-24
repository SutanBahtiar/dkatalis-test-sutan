package dkatalis.bank.handler;

import dkatalis.bank.enums.Commands;
import dkatalis.bank.helper.AtmHelper;
import dkatalis.bank.service.AtmService;
import dkatalis.bank.util.Generator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class AtmHandler extends Thread {

    private static final Logger log = LoggerFactory.getLogger(AtmHandler.class);

    private final Socket clientSocket;
    private final AtmService atmService;

    public AtmHandler(Socket socket,
                      AtmService atmService) {
        this.clientSocket = socket;
        this.atmService = atmService;
    }

    @Override
    public void run() {
        try {
            final String refId = Generator.generateRefId();
            final PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            final BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            if (log.isDebugEnabled())
                log.debug("{}, client connected {}", refId, clientSocket.getInetAddress());

            String readLine;
            while ((readLine = in.readLine()) != null) {
                String message = null;

                if (log.isDebugEnabled())
                    log.debug("{}, client message:{}", refId, readLine);

                final String[] messages = AtmHelper.messages(readLine);
                if (messages.length > 1) {
                    final Commands commands = AtmHelper.commands(AtmHelper.messages(readLine));
                    if (null != commands)
                        message = getMessage(commands, readLine, refId);

                    if (null != message) {
                        if (log.isDebugEnabled())
                            log.debug("{}, send message:\n{}", refId, message);
                        out.println(message);
                    }
                }
            }

            in.close();
            out.close();
            clientSocket.close();

            if (log.isDebugEnabled())
                log.debug("{}, client closing the connection...", refId);

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String getMessage(Commands commands,
                              String message,
                              String refId) {
        switch (commands) {
            case LOGIN:
                return atmService.login(message, refId);
            case LOGOUT:
                return atmService.logout(message, refId);
            case DEPOSIT:
                return atmService.deposit(message, refId);
            case WITHDRAW:
                return atmService.withdraw(message, refId);
            case TRANSFER:
                return atmService.transfer(message, refId);
            case ADMIN:
                return atmService.admin(message, refId);
            default:
                return null;
        }
    }
}
