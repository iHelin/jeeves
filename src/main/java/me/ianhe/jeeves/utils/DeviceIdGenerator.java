package me.ianhe.jeeves.utils;

import java.util.Random;

/**
 * @author linhe2
 * @since 2018/8/13 22:17
 */
public class DeviceIdGenerator {

    private final static Random RANDOM = new Random();

    public static String generate() {
        long rnd = (long) (RANDOM.nextDouble() * 1e15);
        return String.format("e%015d", rnd);
    }
}
