package me.mcomella.notificationmanager

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import kotlinx.android.synthetic.main.activity_application_list.*

class ApplicationListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_application_list)

        application_list.adapter = ApplicationListAdapter(this)
        application_list.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        application_list.layoutManager = layoutManager
    }
}

private class ApplicationListViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
    val title = itemView!!.findViewById(R.id.title) as TextView
    val icon = itemView!!.findViewById(R.id.icon) as ImageView
}

private class ApplicationListAdapter(context: Context) : RecyclerView.Adapter<ApplicationListViewHolder>() {
    // TODO: metadata; lazy?; when to update?; filter on system pkg http://stackoverflow.com/a/8483920?; filter enabled?
    val installedApplications = context.packageManager.getInstalledApplications(0).map {
        val pkgManager = context.packageManager
        AppInfo(it.packageName, it.loadLabel(pkgManager).toString(), it.loadIcon(pkgManager))
    }.sortedBy { it.label }

    override fun getItemCount(): Int {
        return installedApplications.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ApplicationListViewHolder {
        val view = LayoutInflater.
                from(parent!!.context).
                inflate(R.layout.application_item, parent, false)
        return ApplicationListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ApplicationListViewHolder, position: Int) {
        val app = installedApplications[position]
        holder.title.text = app.label
        holder.icon.setImageDrawable(app.icon)
    }

    private data class AppInfo(val pkgName: String, val label: String, val icon: Drawable)

}