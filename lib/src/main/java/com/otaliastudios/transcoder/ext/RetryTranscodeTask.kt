package com.otaliastudios.transcoder.ext

import com.otaliastudios.transcoder.TranscoderListener
import com.otaliastudios.transcoder.ext.ExtUtils.canRetryTranscode
import com.otaliastudios.transcoder.internal.transcode.TranscodeEngine
import com.otaliastudios.transcoder.internal.transcode.TranscodeEngine.Companion.isInterrupted
import com.otaliastudios.transcoder.internal.transcode.TranscodeEngine.Companion.transcode
import java.util.concurrent.Callable

/**
 * weiping@atlasv.com
 * 4/20/21
 */
class RetryTranscodeTask(private val factory: TranscodeOptionFactory, private val listener: TranscoderListener, private val t: Throwable) : Callable<Void?> {
    override fun call(): Void? {
        var times = 0
        while (times < factory.maxRetryTimes()) {
            if (Thread.currentThread().isInterrupted) {
                TranscodeEngine.log.w("[transcode][retry]Thread is interrupted, dispatch cancel event and break.")
                ExtUtils.dispatchCancel(listener)
                return null
            }
            if (TranscodeEngine.transcodingCount.get() == 0) {
                times++
                TranscodeEngine.log.i("[transcode][retry]retry start, current retry times: $times")
                try {
                    transcode(factory.create(currentRetryTimes = times))
                    return null
                } catch (t: Throwable) {
                    TranscodeEngine.log.w("[transcode][retry]retry fail once", t)
                    if (times >= factory.maxRetryTimes() || !t.canRetryTranscode()) {
                        TranscodeEngine.log.e("[transcode][retry]retry finish, dispatch failure event and break", t)
                        ExtUtils.dispatchFailure(listener, t)
                        return null
                    }
                }
            } else {
                try {
                    TranscodeEngine.log.w("[transcode][retry]waiting for transcoding task empty")
                    synchronized(TranscodeEngine.transcodingLock) {
                        TranscodeEngine.transcodingLock.wait()
                    }
                } catch (t: Throwable) {
                    if (t.isInterrupted()) {
                        TranscodeEngine.log.w("[transcode][retry]waiting for retry is interrupted, dispatch cancel event and break", t)
                        ExtUtils.dispatchCancel(listener)
                    } else {
                        ExtUtils.dispatchFailure(listener, t)
                    }
                    return null
                }
            }
        }
        ExtUtils.dispatchFailure(listener, t)
        return null
    }
}