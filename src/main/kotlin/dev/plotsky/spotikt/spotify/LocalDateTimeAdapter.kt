package dev.plotsky.spotikt.spotify

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateTimeAdapter {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd H:m")
    @ToJson fun toJson(localDateTime: LocalDateTime): String {
        return localDateTime.format(formatter)
    }

    @FromJson fun fromJson(dateString: String): LocalDateTime {
        return LocalDateTime.parse(dateString, formatter)
    }
}
