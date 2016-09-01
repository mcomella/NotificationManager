package me.mcomella.notificationmanager

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import kotlinx.android.synthetic.main.activity_application_list.*

class ApplicationListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_application_list)

        application_list.adapter = ApplicationListAdapter()
        application_list.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        application_list.layoutManager = layoutManager
    }
}

private class ApplicationListViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
    val title = itemView!!.findViewById(R.id.title) as TextView
}

private class ApplicationListAdapter : RecyclerView.Adapter<ApplicationListViewHolder>() {
    override fun getItemCount(): Int {
        return 2
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ApplicationListViewHolder {
        val view = LayoutInflater.
                from(parent!!.context).
                inflate(R.layout.application_item, parent, false)
        return ApplicationListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ApplicationListViewHolder?, position: Int) {
        holder!!.title.text = "An application " + position
    }
}