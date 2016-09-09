package me.mcomella.notificationmanager

import android.app.Notification
import android.content.Intent
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import me.mcomella.notificationmanager.missednotify.MissedNotification
import me.mcomella.notificationmanager.missednotify.MissedNotificationsDiskManager

class NotificationService : NotificationListenerService() {
    private val FOREGROUND_ID = 4321

    private val foregroundNotification: Notification
        get() = Notification.Builder(this).
                setContentTitle("Notification Manager").
                setContentText("Listening and blocking specified notifications..."). // TODO: doesn't appear
                build()

    private val diskManager: DiskManager
        get() = DiskManager(this) // TODO: fragile. underlying filesDir is null at init time but not when accessed later.
    private val appsAndState: Map<String, Boolean> // TODO: getting from disk each time inefficient. send refresh signals.
        get() = diskManager.readAppsFromDisk().associateBy({it.pkgname }, {it.checked })

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        startForeground(FOREGROUND_ID, foregroundNotification)
        return super.onStartCommand(intent, flags, startId) // TODO: sticky?
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        Log.d(TAG, "onNotificationPosted: $sbn")
        if (appsAndState.getOrElse(sbn.packageName, {false})) {
            Log.d(TAG, "Cancelling notification for package: ${sbn.packageName}")
            cancelNotification(sbn.key)
            saveNotificationToDisk(sbn)
        }
    }

    private fun saveNotificationToDisk(sbn: StatusBarNotification) {
        val notification = sbn.notification
        val extras = notification.extras

        val missedNotification = MissedNotification(pkgname = sbn.packageName,
                posttime = sbn.postTime,
                title = extras.getString(Notification.EXTRA_TITLE, "TITLE"),
                contenttext = extras.getString(Notification.EXTRA_TEXT, "CONTENT TEXT"))

        MissedNotificationsDiskManager(this).prependNotificationToDisk(missedNotification)
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.d(TAG, "onBind")
        return super.onBind(intent)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d(TAG, "listenerConnected")
    }
}
