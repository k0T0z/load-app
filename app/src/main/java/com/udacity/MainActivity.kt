package com.udacity

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

enum class Title {
    GLIDE, LOAD_APP, RETROFIT
}

class MainActivity : AppCompatActivity() {
    private var radioGroup: RadioGroup? = null
    private var selectedOption: Int = -1

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        custom_button.setOnClickListener {
            val notificationManager =
                ContextCompat.getSystemService(
                    this,
                    NotificationManager::class.java
                ) as NotificationManager
            notificationManager.cancelNotifications()

            radioGroup = findViewById(R.id.radio_list)
            selectedOption = radioGroup!!.checkedRadioButtonId

            when (selectedOption) {
                R.id.glide_radio_button -> download(URL_GLIDE)
                R.id.load_app_starter_radio_button -> download(URL_LOAD_APP)
                R.id.retrofit_radio_button -> download(URL_RETROFIT)
                else -> Toast.makeText(baseContext, R.string.error_state, Toast.LENGTH_SHORT).show()
            }
        }

        createChannel(
            getString(R.string.load_app_notification_channel_id),
            getString(R.string.load_app_notification_channel_name)
        )
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(receiver)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            notificationManager = context?.let {
                ContextCompat.getSystemService(
                    it,
                    NotificationManager::class.java
                )
            } as NotificationManager

            val title: Title = when (selectedOption) {
                R.id.glide_radio_button -> Title.GLIDE
                R.id.load_app_starter_radio_button -> Title.LOAD_APP
                else -> Title.RETROFIT
            }

            when (id) {
                downloadID -> {
                    context.let {
                        notificationManager.sendNotification(
                            it.getText(R.string.download_finished).toString(),
                            it,
                            true,
                            title
                        )
                    }
                }
                else -> {
                    context.let {
                        notificationManager.sendNotification(
                            it.getText(R.string.download_finished).toString(),
                            it,
                            false,
                            title
                        )
                    }
                }
            }
        }
    }

    private fun download(url: String) {
        try {
            val request =
                DownloadManager.Request(
                    Uri.parse(url)
                )
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        }
        catch (e: Exception) {}
    }

    companion object {
        private const val URL_GLIDE =
            "https://github.com/bumptech/glide/archive/master.zip"
        private const val URL_LOAD_APP =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL_RETROFIT =
            "https://github.com/square/retrofit/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "download status channel"
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

}
