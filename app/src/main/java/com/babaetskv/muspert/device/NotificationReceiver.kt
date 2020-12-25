package com.babaetskv.muspert.device

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.babaetskv.muspert.ui.MainActivity
import timber.log.Timber

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Timber.e("onReceive()") // TODO: remove
       Intent(context, MainActivity::class.java).apply {
            putExtra(MainActivity.EXTRA_TRACK_ID, intent.getLongExtra(MainActivity.EXTRA_TRACK_ID, -1))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }.let {
           context.startActivity(it)
       }
    }

    companion object {
        const val BROADCAST_ACTION = "com.babaetskv.muspert.notification"
    }
}
