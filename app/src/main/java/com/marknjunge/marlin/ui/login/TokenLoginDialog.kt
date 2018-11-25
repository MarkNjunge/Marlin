package com.marknjunge.marlin.ui.login

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.marknjunge.marlin.R

class TokenLoginDialog : DialogFragment() {
    var onSelected: ((token: String, canWrite: Boolean) -> Unit)? = null



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Personal Access Token")

        val inflater = LayoutInflater.from(requireContext())

        val view = inflater.inflate(R.layout.dialog_token_login, null)

        val etToken: EditText = view.findViewById(R.id.etToken)
        val cbWrite: CheckBox = view.findViewById(R.id.cbWrite)

        builder.setView(view)
                .setPositiveButton("Login") { _, _ ->
                    onSelected?.invoke(etToken.text.toString(), cbWrite.isChecked)
                }
                .setNegativeButton("Cancel") { _, _ ->
                    dismiss()
                }
        return builder.create()
    }
}