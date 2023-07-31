package edu.ivankuznetsov.registerdataviabarcode.util

import android.app.Activity
import android.content.DialogInterface
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import edu.ivankuznetsov.registerdataviabarcode.R

public fun showErrorMessage(activity: Activity, title: String, text: String, onPositive: DialogInterface.OnClickListener){
    MaterialAlertDialogBuilder(activity,
        R.style.AlertDialogTheme).setTitle(title).setPositiveButton("OK", onPositive).setMessage(text).show()
}