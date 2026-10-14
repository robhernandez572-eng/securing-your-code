package com.osint.crawler.modules

import com.osint.crawler.models.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

/**
 * Orchestrates all crawling modules for a scan session.
 * Emits results as a Flow so the UI can update in real time.
 */
class CrawlerEngine(private val config: ScanConfig) {

    private val _results = MutableSharedFlow<ScanResult>(extraBufferCapacity = 500)
    val results: SharedFlow<ScanResult> = _results

    private val _status = MutableSharedFlow<ScanStatus>(extraBufferCapacity = 20)
    val status: SharedFlow<ScanStatus> = _status

    private val _progress = MutableSharedFlow<ProgressUpdate>(extraBufferCapacity = 100)
    val progress: SharedFlow<ProgressUpdate> = _progress

    private var job: Job? = null

    data class ProgressUpdate(
        val currentModule: String,
        val pagesScanned: Int,
        val resultsFound: Int,
        val message: String
    )

    fun start(scope: CoroutineScope) {
        job = scope.launch(Dispatchers.IO) {
            _status.emit(ScanStatus.RUNNING)

            try {
                // Run enabled module groups in parallel where safe
                val tasks = mutableListOf<Deferred<Unit>>()

                if (config.waybackEnabled && config.modules.any {
                    it == CrawlerModule.WAYBACK_CDX || it == CrawlerModule.WAYBACK_AVAILABILITY
                }) {
                    tasks.add(async { runWaybackModules() })
                }

                if (config.surfaceWebEnabled && config.modules.any {
                    it.category == "Surface Web"
                }) {
                    tasks.add(async { runSurfaceModules() })
                }

                if (config.darkWebEnabled && config.modules.any {
                    it.category == "Dark Web"
                }) {
                    tasks.add(async { runDarkWebModules() })
                }

                tasks.awaitAll()
                _status.emit(ScanStatus.COMPLETED)
            } catch (e: CancellationException) {
                _status.emit(ScanStatus.CANCELLED)
            } catch (e: Exception) {
                _results.emit(ScanResult(
                    target = config.target,
                    sourceType = SourceType.SURFACE_WEB,
                    resultType = ResultType.RAW_TEXT,
                    module = "Engine",
                    title = "Scan Error",
                    url = config.target,
                    snippet = e.message ?: "Unknown error",
                    rawData = e.stackTraceToString()
                ))
                _status.emit(ScanStatus.FAILED)
            }
        }
    }

    fun cancel() {
        job?.cancel()
    }

    private suspend fun runWaybackModules() {
        val wayback = WaybackMachineModule(config)
        var count = 0

        emitProgress("Wayback Machine CDX", 0, count, "Querying archive.org CDX API…")

        if (CrawlerModule.WAYBACK_CDX in config.modules) {
            wayback.fetchCdxResults(config.target, limit = config.maxPages) { result ->
                _results.emit(result)
                count++
                if (count % 10 == 0) emitProgress(
                    "Wayback CDX", 1, count,
                    "Retrieved $count archived URLs"
                )
            }
        }

        if (CrawlerModule.WAYBACK_AVAILABILITY in config.modules) {
            emitProgress("Wayback Availability", 1, count, "Checking snapshot availability…")
            wayback.checkAvailability(config.target) { result ->
                _results.emit(result)
                count++
            }
        }
    }

    private suspend fun runSurfaceModules() {
        val surface = SurfaceWebModule(config)
        val surfaceModules = config.modules.filter { it.category == "Surface Web" }
        val visited = mutableSetOf<String>()
        val queue = ArrayDeque<Pair<String, Int>>() // url, depth
        queue.add(Pair(config.target, 0))
        var pagesCount = 0
        var resultsCount = 0

        while (queue.isNotEmpty() && pagesCount < config.maxPages) {
            val (url, depth) = queue.removeFirst()
            if (url in visited) continue
            if (!url.startsWith("http")) continue
            visited.add(url)
            pagesCount++

            emitProgress("Surface Web", pagesCount, resultsCount, "Scanning $url (depth $depth)")

            val newLinks = surface.crawlUrl(url, surfaceModules) { result ->
                _results.emit(result)
                resultsCount++
            }

            // Enqueue discovered links up to max depth
            if (config.followLinks && depth < config.maxDepth) {
                val targetDomain = extractDomain(config.target)
                newLinks.filter { link ->
                    extractDomain(link) == targetDomain && link !in visited
                }.take(50).forEach { link ->
                    queue.add(Pair(link, depth + 1))
                }
            }
        }
    }

    private suspend fun runDarkWebModules() {
        val darkWeb = DarkWebModule(config)
        val darkModules = config.modules.filter { it.category == "Dark Web" }
        val visited = mutableSetOf<String>()
        val queue = ArrayDeque<Pair<String, Int>>()
        queue.add(Pair(config.target, 0))
        var pagesCount = 0
        var resultsCount = 0

        while (queue.isNotEmpty() && pagesCount < config.maxPages) {
            val (url, depth) = queue.removeFirst()
            if (url in visited) continue
            visited.add(url)
            pagesCount++

            emitProgress("Dark Web / Tor", pagesCount, resultsCount, "Crawling onion: $url")

            val newOnions = darkWeb.crawlOnionUrl(url, darkModules, depth) { result ->
                _results.emit(result)
                resultsCount++
            }

            if (config.followLinks && depth < config.maxDepth) {
                newOnions.filter { it !in visited }.take(10).forEach { onion ->
                    queue.add(Pair(onion, depth + 1))
                }
            }
        }
    }

    private suspend fun emitProgress(module: String, pages: Int, results: Int, msg: String) {
        _progress.emit(ProgressUpdate(module, pages, results, msg))
    }

    private fun extractDomain(url: String): String {
        return try {
            val u = if (url.startsWith("http")) url else "http://$url"
            java.net.URI(u).host ?: url
        } catch (_: Exception) { url }
    }
}
