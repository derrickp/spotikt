package dev.plotsky.spotikt.spotify

import dev.plotsky.musikt.Client
import dev.plotsky.musikt.entities.areas.Area
import dev.plotsky.musikt.entities.artists.Artist
import dev.plotsky.musikt.search.IdOptions
import dev.plotsky.musikt.search.artists.ArtistQuery
import dev.plotsky.spotikt.spotify.data.Listen
import java.lang.Thread.sleep

const val SLEEP_INTERVAL = 1500L

class ArtistAreaProcessor(
    private val listens: List<Listen>,
    private val client: Client,
    private val results: AreaResults,
    private val flush: (results: AreaResults) -> Unit
) {
    fun process() {
        val toProcess = listens
                .slice(results.currentParseIndex until listens.size)
        for (listen in toProcess) {
            results.incrementIndex()
            if (!results.containsArtist(listen.artistName)) {
                processArtistArea(listen)
            }
        }

        flush(results)
    }

    private fun processArtistArea(listen: Listen) {
        val artist = getArtist(listen.artistName) ?: return

        val areaReference = artist.beginArea ?: artist.area
        println(areaReference)
        if (areaReference == null) {
            results.addArtist(listen.artistName, emptyList())
        } else {
            val areas = getAllAreas(areaReference.id, emptyList())
            val trimmedAreas = areas.map { trimArea(it) }
            results.addArtist(listen.artistName, trimmedAreas)
        }

        flush(results)
        sleep(SLEEP_INTERVAL)
    }

    private fun trimArea(area: Area): Area {
        return Area(
            id = area.id,
            type = area.type,
            name = area.name,
            sortName = area.sortName,
            lifeSpan = area.lifeSpan,
            score = area.score
        )
    }

    private fun getAllAreas(id: String, areas: List<Area>): List<Area> {
        sleep(SLEEP_INTERVAL)
        val idOptions = IdOptions(
                id = id,
                relationships = listOf("area-rels")
        )
        val area = client.areas.getById(idOptions)
        return when {
            area == null -> {
                emptyList<Area>() + areas
            }
            area.relatedAreas.isEmpty() -> {
                listOf(area) + areas
            }
            else -> {
                val backward = area.relatedAreas
                        .firstOrNull { it.direction == "backward" }
                if (backward == null) {
                    areas + listOf(area)
                } else {
                    getAllAreas(
                        id = backward.area.id,
                        areas = areas + listOf(area)
                    )
                }
            }
        }
    }

    private fun getArtist(name: String): Artist? {
        val artistQuery = ArtistQuery(artist = "\"${name}\"")
        val artists = client.artists.getByQuery(artistQuery)
        val max = artists.maxByOrNull { it.score!! } ?: return null
        sleep(SLEEP_INTERVAL)
        val idOptions = IdOptions(max.id, listOf("area-rels"))
        return client.artists.getById(idOptions)
    }
}
