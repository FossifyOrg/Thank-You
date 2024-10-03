package org.fossify.thankyou.extensions

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import org.fossify.commons.helpers.isTiramisuPlus

@SuppressLint("UnspecifiedRegisterReceiverFlag")
fun <T> Context.createBroadcastFlow(
    intentFilter: IntentFilter,
    emitOnCollect: Boolean = false,
    value: (intent: Intent?) -> T
) = callbackFlow {
    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            trySend(value(intent))
        }
    }

    if (emitOnCollect) {
        trySend(value(null))
    }

    if (isTiramisuPlus()) {
        registerReceiver(receiver, intentFilter, Context.RECEIVER_EXPORTED)
    } else {
        registerReceiver(receiver, intentFilter)
    }

    awaitClose {
        unregisterReceiver(receiver)
    }
}