package me.mcomella.notificationmanager.missednotify

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_missed_notifications_list.*
import me.mcomella.notificationmanager.R

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
}

private class MissedNotificationsAdapter(context: Context) :
        RecyclerView.Adapter<MissedNotificationsAdapter.MissedNotificationsViewHolder>() {

    private val pkgManager = context.packageManager
    val notifications = MissedNotificationsDiskManager(context).readNotificationsFromDisk() // TODO: sort

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MissedNotificationsViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.missed_notification_list_item, parent, false)
        return MissedNotificationsViewHolder(view)
    }

    override fun onBindViewHolder(holder: MissedNotificationsViewHolder, position: Int) {
        val notification = notifications[position]
        val appInfo = pkgManager.getApplicationInfo(notification.pkgname, 0)
        val appTitle = appInfo.loadLabel(pkgManager)
        val appIcon = appInfo.loadIcon(pkgManager) // TODO: NOTIFICATION icons aren't necessarily app icons

        holder.titleView.text = appTitle
        holder.subtitleView.text = notification.title
        holder.iconView.setImageDrawable(appIcon)
    }

    override fun getItemCount() = notifications.size

    private class MissedNotificationsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iconView = view.findViewById(R.id.iconView) as ImageView
        val titleView = view.findViewById(R.id.titleView) as TextView
        val subtitleView = view.findViewById(R.id.subtitleView) as TextView
    }
}