package dev.plotsky.spotikt.spotify.data

class ArtistResults(
     val artistName: String,
     val albumCounts: MutableMap<String, Int> = mutableMapOf()
) {
    val maxAlbumCount: Int
        get() = albumCounts.values.max()!!
    val maxAlbum: Triple<String, String, Int>
        get()  {
            val maxAlbum = albumCounts.maxBy { it.value }!!
            return Triple(artistName, maxAlbum.key, maxAlbum.value)
        }
    fun addListen(albumName: String) {
        val count = albumCounts[albumName]
        if (count == null) {
            albumCounts[albumName] = 1
        } else {
            albumCounts[albumName] = count + 1
        }
    }
}
