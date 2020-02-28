package dev.plotsky.spotikt.spotify

import com.squareup.moshi.Moshi
import java.io.File

object ParsedHistoryLoader {
    fun fromFile(fileName: String, moshi: Moshi): Results {
        val adapter = moshi.adapter(Results::class.java)
        val file = File(fileName)
        return if (file.isFile) {
            val json = file.readText()
            adapter.fromJson(json)!!
        } else {
            Results(0)
        }
    }

    fun flush(moshi: Moshi, fileName: String, history: Results) {
        val adapter = moshi.adapter(Results::class.java)
        val file = File(fileName)
        val json = adapter.toJson(history)
        file.writeText(json)
    }
}
