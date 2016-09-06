package me.mcomella.notificationmanager

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class AddListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_list)
        supportActionBar!!.setTitle("Add list")
    }

    override fun onStart() {
        super.onStart()
    }
}
