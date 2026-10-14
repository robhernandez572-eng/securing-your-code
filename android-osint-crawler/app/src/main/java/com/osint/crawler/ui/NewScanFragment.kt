package com.osint.crawler.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.osint.crawler.R
import com.osint.crawler.databinding.FragmentNewScanBinding
import com.osint.crawler.models.CrawlerModule
import com.osint.crawler.models.ScanConfig

class NewScanFragment : Fragment() {

    private var _binding: FragmentNewScanBinding? = null
    private val binding get() = _binding!!

    private val selectedModules = mutableSetOf<CrawlerModule>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buildModuleChips()
        restoreSavedTarget()

        binding.btnStartScan.setOnClickListener { startScan() }
        binding.btnSelectAll.setOnClickListener { selectAllModules() }
        binding.btnClearAll.setOnClickListener { clearAllModules() }

        // Dark web toggle warning
        binding.switchDarkWeb.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                Snackbar.make(
                    binding.root,
                    "Dark Web requires Orbot (Tor) installed and running",
                    Snackbar.LENGTH_LONG
                ).setAction("OK") {}.show()
            }
        }
    }

    private fun buildModuleChips() {
        val categories = CrawlerModule.values().groupBy { it.category }

        categories.forEach { (category, modules) ->
            // Category header
            val header = TextView(requireContext()).apply {
                text = category
                setTextAppearance(com.google.android.material.R.style.TextAppearance_MaterialComponents_Caption)
                setPadding(0, 24, 0, 8)
            }
            binding.moduleContainer.addView(header)

            modules.forEach { module ->
                val chip = Chip(requireContext()).apply {
                    text = module.displayName
                    isCheckable = true
                    isChecked = module.category != "Dark Web" // default: all non-dark-web selected
                    if (isChecked) selectedModules.add(module)

                    // Disable dark web chips until dark web toggle is on
                    if (module.category == "Dark Web") {
                        binding.switchDarkWeb.setOnCheckedChangeListener { _, enabled ->
                            this.isEnabled = enabled
                            if (!enabled) {
                                this.isChecked = false
                                selectedModules.remove(module)
                            }
                        }
                        isEnabled = false
                    }

                    setOnCheckedChangeListener { _, checked ->
                        if (checked) selectedModules.add(module) else selectedModules.remove(module)
                    }
                }
                binding.moduleContainer.addView(chip)
            }
        }
    }

    private fun selectAllModules() {
        for (i in 0 until binding.moduleContainer.childCount) {
            val child = binding.moduleContainer.getChildAt(i)
            if (child is Chip && child.isEnabled) {
                child.isChecked = true
            }
        }
    }

    private fun clearAllModules() {
        for (i in 0 until binding.moduleContainer.childCount) {
            val child = binding.moduleContainer.getChildAt(i)
            if (child is Chip) child.isChecked = false
        }
    }

    private fun restoreSavedTarget() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        binding.etTarget.setText(prefs.getString("last_target", ""))
    }

    private fun startScan() {
        val target = binding.etTarget.text.toString().trim()
        if (target.isBlank()) {
            binding.etTarget.error = "Enter a target URL, domain, or .onion address"
            return
        }

        if (selectedModules.isEmpty()) {
            Snackbar.make(binding.root, "Select at least one module", Snackbar.LENGTH_SHORT).show()
            return
        }

        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        prefs.edit { putString("last_target", target) }

        val config = ScanConfig(
            target = target,
            modules = selectedModules.toList(),
            maxDepth = binding.sliderDepth.value.toInt(),
            maxPages = binding.sliderMaxPages.value.toInt(),
            torEnabled = binding.switchDarkWeb.isChecked,
            torHost = prefs.getString("tor_host", "127.0.0.1") ?: "127.0.0.1",
            torPort = prefs.getString("tor_port", "9050")?.toIntOrNull() ?: 9050,
            waybackEnabled = selectedModules.any {
                it == CrawlerModule.WAYBACK_CDX || it == CrawlerModule.WAYBACK_AVAILABILITY
            },
            surfaceWebEnabled = selectedModules.any { it.category == "Surface Web" },
            darkWebEnabled = binding.switchDarkWeb.isChecked,
            followLinks = binding.switchFollowLinks.isChecked,
            userAgent = prefs.getString("user_agent", "Mozilla/5.0 (compatible; OSINTCrawler/1.0)")
                ?: "Mozilla/5.0 (compatible; OSINTCrawler/1.0)"
        )

        val intent = Intent(requireContext(), ScanActivity::class.java).apply {
            putExtra(ScanActivity.EXTRA_CONFIG_TARGET, config.target)
            putExtra(ScanActivity.EXTRA_CONFIG_MODULES, config.modules.map { it.name }.toTypedArray())
            putExtra(ScanActivity.EXTRA_CONFIG_MAX_DEPTH, config.maxDepth)
            putExtra(ScanActivity.EXTRA_CONFIG_MAX_PAGES, config.maxPages)
            putExtra(ScanActivity.EXTRA_CONFIG_TOR_ENABLED, config.torEnabled)
            putExtra(ScanActivity.EXTRA_CONFIG_TOR_HOST, config.torHost)
            putExtra(ScanActivity.EXTRA_CONFIG_TOR_PORT, config.torPort)
            putExtra(ScanActivity.EXTRA_CONFIG_FOLLOW_LINKS, config.followLinks)
            putExtra(ScanActivity.EXTRA_CONFIG_USER_AGENT, config.userAgent)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
