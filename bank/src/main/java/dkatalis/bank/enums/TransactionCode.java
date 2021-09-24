package dkatalis.bank.enums;

import java.util.HashMap;
import java.util.Map;

public enum TransactionCode {

    DEPOSIT(0),
    WITHDRAW(1),
    TRANSFER(2),
    OWED(3);

    private static final Map<Object, Object> map = new HashMap<>();

    static {
        for (TransactionCode transactionCode : TransactionCode.values()) {
            map.put(transactionCode.code, transactionCode);
        }
    }

    private final int code;

    TransactionCode(int code) {
        this.code = code;
    }

    public static TransactionCode valueOf(int transactionCode) {
        return (TransactionCode) map.get(transactionCode);
    }

    public int getCode() {
        return code;
    }
}
