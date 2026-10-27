package com.osint.crawler.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class SourceType {
    SURFACE_WEB,
    WAYBACK_MACHINE,
    DARK_WEB_ONION
}

enum class ResultType {
    URL,
    EMAIL,
    PHONE,
    IP_ADDRESS,
    DOMAIN,
    SOCIAL_MEDIA,
    DOCUMENT,
    CREDENTIAL_EXPOSURE,
    RAW_TEXT,
    ONION_SERVICE,
    ARCHIVED_PAGE
}

@Parcelize
data class ScanResult(
    val id: Long = System.currentTimeMillis(),
    val target: String,
    val sourceType: SourceType,
    val resultType: ResultType,
    val module: String,
    val title: String,
    val url: String,
    val snippet: String,
    val rawData: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val depth: Int = 0,
    val metadata: Map<String, String> = emptyMap()
) : Parcelable

data class ScanConfig(
    val target: String,
    val modules: List<CrawlerModule>,
    val maxDepth: Int = 2,
    val maxPages: Int = 100,
    val torEnabled: Boolean = false,
    val torHost: String = "127.0.0.1",
    val torPort: Int = 9050,
    val waybackEnabled: Boolean = true,
    val surfaceWebEnabled: Boolean = true,
    val darkWebEnabled: Boolean = false,
    val followLinks: Boolean = true,
    val userAgent: String = "Mozilla/5.0 (compatible; OSINTCrawler/1.0)"
)

enum class CrawlerModule(val displayName: String, val description: String, val category: String) {
    WAYBACK_CDX("Wayback Machine CDX", "Archive.org CDX API — URLs, snapshots, MIME types", "Surface/Archive"),
    WAYBACK_AVAILABILITY("Wayback Availability", "Check if URL has an archived snapshot", "Surface/Archive"),
    SURFACE_LINKS("Surface Link Crawler", "Extract all hyperlinks from target page", "Surface Web"),
    SURFACE_EMAILS("Email Harvester", "Extract email addresses from pages", "Surface Web"),
    SURFACE_PHONES("Phone Number Extractor", "Find phone numbers in page text", "Surface Web"),
    SURFACE_META("Metadata Extractor", "Page title, description, keywords, OG tags", "Surface Web"),
    SURFACE_FORMS("Form Analyzer", "Detect login forms, input fields, endpoints", "Surface Web"),
    SURFACE_JS_ENDPOINTS("JS Endpoint Finder", "Find API endpoints in JavaScript files", "Surface Web"),
    SURFACE_SOCIAL("Social Media Finder", "Detect social media profile links", "Surface Web"),
    SURFACE_DOCUMENTS("Document Finder", "Find PDFs, DOCs, spreadsheets", "Surface Web"),
    SURFACE_DNS_LEAKS("DNS/IP Leaks", "Detect IP addresses and DNS references", "Surface Web"),
    TOR_ONION_CRAWLER("Onion Crawler", "Crawl .onion sites via Tor/Orbot", "Dark Web"),
    TOR_HIDDEN_SERVICES("Hidden Service Enum", "Enumerate linked .onion services", "Dark Web"),
    TOR_CONTENT_SCAN("Onion Content Scanner", "Extract text and links from .onion pages", "Dark Web"),
    TOR_MARKETPLACE_DETECT("Market Detector", "Identify marketplace/forum patterns on .onion", "Dark Web")
}

data class ScanSession(
    val id: String = java.util.UUID.randomUUID().toString(),
    val config: ScanConfig,
    val startTime: Long = System.currentTimeMillis(),
    var endTime: Long? = null,
    var status: ScanStatus = ScanStatus.RUNNING,
    var totalResults: Int = 0,
    var pagesScanned: Int = 0,
    var currentModule: String = ""
)

enum class ScanStatus { RUNNING, COMPLETED, FAILED, CANCELLED }
