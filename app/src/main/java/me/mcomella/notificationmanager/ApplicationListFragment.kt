package me.mcomella.notificationmanager

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView

class ApplicationListFragment() : Fragment() {
    var applicationList: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_application_list, container, false)
        initApplicationList(rootView)
        return rootView
    }

    private fun initApplicationList(rootView: View) {
        val tmpApplicationList = rootView.findViewById(R.id.applicationList) as RecyclerView
        tmpApplicationList.adapter = ApplicationListAdapter(context)
        tmpApplicationList.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        tmpApplicationList.layoutManager = layoutManager

        applicationList = tmpApplicationList // TODO: useful?
    }
}

private class ApplicationListAdapter(context: Context) : RecyclerView.Adapter<ApplicationListAdapter.ViewHolder>() {

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

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.
                from(parent!!.context).
                inflate(R.layout.application_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
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

    private class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val title = itemView!!.findViewById(R.id.title) as TextView
        val icon = itemView!!.findViewById(R.id.icon) as ImageView
        val toggle = itemView!!.findViewById(R.id.toggle) as Switch
    }
}
