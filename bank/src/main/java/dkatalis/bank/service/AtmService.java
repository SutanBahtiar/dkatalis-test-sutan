package dkatalis.bank.service;

public interface AtmService {

    String login(String message,
                 String refId);

    String deposit(String message,
                   String refId);

    String withdraw(String message,
                    String refId);

    String transfer(String message,
                    String refId);

    String logout(String message,
                  String refId);

    String admin(String message,
                 String refId);

}
