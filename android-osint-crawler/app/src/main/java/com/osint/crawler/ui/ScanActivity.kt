package com.osint.crawler.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.osint.crawler.R
import com.osint.crawler.adapters.ResultsAdapter
import com.osint.crawler.databinding.ActivityScanBinding
import com.osint.crawler.models.*
import com.osint.crawler.modules.CrawlerEngine
import com.osint.crawler.utils.ExportUtils
import kotlinx.coroutines.launch

class ScanActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CONFIG_TARGET = "extra_target"
        const val EXTRA_CONFIG_MODULES = "extra_modules"
        const val EXTRA_CONFIG_MAX_DEPTH = "extra_max_depth"
        const val EXTRA_CONFIG_MAX_PAGES = "extra_max_pages"
        const val EXTRA_CONFIG_TOR_ENABLED = "extra_tor_enabled"
        const val EXTRA_CONFIG_TOR_HOST = "extra_tor_host"
        const val EXTRA_CONFIG_TOR_PORT = "extra_tor_port"
        const val EXTRA_CONFIG_FOLLOW_LINKS = "extra_follow_links"
        const val EXTRA_CONFIG_USER_AGENT = "extra_user_agent"
    }

    private lateinit var binding: ActivityScanBinding
    private lateinit var adapter: ResultsAdapter
    private lateinit var engine: CrawlerEngine
    private val allResults = mutableListOf<ScanResult>()
    private var currentFilter: SourceType? = null
    private var scanStatus: ScanStatus = ScanStatus.RUNNING

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val config = buildConfigFromIntent()
        supportActionBar?.title = config.target
        supportActionBar?.subtitle = "${config.modules.size} modules"

        adapter = ResultsAdapter { result ->
            val i = Intent(this, ResultDetailActivity::class.java)
            i.putExtra(ResultDetailActivity.EXTRA_RESULT, result)
            startActivity(i)
        }

        binding.recyclerResults.layoutManager = LinearLayoutManager(this)
        binding.recyclerResults.adapter = adapter

        setupFilterChips()

        engine = CrawlerEngine(config)
        startScan()
    }

    private fun startScan() {
        lifecycleScope.launch {
            engine.results.collect { result ->
                allResults.add(result)
                if (currentFilter == null || result.sourceType == currentFilter) {
                    adapter.addResult(result)
                    binding.recyclerResults.scrollToPosition(adapter.itemCount - 1)
                }
                updateStats()
            }
        }

        lifecycleScope.launch {
            engine.progress.collect { update ->
                binding.tvProgress.text = "${update.currentModule} — ${update.message}"
                binding.tvResultCount.text = "${update.resultsFound} findings · ${update.pagesScanned} pages"
            }
        }

        lifecycleScope.launch {
            engine.status.collect { status ->
                scanStatus = status
                when (status) {
                    ScanStatus.COMPLETED -> onScanFinished("Scan completed")
                    ScanStatus.FAILED -> onScanFinished("Scan failed")
                    ScanStatus.CANCELLED -> onScanFinished("Scan cancelled")
                    ScanStatus.RUNNING -> {}
                }
            }
        }

        engine.start(lifecycleScope)
    }

    private fun onScanFinished(msg: String) {
        binding.progressBar.visibility = View.GONE
        binding.tvProgress.text = "$msg · ${allResults.size} total findings"
        invalidateOptionsMenu()
    }

    private fun updateStats() {
        val byType = allResults.groupBy { it.sourceType }
        binding.chipSurface.text = "Surface (${byType[SourceType.SURFACE_WEB]?.size ?: 0})"
        binding.chipWayback.text = "Wayback (${byType[SourceType.WAYBACK_MACHINE]?.size ?: 0})"
        binding.chipDark.text = "Dark Web (${byType[SourceType.DARK_WEB_ONION]?.size ?: 0})"
    }

    private fun setupFilterChips() {
        binding.chipAll.setOnClickListener { applyFilter(null) }
        binding.chipSurface.setOnClickListener { applyFilter(SourceType.SURFACE_WEB) }
        binding.chipWayback.setOnClickListener { applyFilter(SourceType.WAYBACK_MACHINE) }
        binding.chipDark.setOnClickListener { applyFilter(SourceType.DARK_WEB_ONION) }
    }

    private fun applyFilter(type: SourceType?) {
        currentFilter = type
        val filtered = if (type == null) allResults else allResults.filter { it.sourceType == type }
        adapter.setResults(filtered)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.scan_menu, menu)
        menu.findItem(R.id.action_stop)?.isVisible = scanStatus == ScanStatus.RUNNING
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> { onBackPressedDispatcher.onBackPressed(); true }
            R.id.action_stop -> {
                engine.cancel()
                true
            }
            R.id.action_export_json -> {
                ExportUtils.exportJson(this, allResults)
                true
            }
            R.id.action_export_csv -> {
                ExportUtils.exportCsv(this, allResults)
                true
            }
            R.id.action_filter -> {
                showFilterDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showFilterDialog() {
        val types = arrayOf("All", "Surface Web", "Wayback Machine", "Dark Web / Onion")
        MaterialAlertDialogBuilder(this)
            .setTitle("Filter by source")
            .setItems(types) { _, which ->
                applyFilter(when (which) {
                    1 -> SourceType.SURFACE_WEB
                    2 -> SourceType.WAYBACK_MACHINE
                    3 -> SourceType.DARK_WEB_ONION
                    else -> null
                })
            }
            .show()
    }

    private fun buildConfigFromIntent(): ScanConfig {
        val moduleNames = intent.getStringArrayExtra(EXTRA_CONFIG_MODULES) ?: emptyArray()
        val modules = moduleNames.mapNotNull {
            try { CrawlerModule.valueOf(it) } catch (_: Exception) { null }
        }
        return ScanConfig(
            target = intent.getStringExtra(EXTRA_CONFIG_TARGET) ?: "",
            modules = modules,
            maxDepth = intent.getIntExtra(EXTRA_CONFIG_MAX_DEPTH, 2),
            maxPages = intent.getIntExtra(EXTRA_CONFIG_MAX_PAGES, 100),
            torEnabled = intent.getBooleanExtra(EXTRA_CONFIG_TOR_ENABLED, false),
            torHost = intent.getStringExtra(EXTRA_CONFIG_TOR_HOST) ?: "127.0.0.1",
            torPort = intent.getIntExtra(EXTRA_CONFIG_TOR_PORT, 9050),
            followLinks = intent.getBooleanExtra(EXTRA_CONFIG_FOLLOW_LINKS, true),
            userAgent = intent.getStringExtra(EXTRA_CONFIG_USER_AGENT)
                ?: "Mozilla/5.0 (compatible; OSINTCrawler/1.0)"
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        engine.cancel()
    }
}
