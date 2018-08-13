package me.ianhe.jeeves.utils;

import org.springframework.http.HttpHeaders;

/**
 * @author iHelin
 * @since 2018/8/13 22:09
 */
public class HeaderUtils {
    public static HttpHeaders merge(HttpHeaders... headers) {
        if (headers == null) {
            throw new IllegalArgumentException("headers");
        }
        if (headers.length == 1) {
            return headers[0];
        }
        HttpHeaders header = new HttpHeaders();
        for (HttpHeaders h : headers) {
            header.setAll(h.toSingleValueMap());
        }
        return header;
    }

    public static void assign(HttpHeaders header, HttpHeaders... headers) {
        if (header == null) {
            throw new IllegalArgumentException("header");
        }
        if (headers == null) {
            throw new IllegalArgumentException("headers");
        }
        if (headers.length == 0) {
            return;
        }
        for (HttpHeaders h : headers) {
            header.setAll(h.toSingleValueMap());
        }
    }
}
