package dev.plotsky.spotikt.spotify

import dev.plotsky.spotikt.spotify.data.ArtistResults

data class ParsedHistory(
        var currentParseIndex: Int,
        val listens: MutableList<ArtistResults> = mutableListOf()
) {
    fun incrementIndex() {
        currentParseIndex += 1
    }

    fun getArtistResults(artistName: String): ArtistResults? {
        return listens.find { it.artistName == artistName }
    }

    fun topArtists(count: Int): List<ArtistResults> {
        return listens.sortedByDescending { it.maxAlbumCount }.take(count)
    }
}