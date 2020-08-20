package com.upd.contraplus2020.service;

import okhttp3.OkHttpClient;

public class ApolloClient {

    private static final String BASE_URL = "https://ioor46nqul.execute-api.us-east-2.amazonaws.com/beta/graphql";

    public static com.apollographql.apollo.ApolloClient setupApollo(){

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();

        return com.apollographql.apollo.ApolloClient.builder()
                .serverUrl(BASE_URL)
                .okHttpClient(okHttpClient)
                .build();
    }
}
