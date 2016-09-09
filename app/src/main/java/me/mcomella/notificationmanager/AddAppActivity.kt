package me.mcomella.notificationmanager

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.AsyncTask
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
import me.mcomella.notificationmanager.ext.use
import java.lang.ref.WeakReference
import java.util.*

class AddAppActivity : AppCompatActivity() {
    companion object {
        // args
        val KEY_BLOCKED_APPS = "blockedApps"

        // return values
        val KEY_ADDED_APP = "addedApp"
    }

    var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_app)
        supportActionBar!!.setTitle("Add application")

        appList.setHasFixedSize(true)
        appList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    override fun onStart() {
        super.onStart()
        LoadAppsAsyncTask(this, appList).execute(intent.getStringArrayListExtra(KEY_BLOCKED_APPS))

        val progressDialog = ProgressDialog(this)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setMessage("Loading installed apps...")
        progressDialog.show()
        this.progressDialog = progressDialog
    }
}

private class LoadAppsAsyncTask(activity: AddAppActivity, appList: RecyclerView) :
        AsyncTask<List<String>, Void, List<ApplicationInfo>>() {

    val activityWeakReference = WeakReference(activity)
    val appListWeakReference = WeakReference(appList)

    override fun doInBackground(vararg params: List<String>): List<ApplicationInfo> {
        var installedSortedApps: List<ApplicationInfo> = listOf()
        activityWeakReference.use { activity ->
            val packageManager = activity.packageManager
            val alreadyBlockedApps = params[0]
            installedSortedApps = packageManager.getInstalledApplications(0).filter {
                !alreadyBlockedApps.contains(it.packageName)
            }.sortedBy { it.loadLabel(packageManager).toString() } // TODO: sorting is slow.
        }
        return installedSortedApps
    }

    override fun onPostExecute(result: List<ApplicationInfo>?) {
        super.onPostExecute(result)

        appListWeakReference.use { appList ->
            activityWeakReference.use { activity ->
                activity.progressDialog?.cancel()
                appList.adapter = AddAppAdapter(activity, result!!, { pkgName: String ->
                    val ret = Bundle()
                    ret.putString(AddAppActivity.KEY_ADDED_APP, pkgName)
                    val outerRet = Intent()
                    outerRet.putExtra(BlockedListActivity.KEY_BUNDLE, ret)
                    activity.setResult(Activity.RESULT_OK, outerRet)

                    activity.finish()
                })
            }
        }
    }
}

private class AddAppAdapter(context: Context, val apps: List<ApplicationInfo>, val onClickListener: (String) -> Unit) :
        RecyclerView.Adapter<AddAppAdapter.AddAppViewHolder>() {
    private val pkgManager = context.packageManager

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): AddAppViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.add_app_item, parent, false)
        return AddAppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddAppViewHolder, position: Int) {
        val appInfo = apps[position]

        holder.titleView.text = appInfo.loadLabel(pkgManager)
        holder.iconView.setImageDrawable(appInfo.loadIcon(pkgManager))
        holder.rootView.setOnClickListener { onClickListener(appInfo.packageName) }
    }

    override fun getItemCount(): Int = apps.size

    private class AddAppViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rootView = view
        val titleView = view.findViewById(R.id.title) as TextView
        val iconView = view.findViewById(R.id.icon) as ImageView
    }
}