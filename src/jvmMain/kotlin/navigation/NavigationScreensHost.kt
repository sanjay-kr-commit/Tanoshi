package navigation

import androidx.compose.runtime.Composable
import mangaReader.MangaReader
import novelReader.NovelReader
import shared.AppData
import ui.component.Loading
import ui.screen.*
import videoPlayer.VideoPlayer

@Composable
fun NavigationScreensHost(
    navController: NavController ,
    sharedData : AppData
) {
    NavigationHost(navController) {
        composable(NavigationScreens.Bookmark.name) {
            Bookmark( sharedData )
        }
        composable( NavigationScreens.Anime.name ) {
            Anime( sharedData )
        }
        composable( NavigationScreens.AnimeDetail.name ) {
            AnimeDetail( sharedData )
        }
        composable( NavigationScreens.VideoPlayer.name ) {
            VideoPlayer( sharedData )
        }
        composable( NavigationScreens.Manga.name ) {
            Manga( sharedData )
        }
        composable( NavigationScreens.MangaDetail.name ) {
            MangaDetail( sharedData )
        }
        composable( NavigationScreens.MangaReader.name ) {
            MangaReader( sharedData )
        }
        composable( NavigationScreens.Novel.name ) {
            Novel( sharedData )
        }
        composable( NavigationScreens.NovelDetail.name ) {
            NovelDetail( sharedData )
        }
        composable( NavigationScreens.NovelReader.name ) {
            NovelReader( sharedData )
        }
        composable( NavigationScreens.Loading.name ) {
            Loading()
        }
        composable( NavigationScreens.Downloads.name ) {
            Downloads( sharedData )
        }
        composable( NavigationScreens.Settings.name ) {
            Settings( sharedData )
        }
        composable( NavigationScreens.ExtensionRepoList.name ) {
            ExtensionRepoList( sharedData )
        }
    }.build()
}