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
    companion object Keys {
        val KEY_PKG_NAME = "title"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_app)
        supportActionBar!!.setTitle("Add application to your list")
        initAppList()
    }

    private fun initAppList() {
        appList.setHasFixedSize(true)
        appList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        appList.adapter = AppListAdapter(this)
    }
}

private class AppListAdapter(context: Context) : RecyclerView.Adapter<AppListAdapter.ViewHolder>() {
    private val packageManager = context.packageManager
    val apps = packageManager.getInstalledApplications(0).sortedBy {
        it.loadLabel(packageManager).toString() // TODO: we can re-use this later
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.add_app_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = apps[position]
        holder.iconView.setImageDrawable(app.loadIcon(packageManager))
        holder.titleView.text = app.loadLabel(packageManager)
    }

    override fun getItemCount(): Int = apps.size

    private class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView = view.findViewById(R.id.title) as TextView
        val iconView = view.findViewById(R.id.icon) as ImageView
    }
}
