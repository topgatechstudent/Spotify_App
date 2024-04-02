package datamodels

import kotlinx.serialization.Serializable

@Serializable
data class SpotifyTopArtistsResponse(
    val items: List<ArtistItem>
)

@Serializable
data class ArtistItem(
//    val externalUrls: Map<String, String>,
    val followers: Followers,
    val genres: List<String>,
    val href: String,
    val id: String,
    val images: List<ArtistImage>,
    val name: String,
    val popularity: Int,
    val type: String,
    val uri: String
)

@Serializable
data class Followers(
    val href: String? = null,
    val total: Int
)

@Serializable
data class ArtistImage(
    val height: Int,
    val url: String,
    val width: Int
)
