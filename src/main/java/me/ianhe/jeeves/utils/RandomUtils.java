package me.ianhe.jeeves.utils;

/**
 * @author iHelin
 * @since 2018/8/14 18:51
 */
public class RandomUtils {

    public static int generateDateWithBitwiseNot() {
        long time = System.currentTimeMillis();
        return generateDateWithBitwiseNot(time);
    }

    public static int generateDateWithBitwiseNot(long time) {
        return -((int) time + 1);
    }
}
