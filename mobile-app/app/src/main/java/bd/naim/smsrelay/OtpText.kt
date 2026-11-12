package bd.naim.smsrelay

val forwardWords = listOf(
    "OTP",
    "IVAC",
    "One Time Password",
)

fun looksLikeOtp(text: String): Boolean {
    return try {
        val lower = text.lowercase()
        forwardWords.any { word -> lower.contains(word.lowercase()) }
    } catch (_: Exception) {
        false
    }
}
