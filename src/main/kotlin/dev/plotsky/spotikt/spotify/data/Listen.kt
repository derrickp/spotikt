package dev.plotsky.spotikt.spotify.data

import java.time.LocalDateTime

data class Listen(
    val endTime: LocalDateTime,
    val artistName: String,
    val trackName: String,
    val msPlayed: Int
) {
    fun minutesPlayed(): Double {
        return msPlayed / 1000.0 / 60.0
    }

    fun secondsPlayed(): Int {
        return msPlayed / 1000
    }
}
