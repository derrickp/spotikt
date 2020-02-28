package dev.plotsky.spotikt.spotify.data

import java.time.LocalDateTime

const val MS_IN_SEC = 1000
const val SEC_IN_MIN = 60.0

data class Listen(
    val endTime: LocalDateTime,
    val artistName: String,
    val trackName: String,
    val msPlayed: Int
) {
    fun minutesPlayed(): Double {
        return msPlayed / MS_IN_SEC.toFloat() / SEC_IN_MIN
    }

    fun secondsPlayed(): Int {
        return msPlayed / MS_IN_SEC
    }
}
