package bd.naim.smsrelay

object ForwardingPause {
    @Volatile
    var paused: Boolean = false
}
