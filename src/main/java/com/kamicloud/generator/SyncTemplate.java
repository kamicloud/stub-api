package com.kamicloud.generator;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.time.Duration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class SyncTemplate {
    public static void main(String[] argv) throws IOException {
        OkHttpClient client = (new OkHttpClient.Builder()).readTimeout(Duration.ofMinutes(1)).build();

//        String testHost = env.getProperty("test-host", "http://localhost");
        Request request = new Request.Builder()
            .url("http://localhost/test")
            .get()
            .build();

        Response response = client.newCall(request).execute();

//        response.body().
//        new ZipFile();
    }
}
