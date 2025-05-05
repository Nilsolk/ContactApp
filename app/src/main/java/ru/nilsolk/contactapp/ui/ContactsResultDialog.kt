package ru.nilsolk.contactapp.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class CustomDialog(private val message: String) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        return AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton("Ok") { dialog, i ->
                dialog.dismiss()
            }
            .create()
    }
}