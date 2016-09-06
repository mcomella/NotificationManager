package me.mcomella.notificationmanager

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add_list.*

class AddListActivity : AppCompatActivity() {
    companion object Keys {
        val KEY_LIST_TITLE = "title"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_list)
        supportActionBar!!.setTitle("Add list")
    }

    override fun onPause() {
        super.onPause()
        setResultFromWidgets()
    }

    override fun onBackPressed() {
        // http://stackoverflow.com/a/6212627
        setResultFromWidgets()
        super.onBackPressed()
    }

    private fun setResultFromWidgets() {
        val res = Intent()
        val bundle = Bundle()
        bundle.putString(KEY_LIST_TITLE, titleEditText.text.toString())
        res.putExtra(ContextSelectorActivity.KEY_BUNDLE, bundle)

        setResult(Activity.RESULT_OK, res)
    }
}
