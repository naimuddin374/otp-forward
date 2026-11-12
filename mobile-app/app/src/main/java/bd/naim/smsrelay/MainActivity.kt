package bd.naim.smsrelay

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private val requestSms = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        showStatus()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.allowSms).setOnClickListener {
            requestSms.launch(Manifest.permission.RECEIVE_SMS)
        }
        findViewById<Button>(R.id.forwarding).setOnClickListener {
            ForwardingPause.paused = !ForwardingPause.paused
            showStatus()
        }
        findViewById<Button>(R.id.sendPasted).setOnClickListener {
            sendPastedSms()
        }
    }

    private fun sendPastedSms() {
        val text = findViewById<EditText>(R.id.pastedSms).text.toString().trim()
        val send = findViewById<Button>(R.id.sendPasted)
        if (text.isEmpty()) {
            showMessage(R.string.status_paste_empty, R.color.status_warn)
            return
        }
        send.isEnabled = false
        thread {
            val sent = try {
                TelegramClient.send(text)
                true
            } catch (_: Exception) {
                false
            }
            runOnUiThread {
                send.isEnabled = true
                if (sent) showMessage(R.string.status_sent, R.color.status_ready)
                else showMessage(R.string.status_send_failed, R.color.status_error)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        showStatus()
    }

    override fun onStop() {
        ForwardingPause.paused = false
        super.onStop()
    }

    private fun showStatus() {
        val forwarding = findViewById<Button>(R.id.forwarding)
        val smsAllowed = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) ==
            PackageManager.PERMISSION_GRANTED
        val configured = BuildConfig.TELEGRAM_BOT_TOKEN.isNotBlank() &&
            BuildConfig.TELEGRAM_CHAT_ID.isNotBlank()
        forwarding.text = getString(
            if (ForwardingPause.paused) R.string.start_forwarding else R.string.stop_forwarding
        )
        when {
            !configured -> showMessage(R.string.status_missing_config, R.color.status_error)
            !smsAllowed -> showMessage(R.string.status_need_sms, R.color.status_warn)
            ForwardingPause.paused -> showMessage(R.string.status_paused, R.color.status_paused)
            else -> showMessage(R.string.status_ready, R.color.status_ready)
        }
    }

    private fun showMessage(message: Int, tone: Int) {
        findViewById<TextView>(R.id.status).setText(message)
        findViewById<MaterialCardView>(R.id.statusCard)
            .setCardBackgroundColor(ContextCompat.getColor(this, tone))
    }
}
