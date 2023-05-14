package xyz.erichg

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

const val KEY_MILLISECONDS_REMAINING = "xyz.erichg.MILLIS_LEFT"

class TimerWorker(context: Context, parameters: WorkerParameters): Worker(context, parameters){

    override fun doWork(): Result {
        val remainingMillis = inputData.getLong(KEY_MILLISECONDS_REMAINING,0)
        if(remainingMillis == 0L)
        {
            return Result.failure()
        }
        val timer = Timer()
        timer.start(remainingMillis)
        while(timer.isRunning)
        {
            Thread.sleep(100)
            if(timer.remainingMilliseconds == 0L)
            {
                timer.stop()
            }
        }
        return Result.success()
    }

}