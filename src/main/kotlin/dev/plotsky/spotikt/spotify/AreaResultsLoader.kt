package dev.plotsky.spotikt.spotify

import com.squareup.moshi.Moshi
import java.io.File

class AreaResultsLoader {
    companion object Factory {
        fun fromFile(fileName: String, moshi: Moshi): AreaResults {
            val adapter = moshi.adapter(AreaResults::class.java)
            val file = File(fileName)
            return if (file.isFile) {
                val json = file.readText()
                adapter.fromJson(json)!!
            } else {
                AreaResults(0)
            }
        }

        fun flush(moshi: Moshi, fileName: String, results: AreaResults) {
            val adapter = moshi.adapter(AreaResults::class.java)
            val file = File(fileName)
            val json = adapter.toJson(results)
            file.writeText(json)
        }
    }
}
