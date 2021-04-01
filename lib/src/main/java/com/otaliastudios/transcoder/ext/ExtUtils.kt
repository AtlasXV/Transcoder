package com.otaliastudios.transcoder.ext

import android.media.MediaMetadataRetriever

/**
 * weiping@atlasv.com
 * 4/1/21
 */
object ExtUtils {
    fun getMediaLocation(retriever: MediaMetadataRetriever): String? {
        return kotlin.runCatching {
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_LOCATION)
        }.getOrNull()
    }
}