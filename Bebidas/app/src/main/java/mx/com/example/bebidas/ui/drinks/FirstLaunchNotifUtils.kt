package mx.com.example.bebidas.ui.drinks

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mx.com.example.bebidas.R


internal object FirstLaunchNotifUtils {
    private const val CHANNEL_ID = "initLoad"
    private const val NOTIF_ID = 4876
    private const val PENDING_REQCODE = 0
    private var created = false
    private var updated = false
    private var channelRegistered = (Build.VERSION.SDK_INT < 26)

    val needNotifyLoadFinished: Boolean
        get() = created && !updated

    @SuppressLint("NewApi")
    private fun registerChannel(context: Context) {
        if(channelRegistered) return

        val notifMan = ContextCompat.getSystemService(context, NotificationManager::class.java)!!
        NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.initial_load_notifchannel_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = context.getString(R.string.initial_load_notifchannel_description)
        }.also {
            notifMan.createNotificationChannel(it)
        }

        channelRegistered = true
    }

    private suspend fun decodeImage(resources: Resources, imageID: Int)
        = withContext(Dispatchers.Default) {
            BitmapFactory.decodeResource(resources, imageID)
        }

    private fun getPendingIntent(activity: Activity)
        = TaskStackBuilder.create(activity)
        .addNextIntent( Intent(activity, activity.javaClass) )
        .getPendingIntent(PENDING_REQCODE, PendingIntent.FLAG_UPDATE_CURRENT)


    private suspend fun showNotification(activity: Activity, imageID: Int, messageID: Int)
        = NotificationCompat.Builder(activity, CHANNEL_ID)
            .setSmallIcon(imageID)
            .setLargeIcon( decodeImage(activity.resources, imageID) )
            .setContentTitle(activity.getString(R.string.hello))
            .setContentText(activity.getString(messageID))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setStyle(NotificationCompat.BigTextStyle())
            .setContentIntent(getPendingIntent(activity))
            .setAutoCancel(true)
            .build()
            .also { NotificationManagerCompat.from(activity).notify(NOTIF_ID, it) }

    suspend fun showLoading(activity: Activity) {
        if(created) return
        registerChannel(activity)
        showNotification(activity, R.drawable.ic_initial_load, R.string.initial_load_in_progress)
        created = true
    }

    suspend fun modifyLoadFinished(activity: Activity) {
        if(!this.needNotifyLoadFinished) return
        showNotification(activity, R.drawable.ic_initial_load_done, R.string.initial_load_done)
        updated = true
    }
}