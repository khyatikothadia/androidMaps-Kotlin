package com.example.myapplication.util

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog

class Utils {

    companion object {

        /**
         * Display alert dialog for validation and other massages.
         *
         * @param message      Message to display
         * @param context  Activity context
         * @param isFinish boolean to indicate activity state
         */
        fun displayDialog(message: String?, context: Context, isFinish: Boolean) {
            val alertDialog = AlertDialog.Builder(context)
            alertDialog.setMessage(message)
            alertDialog.setPositiveButton(
                context.getString(android.R.string.ok)
            ) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
                if (isFinish) {
                    (context as Activity).finish()
                }
            }
            val dialog = alertDialog.create()
            if (!(context as Activity).isFinishing) {
                if (!dialog.isShowing) {
                    alertDialog.show()
                }
            }
        }

        /**
         * function to close soft keyboard
         *
         * @param activity Activity context
         */
        fun hideSoftKeyboard(activity: Activity) {
            val inputMethodManager =
                (activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
            if (inputMethodManager.isActive) {
                if (activity.currentFocus != null) {
                    inputMethodManager.hideSoftInputFromWindow(
                        activity.currentFocus!!.windowToken, 0
                    )
                }
            }
        }

        /**
         * function to check network availability
         *
         * @param context Required to query internet availability
         * @return true in case if network is available, false otherwise.
         */
        @RequiresApi(Build.VERSION_CODES.M)
        fun isNetworkConnected(context: Context): Boolean {

            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
            return networkCapabilities != null &&
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }
    }
}