package me.mcomella.notificationmanager

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

import kotlinx.android.synthetic.main.activity_application_list.*

val REQ_CODE_ADD_LIST = 1999
val REQ_CODE_ADD_APP = 2000

class ContextSelectorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_application_list)
        attachClickListeners()
    }

    private fun attachClickListeners() {
        /* TODO: fix permissions again.
        notification_settings_button.setOnClickListener {
            startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
        }
        */
        fab.setOnClickListener {
            showAddToListDialog()
        }
    }

    // via https://developer.android.com/reference/android/app/DialogFragment.html
    private val DIALOG_TAG = "dialog"
    private fun showAddToListDialog() {
        val ft = supportFragmentManager!!.beginTransaction()
        val prev = supportFragmentManager!!.findFragmentByTag(DIALOG_TAG)
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)

        val newFrag = AddToListDialogFragment.newInstance()

        newFrag.onAddListClickListener = {
            val intent = Intent(this, AddListActivity::class.java)
            startActivityForResult(intent, REQ_CODE_ADD_LIST)
        }
        newFrag.onAddAppClickListener = {
            val intent = Intent(this, AddListActivity::class.java) // todo
            startActivityForResult(intent, REQ_CODE_ADD_APP)
        }

        newFrag.show(ft, DIALOG_TAG)
    }

    override fun onStart() {
        super.onStart()

        tabsLayout.tabMode = TabLayout.MODE_SCROLLABLE
        contentPager.adapter = UserContextAdapter(this, supportFragmentManager)
        tabsLayout.setupWithViewPager(contentPager)

        startNotificationService()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val innerRes = if (data != null) data.getBundleExtra(KEY_BUNDLE) else Bundle()
        when (requestCode) {
            REQ_CODE_ADD_LIST -> {
                if (data == null) throw IllegalArgumentException("Data unexpectedly null")
                handleAddList(innerRes)
            }
            //else -> throw IllegalArgumentException("Unknown request code: $requestCode") // TODO: this is fragile, but may prevent unexpected behavior.
        }
    }

    private fun handleAddList(result: Bundle) {
        val listTitle = result.getString(AddListActivity.KEY_LIST_TITLE)

        // TODO: terribly inefficient.
        val diskManager = DiskManager(this)
        val userContexts = diskManager.readUserContextsFromDisk()
        val modified = userContexts + UserContext(name = listTitle,
                                                  apps = listOf<String>())
        diskManager.saveUserContextsToDisk(modified)

        contentPager.adapter = UserContextAdapter(this, supportFragmentManager) // refreshes data.
    }

    private fun startNotificationService() {
        val intent = Intent(this, NotificationService::class.java)
        startService(intent) // TODO: start service on device startup too.
    }

    companion object Keys {
        val KEY_BUNDLE = "bundle"
    }
}

private class UserContextAdapter(context: Context, fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
    val diskManager = DiskManager(context)
    val userContexts = diskManager.readUserContextsFromDisk()

    override fun getCount(): Int {
        return userContexts.size
    }

    override fun getItem(position: Int): Fragment {
        return ApplicationListFragment.newInstance(userContexts[position].apps)
    }

    override fun getPageTitle(position: Int): CharSequence {
        return userContexts[position].name
    }
}
