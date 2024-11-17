package com.example.ppapb_p12_notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import com.example.ppapb_p12_notification.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private var likeCount = 0
    private var dislikeCount = 0
    private val comments = mutableListOf<String>()

    companion object {
        const val KEY_COMMENT = "key_comment"
        private var instance: ResultActivity? = null
        private const val CHANNEL_ID = "my_channel"
        private const val NOTIFICATION_ID = 1

        fun getInstance(): ResultActivity? {
            return instance
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        instance = this
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        when (intent?.action) {
            "LIKE_ACTION" -> {
                likeCount++
                updateUI()
            }
            "DISLIKE_ACTION" -> {
                dislikeCount++
                updateUI()
            }
            "COMMENT_ACTION" -> {
                val remoteInput = RemoteInput.getResultsFromIntent(intent)
                remoteInput?.let {
                    val comment = it.getCharSequence(KEY_COMMENT)?.toString()
                    comment?.let { text ->
                        if (text.isNotBlank()) {
                            comments.add(text)
                            updateUI()
                            updateNotification()
                        }
                    }
                }
            }
        }
    }

    private fun updateNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


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
            R.drawable.img)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_vote)
            .setLargeIcon(BitmapFactory.decodeResource(resources, android.R.drawable.ic_menu_compass))
            .setContentTitle("Vote lagi gess")
            .setContentText("Kasi like lagi yg banyak!!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(notifImage)
            )
            .setAutoCancel(false)
            .addAction(android.R.drawable.ic_menu_share, "Like", likePendingIntent)
            .addAction(android.R.drawable.ic_menu_share, "Dislike", dislikePendingIntent)
            .addAction(commentAction)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun updateUI() {
        binding.apply {
            tvLikeCount.text = "$likeCount"
            tvDislikeCount.text = "$dislikeCount"
            tvComments.text = buildCommentsText()
        }
    }

    private fun buildCommentsText(): String {
        val sb = StringBuilder()
        comments.forEachIndexed { index, comment ->
            if (index%2!=0) {
                sb.append("Fans said \"$comment\"\n")
            } else {
                sb.append("Haters said \"$comment\"\n")
            }
        }
        return sb.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            instance = null
        }
    }
}