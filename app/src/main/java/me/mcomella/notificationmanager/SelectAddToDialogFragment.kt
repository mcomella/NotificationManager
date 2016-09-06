package me.mcomella.notificationmanager

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_add_to_list_dialog.*

class SelectAddToDialogFragment() : DialogFragment() {
    companion object Factory {
        fun newInstance(): SelectAddToDialogFragment {
            val frag = SelectAddToDialogFragment()

            // insert args here
            return frag
        }
    }

    private val STYLE = DialogFragment.STYLE_NO_TITLE
    private val THEME = android.R.style.Theme_Material_Light_Dialog

    var onAddListClickListener: () -> Unit = {}
    var onAddAppClickListener: () -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE, THEME)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.fragment_add_to_list_dialog, container, false)
        setClickListeners(view)
        return view
    }

    private fun setClickListeners(root: View) {
        val addListButton = root.findViewById(R.id.addListButton)
        val addAppButton = root.findViewById(R.id.addAppButton)

        addListButton.setOnClickListener {
            dismissAndCall(onAddListClickListener)
        }
        addAppButton.setOnClickListener {
            dismissAndCall(onAddAppClickListener)
        }
    }

    private fun dismissAndCall(callback: () -> Unit) {
        dismiss()
        callback()
    }
}
