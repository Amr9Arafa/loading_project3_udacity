package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.util.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    private lateinit var selectedDownloadUrl: String
    private lateinit var selectedURLDescription: String
    var downloadStatus = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)



        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {

            if (radioList.checkedRadioButtonId == -1) {
                Toast.makeText(this, getString(R.string.empty_selection_msg), Toast.LENGTH_SHORT)
                    .show()
            } else {
                isFileSelected = true
                val index = radioList.indexOfChild(findViewById(radioList.checkedRadioButtonId))
                when (index) {
                    0 -> {
                        selectedDownloadUrl = URL_GLIDE
                        selectedURLDescription = URL_GLIDE_DESCRIPTION
                    }
                    1 -> {
                        selectedDownloadUrl = URL_UDACITY
                        selectedURLDescription = URL_UDACITY_DESCRIPTION
                    }
                    else -> {
                        selectedDownloadUrl = URL_RETROFIT
                        selectedURLDescription = URL_RETROFIT_DESCRIPTION
                    }
                }

                download(selectedDownloadUrl)
            }
        }
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

            val action = intent?.action


            if (downloadID == id) {
                if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                    val query = DownloadManager.Query()
                    query.setFilterById(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0));
                    val downloadManager =
                        context!!.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    val cursor: Cursor = downloadManager.query(query)

                    if (cursor.moveToFirst()) {
                        if (cursor.count > 0) {
                            val status =
                                cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                            downloadStatus = if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                "Success"
                            } else {
                                "Fail"
                            }
                        }
                    }

                    Log.i("details", downloadStatus)
                }
            }
            notificationManager = ContextCompat.getSystemService(
                applicationContext,
                NotificationManager::class.java
            ) as NotificationManager
            notificationManager.sendNotification(
                selectedURLDescription,
                downloadStatus,
                applicationContext
            )
        }
    }

    private fun download(url: String) {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "DESCRIPTIONN"


            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

    companion object {
        private const val URL_UDACITY =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL_GLIDE =
            "https://github.com/bumptech/glide/archive/master.zip"
        private const val URL_RETROFIT =
            "https://github.com/square/retrofit/archive/master.zip"

        private const val URL_UDACITY_DESCRIPTION =
            "LoadApp - Current repository by Udacity"
        private const val URL_GLIDE_DESCRIPTION =
            "Glide - Image Loading Library by BumpTech"
        private const val URL_RETROFIT_DESCRIPTION =
            "Retrofit - Type-safe Http Client for Android and Java by Square, Inc "

        public var isFileSelected = false
    }

}
