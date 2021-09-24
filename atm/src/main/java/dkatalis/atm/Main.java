package dkatalis.atm;

import dkatalis.atm.socket.Client;

public class Main {

    private static final String IP = "127.0.0.1";
    private static final int PORT = 6666;

    public static void main(String[] args) {
        final Client client = new Client();
        client.start(IP, PORT);
    }
}
