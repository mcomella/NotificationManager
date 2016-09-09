package me.mcomella.notificationmanager

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.v7.view.menu.MenuBuilder
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView

import kotlinx.android.synthetic.main.activity_blocked_list.*
import kotlinx.android.synthetic.main.blocked_list_item.*
import me.mcomella.notificationmanager.ext.use
import me.mcomella.notificationmanager.missednotify.MissedNotificationsDiskManager
import me.mcomella.notificationmanager.missednotify.MissedNotificationsListActivity
import java.lang.ref.WeakReference
import java.util.*

val TAG = "NTFY"

private val DISABLED_ITEM_ALPHA = 0.50f

class BlockedListActivity : AppCompatActivity() {
    companion object {
        val KEY_BUNDLE = "bundle"

        val REQ_CODE_ADD_APP = 1999
    }

    /** Manages empty state. Pretty shitty - this setter should be on the list of apps data structure. */
    var isEmpty = false
        set(isEmpty: Boolean) {
            if (isEmpty) {
                blockedList.visibility = View.GONE
                emptyState.visibility = View.VISIBLE
            } else {
                blockedList.visibility = View.VISIBLE
                emptyState.visibility = View.GONE
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blocked_list)
        attachClickListeners()
        initToolbar()
    }

    private fun attachClickListeners() {
        addAppButton.setOnClickListener {
            val blockedApps = (blockedList.adapter as BlockedListAdapter).apps.map { it.pkgname }
            val intent = Intent(this, AddAppActivity::class.java)
            intent.putExtra(AddAppActivity.KEY_BLOCKED_APPS, ArrayList(blockedApps))
            startActivityForResult(intent, REQ_CODE_ADD_APP)
        }

        permissionsSettingsButton.setOnClickListener {
            startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
        }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        toolbar.title = "NTFY"
    }

    private fun isNotificationListenerPermissionGranted(): Boolean {
        val grantedPkgs = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        return grantedPkgs != null &&
                grantedPkgs.contains(packageName)
    }

    private fun updatePermissionAndEmptyStatePrompt() {
        if (isNotificationListenerPermissionGranted()) {
            emptyStateTextView.text = "Add apps you want to hide from!"
            permissionsSettingsButton.visibility = View.GONE
            emptyStateSubtitleView.visibility = View.GONE
            addAppButton.visibility = View.VISIBLE
            isEmpty = DiskManager(this).readAppsFromDisk().isEmpty()
        } else {
            emptyStateTextView.text = "Access Notifications"
            permissionsSettingsButton.visibility = View.VISIBLE
            emptyStateSubtitleView.visibility = View.VISIBLE
            addAppButton.visibility = View.GONE
            isEmpty = true // Not actually empty, but this hack works.
        }
    }

    private fun updateToolbarMenu(menu: Menu?) {
        if (menu == null) return

        menu.clear()
        val blockedNotificationCount = MissedNotificationsDiskManager(this).readNotificationsFromDisk().size
        if (blockedNotificationCount > 0) {
            val item = menu.add(0, R.id.showMissedNotifications, 0, "Show missed notifications")
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            item.icon = createMissedNotificationCounter(blockedNotificationCount)
        }
    }

    override fun onStart() {
        super.onStart()

        updatePermissionAndEmptyStatePrompt()
        updateToolbarMenu(toolbar.menu)

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
        isEmpty = false
    }

    private fun handleRemoveApp(appToRemove: BlockedAppInfo) {
        val diskManager = DiskManager(this)
        val appsToSave = diskManager.readAppsFromDisk().filter { it != appToRemove }
        diskManager.saveAppsToDisk(appsToSave)
        refreshAdapter()
        isEmpty = appsToSave.isEmpty()
    }

    private fun refreshAdapter() {
        blockedList.adapter = BlockedListAdapter(this)
    }

    private fun startNotificationService() {
        // TODO: PERMISSIONS?
        val intent = Intent(this, NotificationService::class.java)
        startService(intent) // TODO: start service on device startup too.
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // create content in onPrepare
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        updateToolbarMenu(menu)
        return true
    }

    private fun createMissedNotificationCounter(notificationCount: Int): Drawable {
        // http://stackoverflow.com/a/9033538
        val dimen = resources.getDimensionPixelSize(R.dimen.toolbar_icon_size)
        val bm = Bitmap.createBitmap(dimen, dimen, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bm)
        val colorAccentPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        colorAccentPaint.color = resources.getColor(R.color.colorAccent, null)

        canvas.drawCircle(canvas.width / 2f, canvas.height / 2f, canvas.width / 2f, colorAccentPaint)

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        textPaint.color = resources.getColor(R.color.colorPrimary, null)
        textPaint.textSize = resources.getDimensionPixelSize(R.dimen.toolbar_text_size).toFloat()
        // positions: http://stackoverflow.com/a/11121873
        val cntStr = notificationCount.toString()
        val xPos = canvas.width / 2f - (if (cntStr.length > 1) 2.5f else 1f) * resources.getDimensionPixelSize(R.dimen.toolbar_text_offset)
        val yPos = canvas.height / 2f - (textPaint.ascent() - textPaint.descent()) / 2f - resources.getDimensionPixelSize(R.dimen.toolbar_text_offset)
        canvas.drawText(cntStr, xPos, yPos, textPaint)
        return BitmapDrawable(resources, bm)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.showMissedNotifications -> {
                val intent = Intent(this, MissedNotificationsListActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
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

        setForCheckedState(holder, app.checked)
        holder.toggle.isChecked = app.checked
        holder.toggle.setOnCheckedChangeListener { buttonView, isChecked ->
            // Thread-safe: only updated from UI thread.
            // Since toggle updates automatically, I don't think we need notifyDatasetChanged
            setForCheckedState(holder, isChecked)
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

    private fun setForCheckedState(holder: ApplicationListViewHolder, isChecked: Boolean) {
        holder.subtitleView.visibility = if (isChecked) View.VISIBLE else View.GONE
        holder.rootView.alpha = if (isChecked) 1f else DISABLED_ITEM_ALPHA
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
        val subtitleView = itemView.findViewById(R.id.subtitleView)
        val icon = itemView!!.findViewById(R.id.icon) as ImageView
        val toggle = itemView!!.findViewById(R.id.toggle) as Switch
    }
}
