package datamodels
import kotlinx.serialization.Serializable

@Serializable
data class SpotifyArtistHistoryResponse(
    val items: List<Artist>
)

@Serializable
data class Artist(
    /*val album: Album,
    val artists: List<Artist>,
    val available_markets: List<String>,
    val disc_number: Int,
    val duration_ms: Int,
    val explicit: Boolean,
    val external_urls: Map<String, String>,
    val href: String,
    val id: String,
    val is_playable: Boolean = false, */
    val name: String,
    /*val popularity: Int,
    val preview_url: String?,
    val track_number: Int,
    val type: String,
    val uri: String, */
)

/*@Serializable
data class Album(
    val album_type: String,
    val total_tracks: Int,
    val available_markets: List<String>,
    val external_urls: Map<String, String>,
    val href: String,
    val id: String,
    val images: List<Image>,
    val name: String,
    val release_date: String,
    val release_date_precision: String,
    val type: String,
    val uri: String
)

@Serializable
data class Artist(
    val external_urls: Map<String, String>,
    val href: String,
    val id: String,
    val name: String,
    val type: String,
    val uri: String
)

@Serializable
data class Image(
    val url: String,
    val height: Int,
    val width: Int
)

@Serializable
data class TrackContext(
    val type: String,
    val href: String?,
    val external_urls: Map<String, String>?,
    val uri: String
)*/
