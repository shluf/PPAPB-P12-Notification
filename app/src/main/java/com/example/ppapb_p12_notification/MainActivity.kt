package com.example.ppapb_p12_notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ppapb_p12_notification.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val CHANNEL_ID = "my_channel"
    private val notificationId = 1
    private val KEY_COMMENT = "key_comment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createNotificationChannel()

        binding.btnShowNotification.setOnClickListener {
            showNotification()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "My Channel"
            val descriptionText = "Channel Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification() {
        val likeIntent = Intent(this, ResultActivity::class.java).apply {
            action = "LIKE_ACTION"
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val likePendingIntent = PendingIntent.getActivity(
            this, 0, likeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        val dislikeIntent = Intent(this, ResultActivity::class.java).apply {
            action = "DISLIKE_ACTION"
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val dislikePendingIntent = PendingIntent.getActivity(
            this, 1, dislikeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        val remoteInput = RemoteInput.Builder(KEY_COMMENT)
            .setLabel("Tulis komentar")
            .build()

        val commentIntent = Intent(this, ResultActivity::class.java).apply {
            action = "COMMENT_ACTION"
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val commentPendingIntent = PendingIntent.getActivity(
            this, System.currentTimeMillis().toInt(), commentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        val commentAction = NotificationCompat.Action.Builder(
            R.drawable.ic_comment,
            "Komentar",
            commentPendingIntent
        ).addRemoteInput(remoteInput).build()


        val notifImage = BitmapFactory.decodeResource(resources,
            R.drawable.first_img)
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_vote)
            .setLargeIcon(BitmapFactory.decodeResource(resources, android.R.drawable.ic_menu_compass))
            .setContentTitle("Vote dulu ges")
            .setContentText("Nohh gambarnya gimana??")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(notifImage)
            )
            .setAutoCancel(false)
            .addAction(android.R.drawable.ic_menu_share, "Like", likePendingIntent)
            .addAction(android.R.drawable.ic_menu_share, "Dislike", dislikePendingIntent)
            .addAction(commentAction)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }
}