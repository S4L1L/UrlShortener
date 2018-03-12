package com.salilsawant.urlshortener.domain.service;

public class UrlShorteningService {

    private static final String CHARACTER_SET = "abcdeABCDE01234fghijFGHIJ56789klmnoKLMNOpqrstPQRSTuvwxyzUVWXYZ";
    private static final int BASE = CHARACTER_SET.length();
    private static final long URL_STARTING = (long) java.lang.Math.pow(BASE, 4) + 1;

    public static String encode(long urlId) {
        StringBuilder encoded_string = new StringBuilder();
        urlId = urlId + URL_STARTING;
        while (urlId > 0) {
            encoded_string.insert(0, CHARACTER_SET.charAt((int)(urlId % BASE)));
            urlId = urlId / BASE;
        }
        return encoded_string.toString();
    }

    public static long decode(String encoded_url) {
        long urlId = 0;
        for (int i = 0; i < encoded_url.length(); i++) {
            urlId = urlId * BASE + CHARACTER_SET.indexOf(encoded_url.charAt(i));
        }
        urlId = urlId - URL_STARTING;
        return urlId;
    }

    private UrlShorteningService() {}
}
