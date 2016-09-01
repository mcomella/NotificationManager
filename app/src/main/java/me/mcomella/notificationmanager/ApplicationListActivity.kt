package me.mcomella.notificationmanager

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
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

import kotlinx.android.synthetic.main.activity_application_list.*

class ApplicationListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_application_list)
        attachClickListeners()
    }

    private fun attachClickListeners() {
        notification_settings_button.setOnClickListener {
            startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
        }
    }

    override fun onStart() {
        super.onStart()

        application_list.adapter = ApplicationListAdapter(this)
        application_list.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        application_list.layoutManager = layoutManager
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

private class ApplicationListAdapter(context: Context) : RecyclerView.Adapter<ApplicationListViewHolder>() {

    val diskManager = DiskManager(context)

    // TODO: make temp vars not properties
    // TODO: lazy? (especially icons)
    // TODO: filter on system pkgs? http://stackoverflow.com/a/8483920? enabled apps?
    private val _installedApps = context.packageManager.getInstalledApplications(0)
    private val _appsAndStateOnDisk = diskManager.readApplicationsFromDisk()
    val installedAppInfo = _installedApps.map {
        val pkgManager = context.packageManager
        AppInfo(pkgName = it.packageName,
                label = it.loadLabel(pkgManager).toString(),
                icon = it.loadIcon(pkgManager),
                isChecked = _appsAndStateOnDisk.getOrElse(it.packageName, { true }))
    }.sortedBy { it.label }

    override fun getItemCount(): Int {
        return installedAppInfo.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ApplicationListViewHolder {
        val view = LayoutInflater.
                from(parent!!.context).
                inflate(R.layout.application_item, parent, false)
        return ApplicationListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ApplicationListViewHolder, position: Int) {
        val app = installedAppInfo[position]
        holder.title.text = app.label
        holder.icon.setImageDrawable(app.icon)

        holder.toggle.isChecked = app.isChecked
        holder.toggle.setOnCheckedChangeListener { buttonView, isChecked ->
            // Thread-safe: only updated from UI thread.
            app.isChecked = isChecked
            diskManager.saveApplicationsToDisk(installedAppInfo.associateBy({it.pkgName}, {it.isChecked})) // TODO: converting each time is slow.
        }
    }

    // TODO: This name collides with ApplicationInfo.
    private data class AppInfo(val pkgName: String, val label: String, val icon: Drawable, var isChecked: Boolean)
}
