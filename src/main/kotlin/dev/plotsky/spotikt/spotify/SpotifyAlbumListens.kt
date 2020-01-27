package dev.plotsky.spotikt.spotify

import dev.plotsky.musikt.entities.Recording
import dev.plotsky.musikt.entities.ReleaseReference
import dev.plotsky.musikt.search.RecordingQuery
import dev.plotsky.musikt.search.RecordingRepository
import dev.plotsky.spotikt.spotify.data.AlbumListens
import dev.plotsky.spotikt.spotify.data.Listen
import java.lang.Thread.sleep

class SpotifyAlbumListens(
        private val listens: List<Listen>,
        private val recordingRepository: RecordingRepository
) {
    private val albumListens = mutableMapOf<String, AlbumListens>()
    private val recordings = mutableMapOf<String, Recording?>()
    fun fillAlbumListens(year: Int): List<AlbumListens> {
        val yearListens = listens
                .filter { it.endTime.year == year && it.minutesPlayed() > 1.0 }
        println("Total number of listens: ${yearListens.size}")
        var count = 1
        for (listen in yearListens) {
            val recording = getRecording(listen)
            if (recording == null) {
                addListens(listen)
            } else {
                val officialReleases = recording.releases.filter { it.status == "Official" }
                addListens(listen, officialReleases)
            }
            if (count % 100 == 0 )println("${listen.artistName} $count")
            sleep(1500L)
            count += 1
        }
        return albumListens.values.toList()
    }

    private fun addListens(listen: Listen) {
        val albumListen = albumListens[listen.artistName] ?: AlbumListens(listen.artistName)
        albumListen.addListen(("UnknownAlbum"))
        this.albumListens[listen.artistName] = albumListen
    }

    private fun addListens(listen: Listen, releases: List<ReleaseReference>) {
        val albumListen = albumListens[listen.artistName] ?: AlbumListens(listen.artistName)
        for(release in releases) {
            albumListen.addListen(release.title)
        }
        this.albumListens[listen.artistName] = albumListen
    }

    private fun getRecording(listen: Listen): Recording? {
        val query = RecordingQuery(
                artistname = "\"${listen.artistName}\"",
                title = scrubTitle(listen.trackName)
        )
        val key = query.getQuery()
        return if (recordings.containsKey(key)) {
            recordings[key]
        } else {
            val recording = getRecording(query)
            recordings[key] = recording
            recording
        }
    }

    private fun scrubTitle(title: String): String {
        return title
                .replace(" - 2015 Remaster", "")
                .replace("...?", "")
    }

    private fun getRecording(query: RecordingQuery): Recording? {
        val recordings = recordingRepository.getByQuery(query)
        if (recordings.isEmpty()) {
            println(query.getEncodedQuery())
        }
        val highScores = recordings.filter { it.score!! >= 90 }
        return highScores.firstOrNull()
    }
}
