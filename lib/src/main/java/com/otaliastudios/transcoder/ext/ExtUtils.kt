package com.otaliastudios.transcoder.ext

import android.media.MediaCodec
import android.media.MediaMetadataRetriever
import android.os.Handler
import android.os.Looper
import com.otaliastudios.transcoder.TranscoderListener

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

    fun dispatchFailure(listener: TranscoderListener?, t: Throwable) {
        listener ?: return
        Handler(Looper.getMainLooper()).post { listener.onTranscodeFailed(t) }
    }

    fun dispatchCancel(listener: TranscoderListener?) {
        listener ?: return
        Handler(Looper.getMainLooper()).post { listener.onTranscodeCanceled() }
    }

    fun Throwable.canRetryTranscode(): Boolean {
        if (this is MediaCodec.CodecException) return true
        if (this == this.cause) return false
        return this.cause?.canRetryTranscode() ?: false
    }
}