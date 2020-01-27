package dev.plotsky.spotikt.spotify.data

class AlbumListens(
    private val artistName: String
) {
    private val albumCounts = mutableMapOf<String, Int>()

    fun addListen(albumName: String) {
        val count = albumCounts[albumName]
        if (count == null) {
            albumCounts[albumName] = 1
        } else {
            albumCounts[albumName] = count + 1
        }
    }

    fun getAlbumMap(): Map<String, Map<String, Int>> {
        return mapOf(
            this.artistName to this.albumCounts.toMap()
        )
    }
}
