package dev.plotsky.spotikt.spotify

import com.squareup.moshi.Moshi
import dev.plotsky.spotikt.spotify.data.ArtistResults
import java.io.File

class ParsedHistoryLoader {
    companion object Factory {
        fun fromFile(fileName: String, moshi: Moshi): ParsedHistory {
            val adapter = moshi.adapter(ParsedHistory::class.java)
            val file = File(fileName)
            return if (file.isFile) {
                val json = file.readText()
                adapter.fromJson(json)!!
            } else {
                ParsedHistory(0)
            }
        }

        fun flush(moshi: Moshi, fileName: String, history: ParsedHistory) {
            val adapter = moshi.adapter(ParsedHistory::class.java)
            val file = File(fileName)
            val json = adapter.toJson(history)
            file.writeText(json)
        }
    }
}