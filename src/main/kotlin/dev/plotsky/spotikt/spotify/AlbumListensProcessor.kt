package dev.plotsky.spotikt.spotify

import dev.plotsky.musikt.Client
import dev.plotsky.musikt.entities.recordings.Recording
import dev.plotsky.musikt.entities.releases.ReleaseReference
import dev.plotsky.musikt.search.recordings.RecordingQuery
import dev.plotsky.spotikt.spotify.data.ArtistResults
import dev.plotsky.spotikt.spotify.data.Listen
import java.lang.Thread.sleep
import java.net.SocketTimeoutException

const val FLUSH_INTERVAL = 10
const val PRINT_INTERVAL = 100
const val MOD_LEFTOVER = 0
const val MIN_PLAYED = 1.0
const val MIN_RECORDING_SCORE = 90

class AlbumListensProcessor(
    private val listens: List<Listen>,
    private val client: Client,
    private val history: Results,
    private val flushHistory: (history: Results) -> Unit
) {
    private val recordings = mutableMapOf<String, Recording?>()

    fun process(year: Int?) {
        val streams = if (year != null) listensByYear(year) else allListens()
        println("Total number of listens: ${streams.size}")
        var count = 1
        val toProcess = streams
                .slice(history.currentParseIndex until streams.size)
        println("${toProcess.size}")
        for (listen in toProcess) {
            val recording = getRecording(listen)
            if (recording == null) {
                addListens(listen)
            } else {
                val officialReleases = recording.releases
                        .filter { it.status == "Official" }
                addListens(listen, officialReleases)
            }
            if (count % FLUSH_INTERVAL == MOD_LEFTOVER) {
                flushHistory(history)
            }
            if (count % PRINT_INTERVAL == MOD_LEFTOVER) {
                println("${listen.artistName} $count")
            }
            sleep(SLEEP_INTERVAL)
            count++
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
                .filter { it.endTime.year == year &&
                        it.minutesPlayed() > MIN_PLAYED }
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
            for (release in releases) {
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
        return try {
            val recordings = client.recordings.getByQuery(query)
            if (recordings.isEmpty()) {
                println(query.getEncodedQuery())
            }
            val highScores = recordings
                .filter { it.score!! >= MIN_RECORDING_SCORE }
            highScores.firstOrNull()
        } catch (exception: SocketTimeoutException) {
            println(query.getEncodedQuery())
            null;
        }
    }
}
