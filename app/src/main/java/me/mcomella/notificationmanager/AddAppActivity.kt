package me.mcomella.notificationmanager

import android.app.Activity
import android.content.Context
import android.content.Intent
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
        val KEY_USER_CONTEXT_INDEX = "userContextIndex"
    }

    val onItemClickListener = { pkgName: String -> finishAndSetResult(pkgName) }
    var userContextIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_app)
        supportActionBar!!.setTitle("Add application to your list")
        initAppList()

        userContextIndex = intent.getIntExtra(KEY_USER_CONTEXT_INDEX, -2)
    }

    private fun initAppList() {
        appList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        appList.adapter = AddAppListAdapter(this, onItemClickListener)
        appList.adapter.notifyDataSetChanged()
    }

    private fun finishAndSetResult(pkgName: String) {
        val bundle = Bundle()
        bundle.putString(KEY_PKG_NAME, pkgName)
        bundle.putInt(KEY_USER_CONTEXT_INDEX, userContextIndex)
        val res = Intent()
        res.putExtra(ContextSelectorActivity.KEY_BUNDLE, bundle)
        setResult(Activity.RESULT_OK, res)

        finish()
    }
}

private class AddAppListAdapter(context: Context, val onClickListener: (String) -> Unit) :
        RecyclerView.Adapter<AddAppListAdapter.ViewHolder>() {
    private val packageManager = context.packageManager
    val apps: List<ApplicationInfo> = packageManager.getInstalledApplications(0).sortedBy {
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
        holder.rootView.setOnClickListener { onClickListener(app.packageName) }
    }

    override fun getItemCount(): Int = apps.size

    private class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rootView = view
        val titleView = view.findViewById(R.id.title) as TextView
        val iconView = view.findViewById(R.id.icon) as ImageView
    }
}
