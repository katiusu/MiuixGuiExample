package com.katiusu.miuixexample.data

/** Transient diagnostic commands, deliberately not preferences or backup data. */
object CardPerformanceContract {
    const val ACTION = "com.katiusu.miuixexample.action.CARD_PERFORMANCE"
    const val COMMAND = "command"
    const val REPLY = "reply"
    const val REPORT = "report"
    const val ERROR = "error"
    const val START = "start"
    const val STOP = "stop"
    const val STATUS = "status"
    const val MAX_DURATION_MS = 60_000L
}
