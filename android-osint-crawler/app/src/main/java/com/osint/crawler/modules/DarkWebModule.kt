package com.osint.crawler.modules

import com.osint.crawler.models.*
import com.osint.crawler.utils.HttpClientFactory
import okhttp3.Request
import org.jsoup.Jsoup
import java.io.IOException
import java.util.regex.Pattern

/**
 * Dark web / .onion crawler.
 *
 * REQUIRES Orbot (Tor for Android) to be installed and running on the device.
 * All requests are routed through the Orbot SOCKS5 proxy at 127.0.0.1:9050.
 * .onion hostnames are resolved by the Tor network — they never hit Android DNS.
 *
 * Usage:
 *   1. Install Orbot from F-Droid or Google Play.
 *   2. Start Orbot and enable VPN or SOCKS5 mode.
 *   3. Enable "Dark Web" in the app settings and set the proxy port (default 9050).
 */
class DarkWebModule(private val config: ScanConfig) {

    private val client = HttpClientFactory.torClient(
        torHost = config.torHost,
        torPort = config.torPort,
        userAgent = config.userAgent
    )

    private val onionLinkPattern = Pattern.compile(
        "(?:https?://)?([a-z2-7]{56}\\.onion(?:/[^\\s\"'<>]*)?)",
        Pattern.CASE_INSENSITIVE
    )
    private val emailPattern = Pattern.compile(
        "[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}"
    )

    // Patterns for detecting marketplace / forum indicators
    private val marketplaceKeywords = listOf(
        "add to cart", "checkout", "vendor", "escrow", "pgp", "monero", "bitcoin",
        "listings", "marketplace", "shop", "price", "btc", "xmr"
    )
    private val forumKeywords = listOf(
        "thread", "post", "reply", "forum", "board", "register", "login", "topic"
    )

    suspend fun crawlOnionUrl(
        url: String,
        modules: List<CrawlerModule>,
        depth: Int = 0,
        onResult: suspend (ScanResult) -> Unit
    ): Set<String> {
        val discoveredOnions = mutableSetOf<String>()

        val normalizedUrl = if (!url.startsWith("http")) "http://$url" else url

        try {
            val request = Request.Builder().url(normalizedUrl).build()
            client.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: return discoveredOnions
                val doc = Jsoup.parse(body, normalizedUrl)

                if (CrawlerModule.TOR_CONTENT_SCAN in modules) {
                    extractOnionContent(normalizedUrl, doc, onResult)
                }
                if (CrawlerModule.TOR_HIDDEN_SERVICES in modules) {
                    extractOnionLinks(normalizedUrl, body, discoveredOnions, onResult)
                }
                if (CrawlerModule.TOR_MARKETPLACE_DETECT in modules) {
                    detectSiteType(normalizedUrl, doc, onResult)
                }

                // Emit a primary result for the onion page itself
                onResult(ScanResult(
                    target = url,
                    sourceType = SourceType.DARK_WEB_ONION,
                    resultType = ResultType.ONION_SERVICE,
                    module = CrawlerModule.TOR_ONION_CRAWLER.displayName,
                    title = doc.title().ifBlank { normalizedUrl },
                    url = normalizedUrl,
                    snippet = doc.body()?.text()?.take(300) ?: "No content",
                    metadata = mapOf(
                        "http_status" to response.code.toString(),
                        "content_length" to (response.body?.contentLength()?.toString() ?: "unknown"),
                        "depth" to depth.toString()
                    )
                ))
            }
        } catch (e: IOException) {
            onResult(ScanResult(
                target = url,
                sourceType = SourceType.DARK_WEB_ONION,
                resultType = ResultType.RAW_TEXT,
                module = CrawlerModule.TOR_ONION_CRAWLER.displayName,
                title = "Onion Unreachable",
                url = normalizedUrl,
                snippet = "Could not reach $url — ${e.message}\n\nMake sure Orbot is running and connected.",
                rawData = e.toString()
            ))
        }

        return discoveredOnions
    }

    private suspend fun extractOnionLinks(
        sourceUrl: String,
        html: String,
        discovered: MutableSet<String>,
        onResult: suspend (ScanResult) -> Unit
    ) {
        val m = onionLinkPattern.matcher(html)
        val found = mutableSetOf<String>()
        while (m.find()) {
            val onionHref = m.group(1) ?: continue
            val fullOnionUrl = if (onionHref.startsWith("http")) onionHref else "http://$onionHref"
            if (found.add(fullOnionUrl)) {
                discovered.add(fullOnionUrl)
                onResult(ScanResult(
                    target = sourceUrl,
                    sourceType = SourceType.DARK_WEB_ONION,
                    resultType = ResultType.ONION_SERVICE,
                    module = CrawlerModule.TOR_HIDDEN_SERVICES.displayName,
                    title = onionHref.substringBefore('/'),
                    url = fullOnionUrl,
                    snippet = "Onion service linked from $sourceUrl"
                ))
            }
        }
    }

    private suspend fun extractOnionContent(
        url: String,
        doc: org.jsoup.nodes.Document,
        onResult: suspend (ScanResult) -> Unit
    ) {
        // Extract emails
        val text = doc.text()
        val emailMatcher = emailPattern.matcher(text)
        val seenEmails = mutableSetOf<String>()
        while (emailMatcher.find()) {
            val email = emailMatcher.group()
            if (seenEmails.add(email)) {
                onResult(ScanResult(
                    target = url,
                    sourceType = SourceType.DARK_WEB_ONION,
                    resultType = ResultType.EMAIL,
                    module = CrawlerModule.TOR_CONTENT_SCAN.displayName,
                    title = email,
                    url = url,
                    snippet = "Email found on .onion page"
                ))
            }
        }

        // Extract headings and key text blocks
        doc.select("h1, h2, h3").forEach { heading ->
            val headingText = heading.text().trim()
            if (headingText.isBlank()) return@forEach
            onResult(ScanResult(
                target = url,
                sourceType = SourceType.DARK_WEB_ONION,
                resultType = ResultType.RAW_TEXT,
                module = CrawlerModule.TOR_CONTENT_SCAN.displayName,
                title = "<${heading.tagName().uppercase()}> $headingText",
                url = url,
                snippet = headingText
            ))
        }

        // Check for PGP keys
        if (text.contains("-----BEGIN PGP PUBLIC KEY BLOCK-----")) {
            onResult(ScanResult(
                target = url,
                sourceType = SourceType.DARK_WEB_ONION,
                resultType = ResultType.RAW_TEXT,
                module = CrawlerModule.TOR_CONTENT_SCAN.displayName,
                title = "[PGP] Public key block found",
                url = url,
                snippet = "Page contains a PGP public key block"
            ))
        }
    }

    private suspend fun detectSiteType(
        url: String,
        doc: org.jsoup.nodes.Document,
        onResult: suspend (ScanResult) -> Unit
    ) {
        val lowerText = doc.text().lowercase()
        val marketScore = marketplaceKeywords.count { lowerText.contains(it) }
        val forumScore = forumKeywords.count { lowerText.contains(it) }

        val siteType = when {
            marketScore >= 3 -> "MARKETPLACE (score: $marketScore/${marketplaceKeywords.size})"
            forumScore >= 3 -> "FORUM/BOARD (score: $forumScore/${forumKeywords.size})"
            lowerText.contains("login") || lowerText.contains("register") -> "AUTHENTICATION REQUIRED"
            else -> "UNKNOWN/STATIC"
        }

        onResult(ScanResult(
            target = url,
            sourceType = SourceType.DARK_WEB_ONION,
            resultType = ResultType.RAW_TEXT,
            module = CrawlerModule.TOR_MARKETPLACE_DETECT.displayName,
            title = "Site Classification: $siteType",
            url = url,
            snippet = "Market indicators: $marketScore | Forum indicators: $forumScore"
        ))
    }
}
