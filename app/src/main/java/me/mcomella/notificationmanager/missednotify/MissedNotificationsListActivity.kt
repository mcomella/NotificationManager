package me.mcomella.notificationmanager.missednotify

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_missed_notifications_list.*
import me.mcomella.notificationmanager.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MissedNotificationsListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_missed_notifications_list)
        supportActionBar!!.title = "Blocked"
    }

    override fun onStart() {
        super.onStart()
        initMissedNotificationsList()
    }

    private fun initMissedNotificationsList() {
        missedNotificationsList.adapter = MissedNotificationsAdapter(this)
        missedNotificationsList.setHasFixedSize(true)
        missedNotificationsList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.activity_missed_notifications_list_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.clearNotifications -> {
                clearNotifications()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun clearNotifications() {
        val diskManager = MissedNotificationsDiskManager(this)
        diskManager.clearAllNotifications()
        missedNotificationsList.adapter = MissedNotificationsAdapter(this)
        finish()
    }
}

private class MissedNotificationsAdapter(context: Context) :
        RecyclerView.Adapter<MissedNotificationsAdapter.MissedNotificationsViewHolder>() {

    private val pkgManager = context.packageManager
    val notifications = MissedNotificationsDiskManager(context).readNotificationsFromDisk().sortedByDescending { it.posttime }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MissedNotificationsViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.missed_notification_list_item, parent, false)
        return MissedNotificationsViewHolder(view)
    }

    override fun onBindViewHolder(holder: MissedNotificationsViewHolder, position: Int) {
        val notification = notifications[position]
        val appInfo = pkgManager.getApplicationInfo(notification.pkgname, 0)
        val appIcon = appInfo.loadIcon(pkgManager)

        holder.titleView.text = notification.title
        holder.subtitleView.text = notification.contenttext
        holder.iconView.setImageDrawable(appIcon)
        holder.postTimeView.text = convertMillisToDate(notification.posttime)
    }

    private fun convertMillisToDate(millis: Long): String {
        val date = Date(millis)
        val dateFormat = SimpleDateFormat("h:mm a", Locale.US)
        return dateFormat.format(date)
    }

    override fun getItemCount() = notifications.size

    private class MissedNotificationsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iconView = view.findViewById(R.id.iconView) as ImageView
        val titleView = view.findViewById(R.id.titleView) as TextView
        val subtitleView = view.findViewById(R.id.subtitleView) as TextView
        val postTimeView = view.findViewById(R.id.postTimeView) as TextView
    }
}