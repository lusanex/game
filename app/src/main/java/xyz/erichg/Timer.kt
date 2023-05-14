package xyz.erichg
import android.os.SystemClock

class Timer {

    private var targetTime: Long = 0
    private var timeLeft: Long = 0
    private var running = false
    private var durationMillis: Long = 0

    val isRunning: Boolean
    get(){
        return running
    }

    fun start(millisLeft: Long)
    {
        durationMillis = millisLeft
        targetTime = SystemClock.uptimeMillis() + durationMillis
        running = true
    }
    fun stop()
    {
        running = false
    }

    val remainingMilliseconds: Long
    get()
    {
        return if( running)
        {
            0L.coerceAtLeast(targetTime - SystemClock.uptimeMillis())
        } else 0

    }


}