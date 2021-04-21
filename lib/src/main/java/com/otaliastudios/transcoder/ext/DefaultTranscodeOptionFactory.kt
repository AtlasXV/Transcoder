package com.otaliastudios.transcoder.ext

import com.otaliastudios.transcoder.TranscoderOptions

/**
 * weiping@atlasv.com
 * 3/25/21
 */
class DefaultTranscodeOptionFactory(private val transcoderOptions: TranscoderOptions) : TranscodeOptionFactory {
    override fun create(currentRetryTimes: Int): TranscoderOptions {
        return transcoderOptions
    }

    override fun maxRetryTimes(): Int {
        return 0
    }
}