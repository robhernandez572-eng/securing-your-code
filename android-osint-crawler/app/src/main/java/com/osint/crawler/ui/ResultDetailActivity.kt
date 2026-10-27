package com.osint.crawler.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.osint.crawler.databinding.ActivityResultDetailBinding
import com.osint.crawler.models.ScanResult
import com.osint.crawler.models.SourceType
import java.text.SimpleDateFormat
import java.util.*

class ResultDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_RESULT = "extra_result"
    }

    private lateinit var binding: ActivityResultDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val result = intent.getParcelableExtra<ScanResult>(EXTRA_RESULT) ?: return
        bindResult(result)
    }

    private fun bindResult(result: ScanResult) {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = sdf.format(Date(result.timestamp))

        supportActionBar?.title = result.resultType.name.replace('_', ' ')

        binding.tvModule.text = result.module
        binding.tvSource.text = when (result.sourceType) {
            SourceType.SURFACE_WEB -> "Surface Web"
            SourceType.WAYBACK_MACHINE -> "Wayback Machine / Archive.org"
            SourceType.DARK_WEB_ONION -> "Dark Web (.onion / Tor)"
        }
        binding.tvTitle.text = result.title
        binding.tvUrl.text = result.url
        binding.tvSnippet.text = result.snippet
        binding.tvTimestamp.text = date

        if (result.metadata.isNotEmpty()) {
            binding.tvMetadata.text = result.metadata.entries.joinToString("\n") { "${it.key}: ${it.value}" }
        } else {
            binding.metadataCard.visibility = android.view.View.GONE
        }

        if (result.rawData.isNotBlank()) {
            binding.tvRawData.text = result.rawData
        } else {
            binding.rawDataCard.visibility = android.view.View.GONE
        }

        binding.btnCopyUrl.setOnClickListener {
            copyToClipboard("URL", result.url)
        }

        binding.btnOpenUrl.setOnClickListener {
            // Only open surface URLs in browser — never auto-open .onion links
            if (result.sourceType != SourceType.DARK_WEB_ONION) {
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(result.url)))
                } catch (_: Exception) {
                    Snackbar.make(binding.root, "Cannot open URL", Snackbar.LENGTH_SHORT).show()
                }
            } else {
                Snackbar.make(
                    binding.root,
                    "Use Tor Browser (Orfox/Fennec) to open .onion links",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        binding.btnShare.setOnClickListener {
            val shareText = buildString {
                append("OSINT Crawler Finding\n\n")
                append("Type: ${result.resultType}\n")
                append("Source: ${result.sourceType}\n")
                append("Module: ${result.module}\n")
                append("Title: ${result.title}\n")
                append("URL: ${result.url}\n")
                append("Details: ${result.snippet}\n")
                append("Found: $date")
            }
            startActivity(Intent.createChooser(
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareText)
                },
                "Share finding"
            ))
        }
    }

    private fun copyToClipboard(label: String, text: String) {
        val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText(label, text))
        Snackbar.make(binding.root, "Copied to clipboard", Snackbar.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
