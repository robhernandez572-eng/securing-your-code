package com.osint.crawler.utils

import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.core.content.FileProvider
import com.google.gson.GsonBuilder
import com.osint.crawler.models.ScanResult
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

object ExportUtils {

    private val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

    fun exportJson(context: Context, results: List<ScanResult>) {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val json = gson.toJson(results)
        val file = writeToExports(context, "osint_results_${sdf.format(Date())}.json", json)
        shareFile(context, file, "application/json")
    }

    fun exportCsv(context: Context, results: List<ScanResult>) {
        val sb = StringBuilder()
        sb.appendLine("id,target,source_type,result_type,module,title,url,snippet,timestamp")
        results.forEach { r ->
            sb.appendLine(
                listOf(
                    r.id, r.target, r.sourceType, r.resultType, r.module,
                    r.title.escapeCsv(), r.url.escapeCsv(),
                    r.snippet.escapeCsv(), r.timestamp
                ).joinToString(",")
            )
        }
        val file = writeToExports(context, "osint_results_${sdf.format(Date())}.csv", sb.toString())
        shareFile(context, file, "text/csv")
    }

    private fun String.escapeCsv(): String {
        return if (contains(',') || contains('"') || contains('\n')) {
            "\"${replace("\"", "\"\"")}\""
        } else this
    }

    private fun writeToExports(context: Context, filename: String, content: String): File {
        val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "OSINTCrawler")
        dir.mkdirs()
        val file = File(dir, filename)
        FileWriter(file).use { it.write(content) }
        return file
    }

    private fun shareFile(context: Context, file: File, mimeType: String) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Export results"))
    }
}
