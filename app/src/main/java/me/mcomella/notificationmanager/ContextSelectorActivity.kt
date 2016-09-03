package me.mcomella.notificationmanager

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

import kotlinx.android.synthetic.main.activity_application_list.*

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
    }

    override fun onStart() {
        super.onStart()

        tabsLayout.tabMode = TabLayout.MODE_SCROLLABLE
        contentPager.adapter = UserContextAdapter(supportFragmentManager)
        tabsLayout.setupWithViewPager(contentPager)

        startNotificationService()
    }

    private fun startNotificationService() {
        val intent = Intent(this, NotificationService::class.java)
        startService(intent) // TODO: start service on device startup too.
    }
}

private class UserContextAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        return ApplicationListFragment()
    }

    override fun getPageTitle(position: Int): CharSequence {
        return "Page $position"
    }
}
