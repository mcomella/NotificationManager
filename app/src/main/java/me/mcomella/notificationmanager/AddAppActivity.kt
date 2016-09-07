package me.mcomella.notificationmanager

import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_add_app.*

class AddAppActivity : AppCompatActivity() {
    companion object {
        // args
        val KEY_BLOCKED_APPS = "blockedApps"

        // return values
        val KEY_ADDED_APP = "addedApp"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_app)
        supportActionBar!!.setTitle("Add application")
        initAppList()
    }

    private fun initAppList() {
        val installedApps = packageManager.getInstalledApplications(0).map { it.packageName }
        val alreadyBlockedApps = intent.getStringArrayListExtra(KEY_BLOCKED_APPS).toSet()
        val sortedAppsToShow = (installedApps - alreadyBlockedApps).toList().map {
            packageManager.getApplicationInfo(it, 0)
        }.sortedBy { it.loadLabel(packageManager).toString() }

        appList.adapter = AddAppAdapter(this, sortedAppsToShow)
        appList.setHasFixedSize(true)
        appList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }
}

private class AddAppAdapter(context: Context, val apps: List<ApplicationInfo>) : RecyclerView.Adapter<AddAppAdapter.AddAppViewHolder>() {
    private val pkgManager = context.packageManager

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): AddAppViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.add_app_item, parent, false)
        return AddAppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddAppViewHolder, position: Int) {
        val appInfo = apps[position]

        holder.titleView.text = appInfo.loadLabel(pkgManager)
        holder.iconView.setImageDrawable(appInfo.loadIcon(pkgManager))
    }

    override fun getItemCount(): Int = apps.size

    private class AddAppViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView = view.findViewById(R.id.title) as TextView
        val iconView = view.findViewById(R.id.icon) as ImageView
    }
}