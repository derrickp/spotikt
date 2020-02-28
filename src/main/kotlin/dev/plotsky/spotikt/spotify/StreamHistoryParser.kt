package dev.plotsky.spotikt.spotify

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dev.plotsky.spotikt.spotify.data.Listen
import java.io.File
import java.lang.reflect.ParameterizedType

class StreamHistoryParser(moshi: Moshi) {
    private val type: ParameterizedType =
            Types.newParameterizedType(List::class.java, Listen::class.java)
    private val adapter: JsonAdapter<List<Listen>> = moshi.adapter(type)

    fun parse(file: File): List<Listen> {
        val json = file.readText()
        val list = adapter.fromJson(json)
        return list ?: emptyList()
    }
}
