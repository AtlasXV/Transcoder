package com.otaliastudios.transcoder.internal.transcode

import com.otaliastudios.transcoder.Transcoder
import com.otaliastudios.transcoder.TranscoderOptions
import com.otaliastudios.transcoder.internal.DataSources
import com.otaliastudios.transcoder.internal.utils.Logger
import com.otaliastudios.transcoder.internal.utils.trackMapOf
import java.util.concurrent.atomic.AtomicInteger

internal abstract class TranscodeEngine {

    abstract fun validate(): Boolean

    abstract fun transcode(progress: (Double) -> Unit)

    abstract fun cleanup()

    companion object {
        val log = Logger("TranscodeEngine")

        fun Throwable.isInterrupted(): Boolean {
            if (this is InterruptedException) return true
            if (this == this.cause) return false
            return this.cause?.isInterrupted() ?: false
        }

        val transcodingCount = AtomicInteger(0)
        val transcodingLock = Object()

        @JvmStatic
        fun transcode(options: TranscoderOptions) {
            transcodingCount.incrementAndGet()
            log.i("[transcode]transcode() called, transcodingCount=$transcodingCount")
            var engine: TranscodeEngine? = null
            val dispatcher = TranscodeDispatcher(options)
            try {
                engine = DefaultTranscodeEngine(
                        dataSources = DataSources(options),
                        dataSink = options.dataSink,
                        strategies = trackMapOf(
                                video = options.videoTrackStrategy,
                                audio = options.audioTrackStrategy
                        ),
                        validator = options.validator,
                        videoRotation = options.videoRotation,
                        interpolator = options.timeInterpolator,
                        audioStretcher = options.audioStretcher,
                        audioResampler = options.audioResampler,
                        glDrawStrategy = options.glDrawStrategy
                )
                if (!engine.validate()) {
                    dispatcher.dispatchSuccess(Transcoder.SUCCESS_NOT_NEEDED)
                } else {
                    engine.transcode {
                        dispatcher.dispatchProgress(it)
                    }
                    dispatcher.dispatchSuccess(Transcoder.SUCCESS_TRANSCODED)
                }
            } catch (e: Exception) {
                if (e.isInterrupted()) {
                    log.i("[transcode]Transcode canceled.", e)
                    dispatcher.dispatchCancel()
                } else {
                    log.e("[transcode]Unexpected error while transcoding.", e)
                    throw e
                }
            } finally {
                transcodingCount.decrementAndGet()
                log.i("[transcode]transcode() finished, transcodingCount=$transcodingCount")
                engine?.cleanup()
                synchronized(transcodingLock) {
                    transcodingLock.notifyAll()
                }
            }
        }
    }
}