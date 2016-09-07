package me.mcomella.notificationmanager

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView

import kotlinx.android.synthetic.main.activity_blocked_list.*
import me.mcomella.notificationmanager.ext.use
import java.lang.ref.WeakReference
import java.util.*

class BlockedListActivity : AppCompatActivity() {
    companion object {
        val KEY_BUNDLE = "bundle"

        val REQ_CODE_ADD_APP = 1999
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blocked_list)
        attachClickListeners()
    }

    private fun attachClickListeners() {
        addAppButton.setOnClickListener {
            val blockedApps = (blockedList.adapter as BlockedListAdapter).apps.map { it.pkgname }
            val intent = Intent(this, AddAppActivity::class.java)
            intent.putExtra(AddAppActivity.KEY_BLOCKED_APPS, ArrayList(blockedApps))
            startActivityForResult(intent, REQ_CODE_ADD_APP)
        }
    }

    override fun onStart() {
        super.onStart()

        blockedList.adapter = BlockedListAdapter(this)
        blockedList.setHasFixedSize(true)
        blockedList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        startNotificationService()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return

        val innerRes = if (data == null) Bundle() else data.getBundleExtra(KEY_BUNDLE)
        when (requestCode) {
            REQ_CODE_ADD_APP -> handleAddApp(innerRes)
        }
    }

    private fun handleAddApp(res: Bundle) {
        val addedApp = res.getString(AddAppActivity.KEY_ADDED_APP)

        val diskManager = DiskManager(this)
        val blockedApps = diskManager.readAppsFromDisk().toMutableList()

        diskManager.saveAppsToDisk(blockedApps + BlockedAppInfo(addedApp, true))
        refreshAdapter()
    }

    private fun handleRemoveApp(appToRemove: BlockedAppInfo) {
        val diskManager = DiskManager(this)
        val appsToSave = diskManager.readAppsFromDisk().filter { it != appToRemove }
        diskManager.saveAppsToDisk(appsToSave)
        refreshAdapter()
    }

    private fun refreshAdapter() {
        blockedList.adapter = BlockedListAdapter(this)
    }

    private fun startNotificationService() {
        // TODO: PERMISSIONS?
        val intent = Intent(this, NotificationService::class.java)
        startService(intent) // TODO: start service on device startup too.
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.blocked_list_context_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val blockedApp = (blockedList.adapter as BlockedListAdapter).getLongClickedApp()
        return when (item.itemId) {
            R.id.removeApp -> {
                handleRemoveApp(blockedApp)
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }
}

private class BlockedListAdapter(activity: Activity) : RecyclerView.Adapter<BlockedListAdapter.ApplicationListViewHolder>() {
    val activityWeakReference = WeakReference(activity)

    val pkgManager = activity.packageManager
    val diskManager = DiskManager(activity)
    val apps = diskManager.readAppsFromDisk().sortedBy {
        pkgManager.getApplicationInfo(it.pkgname, 0).loadLabel(pkgManager).toString()
    }.toMutableList()

    private var longClickPosition: Int = -1

    override fun getItemCount(): Int {
        return apps.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ApplicationListViewHolder {
        val view = LayoutInflater.
                from(parent!!.context).
                inflate(R.layout.blocked_list_item, parent, false)
        return ApplicationListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ApplicationListViewHolder, position: Int) {
        val app = apps[position]
        val appInfo = pkgManager.getApplicationInfo(app.pkgname, 0)

        holder.title.text = appInfo.loadLabel(pkgManager)
        holder.icon.setImageDrawable(appInfo.loadIcon(pkgManager))

        holder.toggle.isChecked = app.checked
        holder.toggle.setOnCheckedChangeListener { buttonView, isChecked ->
            // Thread-safe: only updated from UI thread.
            // Since toggle updates automatically, I don't think we need notifyDatasetChanged
            apps[position] = BlockedAppInfo(app.pkgname, isChecked) // Since toggle updates
            diskManager.saveAppsToDisk(apps)
        }
        holder.rootView.setOnClickListener { holder.toggle.toggle() }
        holder.rootView.setOnLongClickListener {
            longClickPosition = position
            false
        }
        registerForContextMenu(holder.rootView)
    }

    private fun registerForContextMenu(view: View) {
        activityWeakReference.use {
            it.registerForContextMenu(view)
        }
    }

    fun getLongClickedApp(): BlockedAppInfo = apps[longClickPosition]

    private class ApplicationListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rootView = itemView
        val title = itemView!!.findViewById(R.id.title) as TextView
        val icon = itemView!!.findViewById(R.id.icon) as ImageView
        val toggle = itemView!!.findViewById(R.id.toggle) as Switch
    }
}
