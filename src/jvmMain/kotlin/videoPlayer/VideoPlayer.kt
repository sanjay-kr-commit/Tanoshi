package videoPlayer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import logic.downloader.Download
import logic.helper.Preferences
import logic.helper.toFile
import shared.AppData
import tanoshi.lib.util.validateDir
import tanoshi.source.api.model.component.Video
import ui.component.Loading
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import java.nio.file.Paths
import kotlin.io.path.readText
import kotlin.io.path.writeText


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VideoPlayer( sharedData : AppData ) = sharedData.run {

    var links by remember { mutableStateOf( emptyList<Video>() ) }
    val mrl = remember { mutableStateOf( "" ) }
    var fetchingLinks by remember { mutableStateOf( true ) }
    var controlHidden by remember { mutableStateOf( true ) }

    val isLibVlcPresent = remember {
        try{
            MediaPlayerFactory()
            true
        } catch ( _ : LinkageError ) {
            false
        }
    }

    if ( links.isEmpty() ) {
        scope.launch {
            links = anime.selected.value!!.fetchEpisodeLink(anime.episode!!)
            if ( links.isEmpty()) navController.navigateBack()
            else mrl.value = links.first().url
            fetchingLinks = false
        }
    }

    if ( !isLibVlcPresent ) Box( Modifier.fillMaxSize() , contentAlignment = Alignment.Center ) { Text( "VLC Player Not Found\nIntall VLC Player to play videos" ) }
    else {
        if ( fetchingLinks ) Box(Modifier.fillMaxSize().background( Color.Black )) { Loading( Color.Transparent ) }
        else Box( Modifier.fillMaxSize().onClick { controlHidden = !controlHidden } ) {

            if (mrl.value.endsWith(".m3u8")) M3U8VideoPlayer(mrl, sharedData)
            else BasicVideoPlayer(mrl, sharedData)


        }
    }

//    DisposableEffect(key1 = mrl, effect = {
//        this.onDispose {
//        }
//    })

}

@Composable
fun M3U8VideoPlayer( mrl : MutableState<String> , sharedData: AppData ) {

    var m3u8Job : Job? by remember { mutableStateOf( null ) }
    val playList = remember { ArrayList<Pair<String,Long>>() }
    var totalTime by remember { mutableStateOf( 0L ) }
    remember { null }
    val baseLink = remember { mrl.value.substring(0, mrl.value.lastIndexOf("/") + 1) }
    val progress = remember { mutableStateOf( 0f ) }
    var downloaderIndex by remember { mutableStateOf( 0 ) }
    var playerIndex by remember { mutableStateOf( 0 ) }
    var buffering by remember { mutableStateOf( false ) }

    Box( Modifier.fillMaxSize() ){
        Box( Modifier.fillMaxSize() ){ VideoSurface( "" , sharedData ) }
        Box( Modifier.fillMaxSize() ) {
            if ( buffering ) Box( Modifier.fillMaxSize() , contentAlignment = Alignment.Center ) {
                Loading( Color.Transparent )
            }
        }

        Box( Modifier.fillMaxSize().padding( 30.dp ) , contentAlignment = Alignment.BottomCenter ) {
            Row {
                Slider(progress.value, steps = playList.size, onValueChange = {
                    (it * playList.size).toInt().let { requestedIndex ->
                        progress.value = requestedIndex.toFloat() / playList.size
                        downloaderIndex = requestedIndex
                        playerIndex = requestedIndex
                    }
                })
                Text( "${playerIndex+1}:${playList.size}" )
            }
        }
    }

    if ( m3u8Job == null ) {
        m3u8Job = sharedData.scope.launch {

            val tempFolder = "${Preferences.getCacheLocation}/m3u8/"

            tempFolder.validateDir()

            Paths.get("${tempFolder}link").run {
                if (toFile().isFile) {
                    if (!readText().contains(baseLink)) {
                        Paths.get(tempFolder).toFile().run {
                            deleteRecursively()
                            mkdirs()
                        }
                    }
                } else writeText(baseLink)
            }

            Download(mrl.value, "${tempFolder}index.m3u8").run {
                while (!status) {
                    Thread.sleep(10)
                }
            }

            var duration = 0L
            Paths.get("${tempFolder}index.m3u8").readText().split("\n").forEach {
                if (it.startsWith("#EXTINF")) totalTime += (it.filter { char -> char.isDigit() || char == '.' }
                    .toFloat() * 1000).toLong().also { duration = it }
                if (!it.startsWith("#") && it.isNotBlank()) {
                    playList.add( Pair( it , duration ) )
                }
            }

            sharedData.scope.launch {
                while ( downloaderIndex in 0 until  playList.size ) {
                    playList[downloaderIndex].first.let { name ->
                        if (!"$tempFolder${name}".toFile().isFile) Download(
                            "$baseLink$name",
                            "$tempFolder$name"
                        )
                    }
                    downloaderIndex++
                }
            }

           sharedData.scope.launch {
               while ( playerIndex in 0 until  playList.size ) {
                   progress.value = (playerIndex.toFloat()/playList.size.toFloat())
                   playList[playerIndex].let { ( name , duration ) ->
                       playerIndex++
                       if ( ! "$tempFolder$name".toFile().isFile ) Download( "$baseLink$name" , "$tempFolder$name" ).also {
                           buffering = true
                           while (!it.status) continue
                       }
                       buffering = false
                       sharedData.embeddedMediaListPlayerComponent.mediaPlayer().media().play( "$tempFolder$name" )
                       val playerIndexSnapshot = playerIndex
                       for ( i in 0 until duration step 500 ) {
                           if ( playerIndexSnapshot != playerIndex ) break
                           delay( 500 )
                       }
                   }
               }

           }

        }
    }

}

@Composable
private fun BasicVideoPlayer( mrl : MutableState<String> , sharedData: AppData ) {
    VideoSurface( mrl.value , sharedData )
}
