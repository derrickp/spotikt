package dev.plotsky.spotikt.spotify

import dev.plotsky.musikt.entities.Recording
import dev.plotsky.musikt.entities.ReleaseReference
import dev.plotsky.musikt.search.RecordingQuery
import dev.plotsky.musikt.search.RecordingRepository
import dev.plotsky.spotikt.spotify.data.ArtistResults
import dev.plotsky.spotikt.spotify.data.Listen
import java.lang.Thread.sleep

class SpotifyAlbumListens(
        private val listens: List<Listen>,
        private val recordingRepository: RecordingRepository,
        private val history: ParsedHistory,
        private val flushHistory: (history: ParsedHistory) -> Unit
) {
    private val recordings = mutableMapOf<String, Recording?>()

    fun fillAlbumListens(year: Int?) {
        val streams = if (year != null) listensByYear(year) else allListens()
        println("Total number of listens: ${streams.size}")
        var count = 1
        val toProcess = streams.slice(history.currentParseIndex until streams.size)
        println("${toProcess.size}")
        for (listen in toProcess) {
            val recording = getRecording(listen)
            if (recording == null) {
                addListens(listen)
            } else {
                val officialReleases = recording.releases.filter { it.status == "Official" }
                addListens(listen, officialReleases)
            }
            if (count % 10 == 0) {
                flushHistory(history)
            }
            if (count % 100 == 0 )println("${listen.artistName} $count")
            sleep(1500L)
            count += 1
            history.incrementIndex()
        }

        flushHistory(history)
    }

    private fun getArtistResults(artistName: String): ArtistResults {
        val fromHistory = history.getArtistResults(artistName)
        return if (fromHistory == null) {
            val artistResults = ArtistResults(artistName)
            history.listens.add(artistResults)
            artistResults
        } else {
            fromHistory
        }
    }

    private fun listensByYear(year: Int): List<Listen> {
        return listens
                .filter { it.endTime.year == year && it.minutesPlayed() > 1.0 }
    }

    private fun allListens(): List<Listen> {
        return listens
                .filter { it.minutesPlayed() > 1.0 }
    }

    private fun addListens(listen: Listen) {
        val albumListen = getArtistResults(listen.artistName)
        albumListen.addListen(("UnknownAlbum"))
    }

    private fun addListens(listen: Listen, releases: List<ReleaseReference>) {
        val albumListen = getArtistResults(listen.artistName)
        if (releases.isEmpty()) {
            addListens(listen)
        } else {
            for(release in releases) {
                albumListen.addListen(release.title)
            }
        }
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
