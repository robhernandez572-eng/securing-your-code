package com.osint.crawler.utils

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.TimeUnit

object HttpClientFactory {

    private const val CONNECT_TIMEOUT_S = 30L
    private const val READ_TIMEOUT_S = 60L
    private const val WRITE_TIMEOUT_S = 30L

    fun surfaceClient(userAgent: String): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT_S, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_S, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT_S, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", userAgent)
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .header("Accept-Language", "en-US,en;q=0.5")
                    .build()
                chain.proceed(request)
            }
            .followRedirects(true)
            .followSslRedirects(true)
            .build()
    }

    /**
     * Routes all traffic through Orbot's SOCKS5 proxy on 127.0.0.1:9050.
     * Orbot must be installed and running on the device for .onion resolution to work.
     * OkHttp's SOCKS5 proxy resolver sends the hostname to the proxy, so .onion names
     * are resolved by the Tor network rather than Android's DNS.
     */
    fun torClient(torHost: String = "127.0.0.1", torPort: Int = 9050, userAgent: String): OkHttpClient {
        val torProxy = Proxy(Proxy.Type.SOCKS, InetSocketAddress.createUnresolved(torHost, torPort))
        return OkHttpClient.Builder()
            .proxy(torProxy)
            .connectTimeout(60L, TimeUnit.SECONDS)
            .readTimeout(120L, TimeUnit.SECONDS)
            .writeTimeout(60L, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", userAgent)
                    .header("Accept", "text/html,application/xhtml+xml,*/*;q=0.8")
                    .build()
                chain.proceed(request)
            }
            .followRedirects(true)
            .followSslRedirects(false) // .onion sites rarely use valid TLS certs
            .build()
    }

    fun waybackClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT_S, TimeUnit.SECONDS)
            .readTimeout(90L, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT_S, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", "OSINTCrawler/1.0 (OSINT research tool)")
                    .build()
                chain.proceed(request)
            }
            .followRedirects(true)
            .build()
    }
}
