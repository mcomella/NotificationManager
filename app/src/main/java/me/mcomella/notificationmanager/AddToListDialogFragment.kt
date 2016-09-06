package me.mcomella.notificationmanager

import android.app.Dialog
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

    private val STYLE = DialogFragment.STYLE_NO_TITLE
    private val THEME = android.R.style.Theme_Material_Light_Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE, THEME)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.fragment_add_to_list_dialog, container, false)
        return view
    }
}
