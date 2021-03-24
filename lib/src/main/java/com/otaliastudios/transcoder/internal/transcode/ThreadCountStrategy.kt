package com.otaliastudios.transcoder.internal.transcode

/**
 * weiping@atlasv.com
 * 3/25/21
 */
interface ThreadCountStrategy {
    fun getThreadCount(): Int
}