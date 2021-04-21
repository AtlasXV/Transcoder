package com.otaliastudios.transcoder.ext

import com.otaliastudios.transcoder.TranscoderListener
import com.otaliastudios.transcoder.ext.ExtUtils.canRetryTranscode
import com.otaliastudios.transcoder.ext.ExtUtils.dispatchFailure
import com.otaliastudios.transcoder.internal.transcode.TranscodeEngine
import com.otaliastudios.transcoder.internal.transcode.TranscodeEngine.Companion.transcode
import com.otaliastudios.transcoder.internal.utils.ThreadPool
import java.util.concurrent.Callable

/**
 * weiping@atlasv.com
 * 4/20/21
 */
class TranscodeCallable(private val factory: TranscodeOptionFactory, private val listener: TranscoderListener) : Callable<Void?> {
    override fun call(): Void? {
        try {
            transcode(factory.create(currentRetryTimes = 0))
        } catch (t: Throwable) {
            if (t.canRetryTranscode()) {
                TranscodeEngine.log.w("[transcode][retry]transcode fail, submit retry.")
                ThreadPool.submit(RetryTranscodeTask(factory, listener, t), parallel = false)
            } else {
                TranscodeEngine.log.e("[transcode][retry]transcode fail and can not retry, dispatch failure", t)
                dispatchFailure(listener, t)
            }
        }
        return null
    }
}
