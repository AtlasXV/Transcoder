package com.otaliastudios.transcoder.internal.utils

import com.otaliastudios.transcoder.Transcoder.threadCountStrategy
import com.otaliastudios.transcoder.internal.transcode.TranscodeEngine
import java.util.*
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

internal object ThreadPool {

    /**
     * NOTE: A better maximum pool size (instead of CPU+1) would be the number of MediaCodec
     * instances that the device can handle at the same time. Hard to tell though as that
     * also depends on the codec type / on input data.
     */
    @JvmStatic
    val executor = ThreadPoolExecutor(
            getThreadCount(),
            getThreadCount(),
            60,
            TimeUnit.SECONDS,
            LinkedBlockingQueue(),
            object : ThreadFactory {
                private val count = AtomicInteger(1)
                override fun newThread(r: Runnable): Thread {
                    return Thread(r, "TranscoderThread(parallel) #" + count.getAndIncrement())
                }
            })

    @JvmStatic
    val serialExecutor = ThreadPoolExecutor(
            1, 1, 60, TimeUnit.SECONDS,
            LinkedBlockingQueue(),
            object : ThreadFactory {
                private val count = AtomicInteger(1)
                override fun newThread(r: Runnable): Thread {
                    return Thread(r, "TranscoderThread(serial) #" + count.getAndIncrement())
                }
            })

    private fun getThreadCount(): Int {
        return threadCountStrategy?.getThreadCount() ?: (Runtime.getRuntime().availableProcessors() + 1)
    }

    private val runningFutureSet = Collections.synchronizedSet(HashSet<Future<Void?>>())


    @JvmStatic
    fun submit(callable: Callable<Void?>, parallel: Boolean): Future<Void?> {
        val future = if (parallel) {
            executor
        } else {
            serialExecutor
        }.submit(callable)
        trackFuture(future)
        return future
    }

    private fun trackFuture(future: Future<Void?>) {
        runningFutureSet.add(future)
        executor.submit {
            try {
                future.get()
            } catch (t: Throwable) {
                TranscodeEngine.log.w("[transcode]get future result fail", t)
            } finally {
                runningFutureSet.remove(future)
            }
            TranscodeEngine.log.i("[transcode]remove future, current set size: ${runningFutureSet.size}")
        }
    }

    @JvmStatic
    fun cancel(mayInterruptIfRunning: Boolean) {
        for (future in runningFutureSet) {
            if (future.isCancelled || future.isDone) {
                continue
            }
            future.cancel(mayInterruptIfRunning)
        }
    }
}