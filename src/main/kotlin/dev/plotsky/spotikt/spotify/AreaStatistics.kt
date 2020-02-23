package dev.plotsky.spotikt.spotify

import dev.plotsky.musikt.entities.areas.Area

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
            if (!countryMatches(countryName, country)) {
                continue
            }
            var subdivision = artistArea.areas
                    .firstOrNull { it.type == "Subdivision" } ?: continue
            if (subdivisionCounts.containsKey(subdivision.name)) {
                val existing = subdivisionCounts[subdivision.name]!!
                subdivisionCounts[subdivision.name] = existing + 1
            } else {
                subdivisionCounts[subdivision.name] = 1
            }
        }
        return subdivisionCounts.toMap()
    }

    private fun countryMatches(countryName: String, country: Area): Boolean {
        return country.name == countryName || country.sortName == countryName
    }
}
