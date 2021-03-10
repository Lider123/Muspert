package com.babaetskv.muspert.app.device

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.babaetskv.muspert.domain.model.TrackPushData
import com.babaetskv.muspert.app.ui.MainActivity

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Intent(context, MainActivity::class.java).apply {
            val data = intent.getParcelableExtra(MainActivity.EXTRA_TRACK_DATA) as? TrackPushData
            putExtra(MainActivity.EXTRA_TRACK_DATA, data)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }.let {
           context.startActivity(it)
       }
    }

    companion object {
        const val BROADCAST_ACTION = "com.babaetskv.muspert.notification"
    }
}
