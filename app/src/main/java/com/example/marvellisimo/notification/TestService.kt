package com.example.marvellisimo.notification

import android.app.IntentService
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "TestService"

class TestService : JobService() {

    override fun onStopJob(p0: JobParameters?): Boolean {
        return true
    }

    override fun onStartJob(p0: JobParameters?): Boolean {
        Log.d(TAG, "onHandleIntent: starts")

        CoroutineScope(IO).launch {
            while (true) {
                Log.d(TAG, "ping")
                delay(10000)
            }
        }
        return true
    }


}