package dkatalis.bank.util;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Map;

public class Generator {

    public static String generateRefId() {
        return RandomStringUtils.randomAlphanumeric(6);
    }

    public static String generateTrxId() {
        return RandomStringUtils.randomAlphanumeric(9);
    }

    public static long generateId() {
        return System.currentTimeMillis();
//        final UUID uuid = UUID.randomUUID();
//        return uuid.getMostSignificantBits() & Long.MAX_VALUE;
    }

    public static long getId(Map<Long, Object> map) {
        long id = Generator.generateId();
        while (null != map.get(id)) {
            id = Generator.generateId();
        }
        return id;
    }
}
