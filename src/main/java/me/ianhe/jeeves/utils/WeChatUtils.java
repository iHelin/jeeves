package me.ianhe.jeeves.utils;

import me.ianhe.jeeves.domain.response.component.AbstractWeChatHttpResponseBase;
import me.ianhe.jeeves.domain.shared.Contact;
import me.ianhe.jeeves.domain.shared.Message;
import me.ianhe.jeeves.exception.WechatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author linhe2
 * @since 2018/8/14 22:38
 */
public class WeChatUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeChatUtils.class);

    public static void checkBaseResponse(AbstractWeChatHttpResponseBase response) {
        if (response.getBaseResponse().getRet() != 0) {
            throw new WechatException(response.getClass().getSimpleName() + " ret = " + response.getBaseResponse().getRet());
        }
    }

    public static String textDecode(String text) {
        if (text == null) {
            throw new IllegalArgumentException("text");
        }
        return new String(text.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }

    public static boolean isIndividual(Contact contact) {
        if (contact == null) {
            throw new IllegalArgumentException("contact");
        }
        return contact.getUserName().startsWith("@") && !contact.getUserName().startsWith("@@") && ((contact.getVerifyFlag() & 8) == 0);
    }

    public static boolean isChatRoom(Contact contact) {
        if (contact == null) {
            throw new IllegalArgumentException("contact");
        }
        return contact.getUserName().startsWith("@@");
    }

    public static boolean isMediaPlatform(Contact contact) {
        if (contact == null) {
            throw new IllegalArgumentException("contact");
        }
        return contact.getUserName().startsWith("@") && !contact.getUserName().startsWith("@@") && ((contact.getVerifyFlag() & 8) > 0);
    }

    public static boolean isMessageFromIndividual(Message message) {
        return message.getFromUserName() != null
                && message.getFromUserName().startsWith("@")
                && !message.getFromUserName().startsWith("@@");
    }

    public static boolean isMessageFromChatRoom(Message message) {
        return message.getFromUserName() != null && message.getFromUserName().startsWith("@@");
    }

    public static void sleep(){
        int rand = 2 + WeChatUtils.random(100, 3000);
        sleep(rand);
    }

    /**
     * 休眠，单位: 毫秒
     *
     * @param ms
     */
    public static void sleep(long ms) {
        try {
            TimeUnit.MILLISECONDS.sleep(ms);
        } catch (InterruptedException e) {
            LOGGER.error("", e);
        }
    }

    public static int random(int min, int max) {
        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }
}
