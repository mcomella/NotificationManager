package me.mcomella.notificationmanager

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView

import kotlinx.android.synthetic.main.activity_blocked_list.*

class BlockedListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blocked_list)
        attachClickListeners()
    }

    private fun attachClickListeners() {
        notification_settings_button.setOnClickListener {
            startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
        }
    }

    override fun onStart() {
        super.onStart()

        blockedList.adapter = BlockedListAdapter(this)
        blockedList.setHasFixedSize(true)
        blockedList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        startNotificationService()
    }

    private fun startNotificationService() {
        // TODO: PERMISSIONS?
        val intent = Intent(this, NotificationService::class.java)
        startService(intent) // TODO: start service on device startup too.
    }
}

private class ApplicationListViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
    val title = itemView!!.findViewById(R.id.title) as TextView
    val icon = itemView!!.findViewById(R.id.icon) as ImageView
    val toggle = itemView!!.findViewById(R.id.toggle) as Switch
}

private class BlockedListAdapter(context: Context) : RecyclerView.Adapter<ApplicationListViewHolder>() {

    val pkgManager = context.packageManager
    val diskManager = DiskManager(context)
    val apps = diskManager.readAppsFromDisk().sortedBy {
        pkgManager.getApplicationInfo(it.pkgName, 0).loadLabel(pkgManager).toString()
    }

    override fun getItemCount(): Int {
        return apps.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ApplicationListViewHolder {
        val view = LayoutInflater.
                from(parent!!.context).
                inflate(R.layout.application_item, parent, false)
        return ApplicationListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ApplicationListViewHolder, position: Int) {
        val app = apps[position]
        val appInfo = pkgManager.getApplicationInfo(app.pkgName, 0)

        holder.title.text = appInfo.loadLabel(pkgManager)
        holder.icon.setImageDrawable(appInfo.loadIcon(pkgManager))

        holder.toggle.isChecked = app.isChecked
        holder.toggle.setOnCheckedChangeListener { buttonView, isChecked ->
            // Thread-safe: only updated from UI thread.
            val mutableApps = apps.toMutableList()
            mutableApps[position] = BlockedAppInfo(app.pkgName, isChecked)
            diskManager.saveAppsToDisk(mutableApps)
        }
    }
}
