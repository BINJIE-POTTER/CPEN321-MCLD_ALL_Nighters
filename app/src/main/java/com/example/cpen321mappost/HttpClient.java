package com.example.cpen321mappost;

import okhttp3.OkHttpClient;

public class HttpClient {
    private static OkHttpClient singletonClient;

    private HttpClient() {
        // private constructor to enforce Singleton pattern
    }

    public static synchronized OkHttpClient getInstance() {

        if (singletonClient == null) singletonClient = new OkHttpClient();

        return singletonClient;

    }

}