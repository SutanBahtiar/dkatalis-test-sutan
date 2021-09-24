package dkatalis.bank.socket;

import dkatalis.bank.handler.AtmHandler;
import dkatalis.bank.service.AtmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);
    private final AtmService atmService;
    private ServerSocket serverSocket;

    public Server(AtmService atmService) {
        this.atmService = atmService;
    }

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            LOGGER.info("Starting server..");
            LOGGER.info("Listening on port:{}", port);
            while (true) {
                new AtmHandler(serverSocket.accept(), atmService).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stop();
        }

    }

    public void stop() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
