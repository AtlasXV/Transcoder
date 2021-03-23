package com.otaliastudios.transcoder.ext

import com.otaliastudios.transcoder.TranscoderOptions

/**
 * weiping@atlasv.com
 * 3/23/21
 */
class DefaultTranscodeOptionFactory(private val transcoderOptions: TranscoderOptions) : TranscodeOptionFactory {
    override fun create(): TranscoderOptions {
        return transcoderOptions
    }
}