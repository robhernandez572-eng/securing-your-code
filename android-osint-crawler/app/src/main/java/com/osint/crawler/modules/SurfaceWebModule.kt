package com.osint.crawler.modules

import com.osint.crawler.models.*
import com.osint.crawler.utils.HttpClientFactory
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import java.util.regex.Pattern

/**
 * Surface web crawler — uses OkHttp to fetch pages and JSoup to parse HTML.
 * Extracts links, emails, phones, metadata, social profiles, documents, and
 * API endpoints from JavaScript.
 */
class SurfaceWebModule(private val config: ScanConfig) {

    private val client = HttpClientFactory.surfaceClient(config.userAgent)

    private val emailPattern = Pattern.compile(
        "[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}"
    )
    private val phonePattern = Pattern.compile(
        "(?:\\+?1[-.]?)?\\(?\\d{3}\\)?[-. ]?\\d{3}[-. ]?\\d{4}"
    )
    private val ipPattern = Pattern.compile(
        "\\b(?:(?:25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(?:25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\b"
    )
    private val apiEndpointPattern = Pattern.compile(
        "(?:\"|\')(/?(?:api|v\\d+|rest|graphql|endpoint|service)/[^\"'\\s]{3,})(?:\"|')"
    )
    private val socialPatterns = mapOf(
        "Twitter/X" to Pattern.compile("(?:twitter\\.com|x\\.com)/([A-Za-z0-9_]{1,50})"),
        "GitHub" to Pattern.compile("github\\.com/([A-Za-z0-9_\\-]{1,100})"),
        "LinkedIn" to Pattern.compile("linkedin\\.com/(?:in|company)/([A-Za-z0-9_\\-]{1,100})"),
        "Instagram" to Pattern.compile("instagram\\.com/([A-Za-z0-9_.]{1,50})"),
        "Facebook" to Pattern.compile("facebook\\.com/([A-Za-z0-9_.]{1,100})"),
        "YouTube" to Pattern.compile("youtube\\.com/(?:c/|channel/|user/)?([A-Za-z0-9_\\-]{1,100})")
    )
    private val docExtensions = setOf("pdf","doc","docx","xls","xlsx","ppt","pptx","csv","txt","zip","tar","gz")

    suspend fun crawlUrl(
        url: String,
        modules: List<CrawlerModule>,
        onResult: suspend (ScanResult) -> Unit
    ): Set<String> {
        val discoveredLinks = mutableSetOf<String>()
        val doc = fetchDocument(url) ?: return discoveredLinks

        if (CrawlerModule.SURFACE_META in modules) extractMeta(url, doc, onResult)
        if (CrawlerModule.SURFACE_LINKS in modules) extractLinks(url, doc, discoveredLinks, onResult)
        if (CrawlerModule.SURFACE_EMAILS in modules) extractEmails(url, doc, onResult)
        if (CrawlerModule.SURFACE_PHONES in modules) extractPhones(url, doc, onResult)
        if (CrawlerModule.SURFACE_SOCIAL in modules) extractSocial(url, doc, onResult)
        if (CrawlerModule.SURFACE_DOCUMENTS in modules) extractDocuments(url, doc, onResult)
        if (CrawlerModule.SURFACE_FORMS in modules) extractForms(url, doc, onResult)
        if (CrawlerModule.SURFACE_JS_ENDPOINTS in modules) extractJsEndpoints(url, doc, onResult)
        if (CrawlerModule.SURFACE_DNS_LEAKS in modules) extractIpRefs(url, doc, onResult)

        return discoveredLinks
    }

    private fun fetchDocument(url: String): Document? {
        return try {
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: return null
                Jsoup.parse(body, url)
            }
        } catch (e: IOException) {
            null
        }
    }

    private suspend fun extractMeta(url: String, doc: Document, onResult: suspend (ScanResult) -> Unit) {
        val title = doc.title()
        val desc = doc.select("meta[name=description]").attr("content")
        val keywords = doc.select("meta[name=keywords]").attr("content")
        val ogTitle = doc.select("meta[property=og:title]").attr("content")
        val ogImage = doc.select("meta[property=og:image]").attr("content")
        val generator = doc.select("meta[name=generator]").attr("content")

        val meta = buildString {
            if (title.isNotBlank()) append("Title: $title\n")
            if (desc.isNotBlank()) append("Description: $desc\n")
            if (keywords.isNotBlank()) append("Keywords: $keywords\n")
            if (ogTitle.isNotBlank()) append("OG Title: $ogTitle\n")
            if (generator.isNotBlank()) append("Generator: $generator\n")
        }

        if (meta.isBlank()) return

        onResult(ScanResult(
            target = url,
            sourceType = SourceType.SURFACE_WEB,
            resultType = ResultType.RAW_TEXT,
            module = CrawlerModule.SURFACE_META.displayName,
            title = title.ifBlank { "Page Metadata" },
            url = url,
            snippet = desc.ifBlank { meta.take(200) },
            rawData = meta,
            metadata = mapOf(
                "page_title" to title,
                "description" to desc,
                "keywords" to keywords,
                "og_image" to ogImage,
                "generator" to generator
            )
        ))
    }

    private suspend fun extractLinks(
        url: String, doc: Document,
        discoveredLinks: MutableSet<String>,
        onResult: suspend (ScanResult) -> Unit
    ) {
        doc.select("a[href]").forEach { el ->
            val href = el.absUrl("href")
            if (href.isBlank() || href.startsWith("javascript:") || href.startsWith("mailto:")) return@forEach
            discoveredLinks.add(href)
            onResult(ScanResult(
                target = url,
                sourceType = SourceType.SURFACE_WEB,
                resultType = ResultType.URL,
                module = CrawlerModule.SURFACE_LINKS.displayName,
                title = el.text().take(120).ifBlank { href },
                url = href,
                snippet = "Link found on $url"
            ))
        }
    }

    private suspend fun extractEmails(url: String, doc: Document, onResult: suspend (ScanResult) -> Unit) {
        val text = doc.text() + " " + doc.html()
        val found = mutableSetOf<String>()
        val m = emailPattern.matcher(text)
        while (m.find()) {
            val email = m.group()
            if (found.add(email)) {
                onResult(ScanResult(
                    target = url,
                    sourceType = SourceType.SURFACE_WEB,
                    resultType = ResultType.EMAIL,
                    module = CrawlerModule.SURFACE_EMAILS.displayName,
                    title = email,
                    url = url,
                    snippet = "Email found on $url"
                ))
            }
        }
        // Also check mailto: links
        doc.select("a[href^=mailto]").forEach { el ->
            val email = el.attr("href").removePrefix("mailto:").substringBefore("?")
            if (email.contains("@") && found.add(email)) {
                onResult(ScanResult(
                    target = url,
                    sourceType = SourceType.SURFACE_WEB,
                    resultType = ResultType.EMAIL,
                    module = CrawlerModule.SURFACE_EMAILS.displayName,
                    title = email,
                    url = url,
                    snippet = "Email in mailto: link on $url"
                ))
            }
        }
    }

    private suspend fun extractPhones(url: String, doc: Document, onResult: suspend (ScanResult) -> Unit) {
        val text = doc.text()
        val found = mutableSetOf<String>()
        val m = phonePattern.matcher(text)
        while (m.find()) {
            val phone = m.group()
            if (found.add(phone)) {
                onResult(ScanResult(
                    target = url,
                    sourceType = SourceType.SURFACE_WEB,
                    resultType = ResultType.PHONE,
                    module = CrawlerModule.SURFACE_PHONES.displayName,
                    title = phone,
                    url = url,
                    snippet = "Phone found on $url"
                ))
            }
        }
    }

    private suspend fun extractSocial(url: String, doc: Document, onResult: suspend (ScanResult) -> Unit) {
        val html = doc.html()
        socialPatterns.forEach { (platform, pattern) ->
            val m = pattern.matcher(html)
            val found = mutableSetOf<String>()
            while (m.find()) {
                val profileUrl = m.group()
                if (found.add(profileUrl)) {
                    onResult(ScanResult(
                        target = url,
                        sourceType = SourceType.SURFACE_WEB,
                        resultType = ResultType.SOCIAL_MEDIA,
                        module = CrawlerModule.SURFACE_SOCIAL.displayName,
                        title = "$platform: ${m.group(1)}",
                        url = "https://$profileUrl",
                        snippet = "Social profile found on $url",
                        metadata = mapOf("platform" to platform, "handle" to (m.group(1) ?: ""))
                    ))
                }
            }
        }
    }

    private suspend fun extractDocuments(url: String, doc: Document, onResult: suspend (ScanResult) -> Unit) {
        doc.select("a[href]").forEach { el ->
            val href = el.absUrl("href")
            val ext = href.substringAfterLast('.').lowercase().substringBefore('?')
            if (ext in docExtensions) {
                onResult(ScanResult(
                    target = url,
                    sourceType = SourceType.SURFACE_WEB,
                    resultType = ResultType.DOCUMENT,
                    module = CrawlerModule.SURFACE_DOCUMENTS.displayName,
                    title = el.text().ifBlank { href.substringAfterLast('/') },
                    url = href,
                    snippet = ".$ext document linked from $url",
                    metadata = mapOf("file_type" to ext)
                ))
            }
        }
    }

    private suspend fun extractForms(url: String, doc: Document, onResult: suspend (ScanResult) -> Unit) {
        doc.select("form").forEach { form ->
            val action = form.absUrl("action").ifBlank { url }
            val method = form.attr("method").uppercase().ifBlank { "GET" }
            val inputs = form.select("input").map { "${it.attr("type")}:${it.attr("name")}" }
            val hasPassword = inputs.any { it.startsWith("password") }

            onResult(ScanResult(
                target = url,
                sourceType = SourceType.SURFACE_WEB,
                resultType = ResultType.URL,
                module = CrawlerModule.SURFACE_FORMS.displayName,
                title = "${if (hasPassword) "[LOGIN FORM]" else "[FORM]"} $method $action",
                url = action,
                snippet = "Form inputs: ${inputs.joinToString(", ").take(200)}",
                metadata = mapOf(
                    "method" to method,
                    "action" to action,
                    "has_password_field" to hasPassword.toString(),
                    "inputs" to inputs.joinToString("|")
                )
            ))
        }
    }

    private suspend fun extractJsEndpoints(url: String, doc: Document, onResult: suspend (ScanResult) -> Unit) {
        val scripts = doc.select("script[src]").map { it.absUrl("src") }
        scripts.forEach { scriptUrl ->
            try {
                val req = Request.Builder().url(scriptUrl).build()
                client.newCall(req).execute().use { resp ->
                    val jsCode = resp.body?.string() ?: return@use
                    val found = mutableSetOf<String>()
                    val m = apiEndpointPattern.matcher(jsCode)
                    while (m.find()) {
                        val endpoint = m.group(1) ?: continue
                        if (found.add(endpoint)) {
                            onResult(ScanResult(
                                target = url,
                                sourceType = SourceType.SURFACE_WEB,
                                resultType = ResultType.URL,
                                module = CrawlerModule.SURFACE_JS_ENDPOINTS.displayName,
                                title = "API endpoint: $endpoint",
                                url = endpoint,
                                snippet = "Found in JS: $scriptUrl",
                                metadata = mapOf("script_source" to scriptUrl)
                            ))
                        }
                    }
                }
            } catch (_: IOException) {}
        }
    }

    private suspend fun extractIpRefs(url: String, doc: Document, onResult: suspend (ScanResult) -> Unit) {
        val text = doc.html()
        val found = mutableSetOf<String>()
        val m = ipPattern.matcher(text)
        while (m.find()) {
            val ip = m.group()
            // Skip common private/loopback ranges that are uninteresting
            if (ip.startsWith("127.") || ip.startsWith("0.") || ip == "255.255.255.255") continue
            if (found.add(ip)) {
                onResult(ScanResult(
                    target = url,
                    sourceType = SourceType.SURFACE_WEB,
                    resultType = ResultType.IP_ADDRESS,
                    module = CrawlerModule.SURFACE_DNS_LEAKS.displayName,
                    title = "IP reference: $ip",
                    url = url,
                    snippet = "IP address found in page source of $url"
                ))
            }
        }
    }
}
