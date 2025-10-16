package com.qqqqqq.template21.frame.util;

import cn.hutool.extra.spring.SpringUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author apple
 */
public class FileDownloader {

    private static final String DOWNLOAD_BIN = "download.bin";

    public record FileBytes(byte[] bytes, String fileName, String contentType, long contentLength) {
    }

    public static FileBytes download(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        OkHttpClient okHttpClient = SpringUtil.getBean(OkHttpClient.class);
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code: " + response);
            }
            assert response.body() != null;
            byte[] bytes = response.body().bytes();
            String contentType = response.header("Content-Type", "application/octet-stream");
            long contentLength = response.body().contentLength();
            String fileName = extractFileName(response.header("Content-Disposition"), url);
            return new FileBytes(bytes, fileName, contentType, contentLength);
        }
    }

    /**
     * 从 Content-Disposition 提取文件名；兼容 RFC5987 的 filename*=utf-8''...，其次 filename=...；失败则回退到 URL
     */
    private static String extractFileName(String contentDisposition, String url) {
        // 1) RFC5987: filename*=utf-8''encoded%20name.mp3  或  filename*=UTF-8''...
        if (contentDisposition != null) {
            Matcher mStar = Pattern.compile("filename\\*=(?:[A-Za-z0-9_-]+')?[^']*'([^;]+)")
                    .matcher(contentDisposition);
            if (mStar.find()) {
                String encoded = mStar.group(1);
                return urlDecodeUtf8(encoded);
            }

            // 2) 常规：filename="name.mp3" 或 filename=name.mp3
            Matcher m = Pattern.compile("filename=\"?([^\";]+)\"?")
                    .matcher(contentDisposition);
            if (m.find()) {
                return m.group(1);
            }
        }

        // 3) 回退：URL 最后一段
        return fileNameFromUrl(url);
    }

    private static String fileNameFromUrl(String url) {
        try {
            String path = URI.create(url).getPath();
            if (path == null || path.isBlank()) {
                return DOWNLOAD_BIN;
            }
            String last = path.substring(path.lastIndexOf('/') + 1);
            if (last.isBlank()) {
                return DOWNLOAD_BIN;
            }
            return urlDecodeUtf8(last);
        } catch (Exception ignore) {
            return DOWNLOAD_BIN;
        }
    }

    private static String urlDecodeUtf8(String s) {
        return URLDecoder.decode(s, StandardCharsets.UTF_8);
    }

//    public static void main(String[] args) throws IOException {
//        FileBytes fb = download("https://amber.7mfitness.com/i18n/audio/418307d2-6124-4c94-bcb5-a1fa5bc50b30.mp3?name=4O15T60k1tC9NpJEPWuksxztYz4jIbwmhlRXhKxeeC38VLuyNCqmbz");
//        System.out.printf("name=%s, type=%s, bytes=%d%n", fb.fileName(), fb.contentType(), fb.bytes().length);
//    }
}
