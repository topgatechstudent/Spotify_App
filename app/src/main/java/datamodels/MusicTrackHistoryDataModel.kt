package datamodels

import kotlinx.serialization.Serializable

@Serializable
data class SpotifyTopTracksResponse(
    val items: List<TrackItem>
)

@Serializable
data class TrackItem(
    val album: Album,
    val artists: List<Artist>,
    val disc_number: Int,
    val duration_ms: Int,
    val explicit: Boolean,
    val external_urls: Map<String, String>,
    val href: String,
    val id: String,
    val is_local: Boolean,
    val name: String,
    val popularity: Int,
    val preview_url: String?,
    val track_number: Int,
    val type: String,
    val uri: String
)

@Serializable
data class Album(
    val album_type: String,
    val artists: List<Artist>,
    val available_markets: List<String>,
    val external_urls: Map<String, String>,
    val href: String,
    val id: String,
    val images: List<TrackImage>,
    val name: String,
    val release_date: String,
    val release_date_precision: String,
    val total_tracks: Int,
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
data class TrackImage(
    val height: Int,
    val url: String,
    val width: Int
)
