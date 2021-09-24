package dkatalis.atm.socket;

import dkatalis.atm.helper.CommandsHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

public class Client {

    private static final String SEPARATOR_USERNAME = "#";

    private final Scanner scanner;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public Client() {
        this.scanner = new Scanner(System.in);
    }

    public void start(String ip,
                      int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // default customer if not logged
            final String unknownCustomerName = "-";

            // for login logout
            final AtomicReference<String> customerName = new AtomicReference<>(unknownCustomerName);

            Thread sender = new Thread(new Runnable() {
                String message;

                @Override
                public void run() {
                    while (true) {
                        message = scanner.nextLine();

                        CommandsHelper.Message cmdMessage = CommandsHelper.setMessage(customerName.get(), message);
                        if (null != cmdMessage.getHelpMessage())
                            System.out.println(cmdMessage.getHelpMessage());

                        message = cmdMessage.getMessage();
                        if (null != message)
                            out.println(message);
                    }
                }
            });
            sender.start();

            Thread receiver = new Thread(new Runnable() {
                String message;

                @Override
                public void run() {
                    try {
                        message = in.readLine();
                        while (message != null) {

                            // set login logout
                            if (message.contains(SEPARATOR_USERNAME)) {
                                final String[] responseMessage = message.split(SEPARATOR_USERNAME);
                                customerName.set(responseMessage[0]);
                                message = responseMessage[1];
                            }

                            System.out.println(message);
                            message = in.readLine();
                        }

                        System.out.println("Server out of service...");
                        out.close();
                        clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            receiver.start();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
