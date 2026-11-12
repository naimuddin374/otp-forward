package bd.naim.smsrelay

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object TelegramClient {
    fun send(text: String) {
        val token = BuildConfig.TELEGRAM_BOT_TOKEN
        val chatId = BuildConfig.TELEGRAM_CHAT_ID
        if (token.isBlank() || chatId.isBlank()) {
            throw IllegalStateException("Telegram token or chat id is missing")
        }
        val payload = JSONObject()
            .put("chat_id", chatId)
            .put("text", text)
            .toString()
            .toByteArray(Charsets.UTF_8)
        val url = URL("https://api.telegram.org/bot$token/sendMessage")
        var lastError: Exception? = null
        repeat(3) { attempt ->
            try {
                val connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    doOutput = true
                    connectTimeout = 8000
                    readTimeout = 8000
                    setRequestProperty("Content-Type", "application/json")
                }
                connection.outputStream.use { it.write(payload) }
                val code = connection.responseCode
                connection.disconnect()
                if (code in 200..299) return
                lastError = IllegalStateException("Telegram returned $code")
            } catch (error: Exception) {
                lastError = error
            }
            if (attempt < 2) Thread.sleep(1000)
        }
        throw lastError ?: IllegalStateException("Telegram request failed")
    }
}
