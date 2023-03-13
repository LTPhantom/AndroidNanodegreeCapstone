package com.example.android.politicalpreparedness.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class CivicsHttpClient: OkHttpClient() {

    companion object {

        private const val API_KEY = "API_KEY_HERE"
        private const val SHA = "SHA1_HERE"
        private const val PACKAGE = "com.example.android.politicalpreparedness"

        val interceptor = HttpLoggingInterceptor()
            .apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

        fun getClient(): OkHttpClient {
            return Builder()
                    .addInterceptor { chain ->
                        val original = chain.request()
                        val url = original
                                .url
                                .newBuilder()
                                .addQueryParameter("key", API_KEY)
                                .build()
                        val request = original
                                .newBuilder()
                            .addHeader("X-Android-Package", PACKAGE)
                            .addHeader("X-Android-Cert", SHA)
                                .url(url)
                                .build()
                        chain.proceed(request)
                    }
                .addInterceptor(interceptor)
                    .build()
        }
    }
}