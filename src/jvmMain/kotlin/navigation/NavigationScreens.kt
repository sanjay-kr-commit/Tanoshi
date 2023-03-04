package navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavigationScreens(
    val label: String,
    val icon: ImageVector
) {
    Bookmark(
        label = "Bookmark" ,
        icon = Icons.Filled.Bookmarks
    ) ,
    Anime(
        label = "Anime" ,
        icon = Icons.Filled.PlayArrow
    ),
    AnimeDetail(
        label = "AnimeDetail" ,
        icon = Icons.Filled.Pages
    ),
    VideoPlayer(
        label = "VideoPlayer" ,
        icon = Icons.Filled.VideoLabel
    ),
    Manga(
        label = "Manga" ,
        icon = Icons.Filled.LibraryBooks
    ),
    MangaDetail(
        label = "MangaDetail" ,
        icon = Icons.Filled.Pages
    ),
    MangaReader(
        label = "MangaReader" ,
        icon = Icons.Filled.Pageview
    ),
    Novel(
        label = "Novel" ,
        icon = Icons.Filled.MenuBook
    ),
    NovelDetail(
        label = "NovelDetail" ,
        icon = Icons.Filled.Pages
    ),
    NovelReader(
        label = "NovelReader" ,
        icon = Icons.Filled.Pageview
    ),
    Loading(
        label = "Loading" ,
        icon = Icons.Filled.PlayCircle
    ),
    Downloads(
        label = "Downloads" ,
        icon = Icons.Filled.Downloading
    ),
    Settings(
        label = "Settings" ,
        icon = Icons.Filled.Settings
    ),
    ExtensionRepoList(
        label = "ExtensionRepoList" ,
        icon = Icons.Filled.Archive
    )

}