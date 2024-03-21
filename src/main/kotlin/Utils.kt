package org.gladkowski

import sun.misc.Signal
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.function.Consumer

class ShutdownUtil {
    private val latch = CountDownLatch(1)

    fun remove() {
        synchronized(COUNTDOWN_LATCHES) {
            COUNTDOWN_LATCHES.remove(this.latch)
        }
    }

    fun await() {
        try {
            latch.await()
        } catch (var2: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }

    init {
        synchronized(COUNTDOWN_LATCHES) {
            COUNTDOWN_LATCHES.add(this.latch)
        }
    }

    companion object {
        private val SIGNAL_NAMES: Array<String> = arrayOf("INT", "TERM")
        private val COUNTDOWN_LATCHES: ArrayList<CountDownLatch?> = ArrayList<CountDownLatch?>()
        private fun signalAndClearAll() {
            synchronized(COUNTDOWN_LATCHES) {
                COUNTDOWN_LATCHES.forEach(Consumer { obj: CountDownLatch? -> obj!!.countDown() })
                COUNTDOWN_LATCHES.clear()
            }
        }

        init {
            val var0 = SIGNAL_NAMES
            val var1 = var0.size

            for (var2 in 0 until var1) {
                val signalName = var0[var2]
                SignalUtil.register(signalName) { signalAndClearAll() }
            }
        }
    }
}

object SignalUtil {
    fun register(signalName: String?, task: Runnable) {
        Objects.requireNonNull(task)
        Signal.handle(Signal(signalName)) { signal: Signal? ->
            task.run()
        }
    }
}
