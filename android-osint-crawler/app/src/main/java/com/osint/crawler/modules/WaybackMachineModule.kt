package com.osint.crawler.modules

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.osint.crawler.models.*
import com.osint.crawler.utils.HttpClientFactory
import okhttp3.Request
import java.io.IOException

/**
 * Queries the Wayback Machine CDX Server API to retrieve archived snapshots
 * for a target URL or domain.
 *
 * CDX API docs: https://github.com/internetarchive/wayback/tree/master/wayback-cdx-server
 */
class WaybackMachineModule(private val config: ScanConfig) {

    private val client = HttpClientFactory.waybackClient()
    private val gson = Gson()

    companion object {
        private const val CDX_BASE = "https://web.archive.org/cdx/search/cdx"
        private const val AVAILABILITY_BASE = "https://archive.org/wayback/available"
    }

    /** Fetch all archived URLs for a domain using the CDX API. */
    suspend fun fetchCdxResults(
        target: String,
        limit: Int = 200,
        onResult: suspend (ScanResult) -> Unit
    ) {
        // Strip scheme for CDX queries
        val domain = target.removePrefix("https://").removePrefix("http://").trimEnd('/')

        val url = "$CDX_BASE?" +
            "url=${domain}/*" +
            "&output=json" +
            "&fl=original,timestamp,statuscode,mimetype,length" +
            "&collapse=urlkey" +
            "&limit=$limit" +
            "&filter=statuscode:200"

        val request = Request.Builder().url(url).build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return
                val body = response.body?.string() ?: return

                val jsonArray = gson.fromJson(body, JsonArray::class.java)
                // First row is the header — skip it
                for (i in 1 until jsonArray.size()) {
                    val row = jsonArray[i].asJsonArray
                    if (row.size() < 5) continue

                    val originalUrl = row[0].asString
                    val timestamp = row[1].asString
                    val statusCode = row[2].asString
                    val mimeType = row[3].asString
                    val length = row[4].asString

                    val archiveUrl = "https://web.archive.org/web/${timestamp}/${originalUrl}"
                    val formattedDate = formatTimestamp(timestamp)

                    onResult(
                        ScanResult(
                            target = target,
                            sourceType = SourceType.WAYBACK_MACHINE,
                            resultType = mimeToResultType(mimeType, originalUrl),
                            module = CrawlerModule.WAYBACK_CDX.displayName,
                            title = "[$mimeType] $originalUrl",
                            url = archiveUrl,
                            snippet = "Archived $formattedDate · Status $statusCode · Size $length bytes",
                            rawData = row.toString(),
                            metadata = mapOf(
                                "original_url" to originalUrl,
                                "timestamp" to timestamp,
                                "archived_date" to formattedDate,
                                "status_code" to statusCode,
                                "mime_type" to mimeType,
                                "content_length" to length
                            )
                        )
                    )
                }
            }
        } catch (e: IOException) {
            // Network error — emit a failure result
            onResult(
                ScanResult(
                    target = target,
                    sourceType = SourceType.WAYBACK_MACHINE,
                    resultType = ResultType.RAW_TEXT,
                    module = CrawlerModule.WAYBACK_CDX.displayName,
                    title = "CDX API Error",
                    url = url,
                    snippet = "Failed to reach CDX API: ${e.message}",
                    rawData = e.toString()
                )
            )
        }
    }

    /** Check if a specific URL has at least one archived snapshot. */
    suspend fun checkAvailability(targetUrl: String, onResult: suspend (ScanResult) -> Unit) {
        val url = "$AVAILABILITY_BASE?url=${targetUrl}"
        val request = Request.Builder().url(url).build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return
                val body = response.body?.string() ?: return

                val json = gson.fromJson(body, AvailabilityResponse::class.java)
                val snapshot = json.archived_snapshots?.closest

                if (snapshot != null && snapshot.available == true) {
                    onResult(
                        ScanResult(
                            target = targetUrl,
                            sourceType = SourceType.WAYBACK_MACHINE,
                            resultType = ResultType.ARCHIVED_PAGE,
                            module = CrawlerModule.WAYBACK_AVAILABILITY.displayName,
                            title = "Archived snapshot available",
                            url = snapshot.url ?: "",
                            snippet = "Snapshot from ${snapshot.timestamp} · Status ${snapshot.status}",
                            metadata = mapOf(
                                "archived_url" to (snapshot.url ?: ""),
                                "snapshot_timestamp" to (snapshot.timestamp ?: ""),
                                "http_status" to (snapshot.status ?: "")
                            )
                        )
                    )
                }
            }
        } catch (e: IOException) {
            // Silently skip — availability check is best-effort
        }
    }

    private fun formatTimestamp(ts: String): String {
        if (ts.length < 8) return ts
        return "${ts.substring(0,4)}-${ts.substring(4,6)}-${ts.substring(6,8)}"
    }

    private fun mimeToResultType(mime: String, url: String): ResultType {
        return when {
            mime.contains("pdf", true) || url.endsWith(".pdf", true) -> ResultType.DOCUMENT
            mime.contains("html", true) -> ResultType.ARCHIVED_PAGE
            url.endsWith(".doc", true) || url.endsWith(".docx", true) ||
                url.endsWith(".xls", true) || url.endsWith(".xlsx", true) -> ResultType.DOCUMENT
            else -> ResultType.URL
        }
    }

    // CDX availability API response models
    private data class AvailabilityResponse(val archived_snapshots: Snapshots?)
    private data class Snapshots(val closest: Snapshot?)
    private data class Snapshot(val available: Boolean?, val url: String?, val timestamp: String?, val status: String?)
}
