package org.hyperskill.stopwatch

import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat


class MainActivity : AppCompatActivity() {


    private var seconds = 0
    private var running = false
    private lateinit var resetBtn: Button
    private lateinit var startBtn: Button
    lateinit var handler: Handler
    lateinit var timer: TextView
    private lateinit var bar: ProgressBar
    private lateinit var settingsBtn: Button
    private lateinit var dialog: View
    private var limit: Int? = null
    private var channelId = "org.hyperskill"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()

        resetBtn.setOnClickListener {
            resetStopwatch()
        }

        startBtn.setOnClickListener {
            startStopwatch()
        }

        settingsBtn.setOnClickListener {
            showAlert()
        }
    }

    private fun resetStopwatch() {
        running = false
        seconds = 0
        settingsBtn.isEnabled = true
        bar.visibility = View.INVISIBLE
    }


    private fun startStopwatch() {
        super.onStart()
        if (!running) {
            bar.visibility = View.VISIBLE
            running = true
            handler.postDelayed(updateTimer, 100)
            settingsBtn.isEnabled = false
        }
    }

    private fun showAlert() {
        AlertDialog.Builder(this)
            .setTitle("Choose upper limit")
            .setView(dialog)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val editNum = dialog.findViewById<EditText>(R.id.upperLimitEditText)
                try {
                    limit = editNum.text.toString().toInt()
                } catch (_: Exception) {
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private val updateTimer: Runnable =
        object : Runnable {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun run() {

                val minutes = seconds % 3600 / 60
                val secs = seconds % 60

                if (running)
                    seconds++
                val time = "${if (seconds / 60 < 10) "0$minutes" else minutes}:" +
                        "${if (secs < 10) "0$secs" else secs}"

                timer.text = time

                if (limit != null && limit!! < seconds) {
                    timer.setTextColor(Color.RED)
                    if (limit!! + 1 == seconds && limit != 0)
                        createNotification()
                } else {
                    timer.setTextColor(Color.BLACK)
                }

                val color = Color.argb(
                    255,
                    (seconds * 10) % 200,
                    (seconds * 10) % 200,
                    (seconds * 10) % 200
                )
                bar.indeterminateTintList = getColors(color)

                if (running)
                    handler.postDelayed(this, 1000)

            }
        }

    fun getColors(color: Int = 0xFF8C5AFF.toInt()): ColorStateList {
        return ColorStateList(arrayOf(intArrayOf()), intArrayOf(color))
    }

    private fun createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "stopwatch"
            val channelDescription = "stopwatch description"
            val channelImportance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, channelImportance).apply {
                description = channelDescription
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            val notificationBuilder = NotificationCompat.Builder(applicationContext, channelId)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentTitle("Stopwatch")
                .setContentText("Time left")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)

            val notification = notificationBuilder.build()
            notification.flags = notification.flags or Notification.FLAG_INSISTENT
            notificationManager.notify(393939, notification)
        }
    }

    private fun init() {
        handler = Handler(Looper.getMainLooper())
        resetBtn = findViewById(R.id.resetButton)
        startBtn = findViewById(R.id.startButton)
        timer = findViewById(R.id.textView)
        bar = findViewById(R.id.progressBar)
        bar.visibility = View.INVISIBLE
        settingsBtn = findViewById(R.id.settingsButton)
        dialog = LayoutInflater.from(this).inflate(R.layout.alert_diaold, null, false)
    }

}

