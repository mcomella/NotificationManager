package me.mcomella.notificationmanager

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


class AddToListDialogFragment() : DialogFragment() {
    companion object Factory {
        fun newInstance(): AddToListDialogFragment {
            val frag = AddToListDialogFragment()

            // insert args here
            return frag
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.fragment_add_to_list_dialog, container, false)
        return view
    }
}
