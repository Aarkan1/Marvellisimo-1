package com.example.marvellisimo.notification

import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.marvellisimo.activity.character_details.CharacterDetailsActivity
import com.example.marvellisimo.MarvellisimoApplication
import com.example.marvellisimo.R
import com.example.marvellisimo.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

private const val TAG = "TestService"

class TestService : JobService() {

    @Inject
    lateinit var repository: Repository

    override fun onCreate() {
        super.onCreate()

        MarvellisimoApplication.applicationComponent.inject(this)
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        return true
    }

    override fun onStartJob(p0: JobParameters?): Boolean {
        Log.d(TAG, "onHandleIntent: starts")
        CoroutineScope(IO).launch {
            while (true) {
                if (Random.nextInt(10) < 2) showNotification()
                delay(10000)
            }
        }
        return true
    }

    private fun showNotification() = CoroutineScope(IO).launch {
        Log.d(TAG, "showNotification: starts ")

        val character = repository.fetchCharacterById("1009610") ?: return@launch

        val intent = Intent(applicationContext, CharacterDetailsActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        intent.putExtra("id", character.id)

        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)

        val builder = NotificationCompat.Builder(applicationContext, MarvellisimoApplication.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_menu_camera)
            .setContentTitle("Marvellisimo")
            .setContentText("Someone shared Spiderman with you")
            .setStyle(NotificationCompat.BigTextStyle().bigText("This is a longer text, This is a longer text, This is a longer text, This is a longer text, This is a longer text, This is a longer text, This is a longer text, This is a longer text, This is a longer text, This is a longer text, This is a longer text, This is a longer text, This is a longer text"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        CoroutineScope(Main).launch {
            with(NotificationManagerCompat.from(applicationContext)) {
                notify(0, builder.build())
            }
        }
    }
}