package com.osint.crawler.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.osint.crawler.databinding.ItemResultBinding
import com.osint.crawler.models.ResultType
import com.osint.crawler.models.ScanResult
import com.osint.crawler.models.SourceType

class ResultsAdapter(
    private val onItemClick: (ScanResult) -> Unit
) : RecyclerView.Adapter<ResultsAdapter.ViewHolder>() {

    private val results = mutableListOf<ScanResult>()

    fun addResult(result: ScanResult) {
        results.add(result)
        notifyItemInserted(results.size - 1)
    }

    fun setResults(newResults: List<ScanResult>) {
        results.clear()
        results.addAll(newResults)
        notifyDataSetChanged()
    }

    override fun getItemCount() = results.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(results[position])
    }

    inner class ViewHolder(private val binding: ItemResultBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(result: ScanResult) {
            binding.tvTitle.text = result.title
            binding.tvSnippet.text = result.snippet.take(120)
            binding.tvModule.text = result.module
            binding.tvType.text = result.resultType.name.replace('_', ' ')

            // Source badge color
            val badgeColor = when (result.sourceType) {
                SourceType.SURFACE_WEB -> 0xFF1976D2.toInt()        // Blue
                SourceType.WAYBACK_MACHINE -> 0xFF388E3C.toInt()    // Green
                SourceType.DARK_WEB_ONION -> 0xFF7B1FA2.toInt()     // Purple
            }
            binding.viewSourceBadge.setBackgroundColor(badgeColor)

            // Result type icon
            binding.ivResultIcon.setImageResource(resultTypeIcon(result.resultType))

            binding.root.setOnClickListener { onItemClick(result) }
        }

        private fun resultTypeIcon(type: ResultType): Int {
            return when (type) {
                ResultType.URL -> android.R.drawable.ic_menu_compass
                ResultType.EMAIL -> android.R.drawable.ic_dialog_email
                ResultType.PHONE -> android.R.drawable.ic_menu_call
                ResultType.IP_ADDRESS -> android.R.drawable.ic_menu_manage
                ResultType.DOMAIN -> android.R.drawable.ic_menu_compass
                ResultType.SOCIAL_MEDIA -> android.R.drawable.ic_menu_share
                ResultType.DOCUMENT -> android.R.drawable.ic_menu_save
                ResultType.CREDENTIAL_EXPOSURE -> android.R.drawable.ic_lock_lock
                ResultType.RAW_TEXT -> android.R.drawable.ic_menu_edit
                ResultType.ONION_SERVICE -> android.R.drawable.ic_menu_compass
                ResultType.ARCHIVED_PAGE -> android.R.drawable.ic_menu_recent_history
            }
        }
    }
}
