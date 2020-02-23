package dev.plotsky.spotikt.spotify

import dev.plotsky.musikt.entities.areas.Area
import dev.plotsky.spotikt.spotify.data.ArtistAreas

data class AreaResults(
    var currentParseIndex: Int,
    val artistAreas: MutableList<ArtistAreas> = mutableListOf()
) {
    fun incrementIndex() {
        currentParseIndex += 1
    }

    fun containsArtist(name: String): Boolean {
        return artistAreas.any { it.name == name }
    }

    fun addArtist(name: String, areas: List<Area>) {
        if (containsArtist(name)) {
            return
        }

        val artistArea = ArtistAreas(name = name, areas = areas)
        artistAreas.add(artistArea)
    }
}
