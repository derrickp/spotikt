package dev.plotsky.spotikt.spotify

import dev.plotsky.musikt.entities.areas.Area
import dev.plotsky.spotikt.spotify.data.ArtistAreas

class AreaStatistics(private val areaResults: AreaResults) {
    fun countryCounts(): Map<String, Int> {
        val countryCounts: MutableMap<String, Int> = mutableMapOf()
        for (artistArea in areaResults.artistAreas) {
            val country = artistArea.areas
                    .firstOrNull { it.type == "Country" } ?: continue
            if (countryCounts.containsKey(country.name)) {
                val existing = countryCounts[country.name]!!
                countryCounts[country.name] = existing + 1
            } else {
                countryCounts[country.name] = 1
            }
        }
        return countryCounts.toMap()
    }

    fun subDivisionCounts(countryName: String): Map<String, Int> {
        val subdivisionCounts: MutableMap<String, Int> = mutableMapOf()
        for (artistArea in areaResults.artistAreas) {
            val country = artistArea.areas
                    .firstOrNull { it.type == "Country" } ?: continue
            if (countryMatches(countryName, country)) {
                processArtistArea(artistArea, subdivisionCounts)
            }
        }
        return subdivisionCounts.toMap()
    }

    private fun processArtistArea(
        artistAreas: ArtistAreas,
        collector: MutableMap<String, Int>
    ) {
        val subdivision = artistAreas.areas
                .firstOrNull { it.type == "Subdivision" } ?: return
        if (collector.containsKey(subdivision.name)) {
            val existing = collector[subdivision.name]!!
            collector[subdivision.name] = existing + 1
        } else {
            collector[subdivision.name] = 1
        }
    }

    private fun countryMatches(countryName: String, country: Area): Boolean {
        return country.name == countryName || country.sortName == countryName
    }
}
