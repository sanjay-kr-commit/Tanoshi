package shared

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.window.WindowState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import logic.ExtensionManager
import navigation.NavController
import navigation.NavigationScreens
import uk.co.caprica.vlcj.player.component.EmbeddedMediaListPlayerComponent
import logic.helper.Preferences

class AppData {

    val hiddenScreens : List<String> = listOf(
        NavigationScreens.AnimeDetail.name ,
        NavigationScreens.VideoPlayer.name ,
        NavigationScreens.MangaDetail.name ,
        NavigationScreens.MangaReader.name ,
        NavigationScreens.NovelDetail.name ,
        NavigationScreens.NovelReader.name ,
        NavigationScreens.Loading.name ,
        NavigationScreens.ExtensionRepoList.name
    )
    val screens : List<NavigationScreens> = NavigationScreens.values().toList()
    val navController: NavController = NavController( NavigationScreens.Bookmark.name )
    val currentScreen : MutableState<String> = navController.currentScreen

    var extensionManager = ExtensionManager().loadJarFromPath( Preferences.getExtensionFolder )

    var anime = extensionManager.animeSource
    var manga = extensionManager.mangaSource
    var novel = extensionManager.novelSource

    val scope = CoroutineScope( Dispatchers.Default )

    lateinit var embeddedMediaListPlayerComponent : EmbeddedMediaListPlayerComponent
    var title = ""

    var appState : WindowState = WindowState()
    var navigationBarHidden = mutableStateOf( false )

}