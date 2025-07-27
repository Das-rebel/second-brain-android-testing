package com.secondbrain.app.util

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.net.URL
/**
 * Utility class for extracting metadata from web pages.
 */
class WebMetadataExtractor {
    
    /**
     * Extracts metadata from a web page.
     * @param urlString The URL of the web page
     * @return A [WebMetadata] object containing the extracted metadata
     */
    suspend fun extractMetadata(urlString: String): WebMetadata {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(urlString)
                val connection = Jsoup.connect(urlString)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .referrer("http://www.google.com")
                    .timeout(10000)
                    .followRedirects(true)
                    .get()

                val title = connection.title()
                val description = connection.select("meta[name=description]").attr("content")
                    .takeIf { it.isNotBlank() } 
                    ?: connection.select("meta[property=og:description]").attr("content")
                        .takeIf { it.isNotBlank() }
                    ?: ""

                // Try to get favicon
                var faviconUrl = connection.select("link[rel~=(?i)icon|apple-touch-icon]").first()?.attr("href")
                if (faviconUrl.isNullOrBlank()) {
                    faviconUrl = "${url.protocol}://${url.host}/favicon.ico"
                } else if (faviconUrl.startsWith("/")) {
                    faviconUrl = "${url.protocol}://${url.host}$faviconUrl"
                } else if (faviconUrl.startsWith("//")) {
                    faviconUrl = "${url.protocol}:$faviconUrl"
                } else if (!faviconUrl.startsWith("http")) {
                    faviconUrl = "${url.protocol}://${url.host}/$faviconUrl"
                }

                WebMetadata(
                    title = title.ifBlank { url.host },
                    description = description,
                    faviconUrl = faviconUrl,
                    domain = url.host
                )
            } catch (e: Exception) {
                Log.e("WebMetadataExtractor", "Error extracting metadata: ${e.message}")
                // Fallback to basic URL parsing if metadata extraction fails
                try {
                    val url = URL(urlString)
                    WebMetadata(
                        title = url.host,
                        description = "",
                        faviconUrl = "${url.protocol}://${url.host}/favicon.ico",
                        domain = url.host
                    )
                } catch (e: Exception) {
                    WebMetadata(
                        title = urlString,
                        description = "",
                        faviconUrl = null,
                        domain = null
                    )
                }
            }
        }
    }
}

/**
 * Data class representing web page metadata.
 */
data class WebMetadata(
    val title: String,
    val description: String,
    val faviconUrl: String?,
    val domain: String?
)
