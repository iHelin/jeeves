package me.ianhe.jeeves.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author iHelin
 * @since 2018/8/13 20:54
 */
public class MessageUtils {

    private static Pattern pattern = Pattern.compile("^(@([0-9]|[a-z])+):");

    public static String getChatRoomTextMessageContent(String content) {
        if (content == null) {
            throw new IllegalArgumentException("content");
        }
        return content.replaceAll("^(@([0-9]|[a-z])+):", "")
                .replaceAll("<br/>", "");
    }

    public static String getSenderOfChatRoomTextMessage(String content) {
        if (content == null) {
            throw new IllegalArgumentException("content");
        }
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
