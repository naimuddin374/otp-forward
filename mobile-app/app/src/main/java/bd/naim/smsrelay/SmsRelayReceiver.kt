package bd.naim.smsrelay

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import kotlin.concurrent.thread

class SmsRelayReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        try {
            if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return
            if (ForwardingPause.paused) return
            val body = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                .joinToString(separator = "") { it.displayMessageBody ?: "" }
            if (!looksLikeOtp(body)) return
            val pending = goAsync()
            thread {
                try {
                    TelegramClient.send(body)
                } catch (_: Exception) {
                    // A failed forward must not change how the phone stores the SMS.
                } finally {
                    pending.finish()
                }
            }
        } catch (_: Exception) {
            // A bad message must not crash the receiver.
        }
    }
}
