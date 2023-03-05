package videoPlayer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
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
    val controlHidden = remember { mutableStateOf( true ) }
    var isPlayerHidden by remember { mutableStateOf( false ) }

    val isLibVlcPresent = remember {
        try{
            MediaPlayerFactory()
            true
        } catch ( _ : LinkageError ) {
            false
        }
    }

    if ( isLibVlcPresent && links.isEmpty() ) {
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
        else Box( Modifier.fillMaxSize().onClick { controlHidden.value = !controlHidden.value } ) {
            Box(Modifier.fillMaxSize().background( Color.Black ) ) {
                if ( !isPlayerHidden ) {
                    if (mrl.value.endsWith(".m3u8")) M3U8VideoPlayer(mrl, sharedData, controlHidden)
                    else BasicVideoPlayer(mrl, sharedData)
                }
            }
            if ( !controlHidden.value ) Box( Modifier.fillMaxSize().padding( 30.dp ) , contentAlignment = Alignment.TopStart ) {
                var isExpanded by remember { mutableStateOf( false ) }

                Row( verticalAlignment = Alignment.CenterVertically ) {
                    Image(
                        Icons.Filled.Settings , "Change Resolution" ,
                        colorFilter = ColorFilter.tint( Color.White ) ,
                        modifier = Modifier.size( 30.dp )
                            .onClick {
                                isExpanded = !isExpanded
                            }
                    )
                    Spacer( Modifier.width( 10.dp ) )
                    Image(
                        if ( sharedData.appState.placement != WindowPlacement.Fullscreen ) Icons.Filled.Fullscreen
                        else Icons.Filled.FullscreenExit
                        , "" , colorFilter = ColorFilter.tint(Color.White) ,
                        modifier = Modifier.size( 30.dp )
                            .onClick {
                            if ( sharedData.appState.placement != WindowPlacement.Fullscreen ) {
                                sharedData.appState.placement = WindowPlacement.Fullscreen
                                sharedData.navigationBarHidden.value = true
                            } else {
                                sharedData.appState.placement = WindowPlacement.Floating
                                sharedData.navigationBarHidden.value = false
                            }
                        }
                    )
                    Spacer( Modifier.width( 10.dp ) )
                    Image( Icons.Filled.Crop ,
                        "" ,
                        colorFilter = ColorFilter.tint( Color.White ),
                        modifier = Modifier
                            .size( 30.dp )
                            .onClick {
                                sharedData.contentScale.value = when ( sharedData.contentScale.value ) {
                                    ContentScale.Fit -> ContentScale.FillBounds
                                    ContentScale.FillBounds -> ContentScale.Crop
                                    ContentScale.Crop -> ContentScale.FillHeight
                                    ContentScale.FillHeight -> ContentScale.FillWidth
                                    ContentScale.FillWidth -> ContentScale.Inside
                                    else -> ContentScale.Fit
                                }
                            }
                    )
                    Spacer( Modifier.width( 10.dp ) )
                    Text(
                        when ( sharedData.contentScale.value ) {
                            ContentScale.Fit -> "Fit"
                            ContentScale.Crop -> "Cropped"
                            ContentScale.FillBounds -> "Stretched"
                            ContentScale.FillHeight -> "Fit Height"
                            ContentScale.FillWidth -> "Fit Width"
                            else -> "Original"
                        }
                        , color = Color.White )
                }

                DropdownMenu( expanded = isExpanded , onDismissRequest = { isExpanded = false } ) {
                    links.forEach { video ->
                        DropdownMenuItem( enabled = true , onClick = {
                            mrl.value = video.url
                            sharedData.scope.launch {
                                isPlayerHidden = true
                                delay( 100 )
                                isPlayerHidden = false
                            }
                            isExpanded = false
                        }, content = {
                            if ( mrl.value == video.url ) Text("*${video.quality}" )
                            else Text( video.quality )
                        } )
                    }
                }
            }
        }
    }

    DisposableEffect( key1 = null , effect = {
        this.onDispose {
            sharedData.appState.placement = WindowPlacement.Floating
            sharedData.navigationBarHidden.value = false
        }
    })

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun M3U8VideoPlayer( mrl : MutableState<String> , sharedData: AppData , isControlHidden : MutableState<Boolean> ) {

    var m3u8Job : Job? by remember { mutableStateOf( null ) }
    var bufferJob : Job? by remember { mutableStateOf( null ) }
    var playerJob : Job? by remember { mutableStateOf( null ) }
    val playList = remember { ArrayList<Pair<String,Long>>() }
    var totalTime by remember { mutableStateOf( 0L ) }
    val baseLink = remember { mrl.value.substring(0, mrl.value.lastIndexOf("/") + 1) }
    val progress = remember { mutableStateOf( 0f ) }
    var downloaderIndex by remember { mutableStateOf( 0 ) }
    var playerIndex by remember { mutableStateOf( 0 ) }
    var buffering by remember { mutableStateOf( true ) }
    var playTime by remember { mutableStateOf( 0L ) }
    var isPlaying by remember { mutableStateOf( true ) }

    Box( Modifier.fillMaxSize() ){
        Box( Modifier.fillMaxSize() ){ VideoSurface( "" , sharedData ) }
        if ( !isControlHidden.value ) Box( Modifier.fillMaxSize().padding( 30.dp ) , contentAlignment = Alignment.BottomCenter ) {
            Box ( modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center )  {
                if (buffering) Loading( backgroundColor = Color.Transparent )
                else Image(
                    if ( isPlaying ) Icons.Filled.PauseCircleOutline
                    else Icons.Filled.PlayCircleOutline ,
                    "" ,
                    colorFilter = ColorFilter.tint( Color.White ) ,
                    modifier = Modifier
                        .size(100.dp)
                        .onClick {
                            if (isPlaying ) sharedData.embeddedMediaListPlayerComponent.mediaPlayer().controls().pause()
                            else sharedData.embeddedMediaListPlayerComponent.mediaPlayer().controls().play()
                        isPlaying = !isPlaying
                    }
                )
            }
            Row( verticalAlignment = Alignment.CenterVertically , horizontalArrangement = Arrangement.SpaceEvenly ) {
                Text( (playTime/1000).let { "${it/60}:${it%60}" } , modifier = Modifier.weight( 1.5f ).fillMaxWidth() , textAlign = TextAlign.Center  , color = Color.White )
                Slider(progress.value, steps = playList.size, onValueChange = {
                    sharedData.embeddedMediaListPlayerComponent.mediaPlayer().controls().pause()
                    (it * playList.size).toInt().let { requestedIndex ->
                        progress.value = requestedIndex.toFloat() / playList.size
                        downloaderIndex = requestedIndex
                        playerIndex = requestedIndex
                        playTime = 0
                        for ( i in 0 until requestedIndex ) playTime += playList[i].second
                    }
                } , modifier = Modifier.weight( 9f ) ,
                    colors = SliderDefaults.colors(thumbColor = Color.Transparent , inactiveTrackColor = Color.DarkGray , activeTrackColor = Color.White , activeTickColor = Color.Transparent , inactiveTickColor = Color.Transparent )
                )
                Text( totalTime.let { "${it/60}:${it%60}" } , modifier = Modifier.weight( 1.5f ).fillMaxWidth() , textAlign = TextAlign.Center , color = Color.White )
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
            totalTime /= 1000

            bufferJob = sharedData.scope.launch {
                while ( downloaderIndex in 0 until  playList.size ) {
                    while ( !isPlaying ) delay( 10 )
                    playList[downloaderIndex].first.let { name ->
                        if (!"$tempFolder${name}".toFile().isFile) Download(
                            "$baseLink$name",
                            "$tempFolder$name"
                        )
                    }
                    downloaderIndex++
                }
            }

           playerJob = sharedData.scope.launch {
               while ( playerIndex in 0 until  playList.size ) {
                   while ( !isPlaying ) delay( 10 )
                   progress.value = (playerIndex.toFloat()/playList.size.toFloat())
                   playList[playerIndex].let { ( name , duration ) ->
                       playerIndex++
                       buffering = true
                       if ( ! "$tempFolder$name".toFile().isFile ) Download( "$baseLink$name" , "$tempFolder$name" ).also {
                           while (!it.status) continue
                       }
                       buffering = false
                       sharedData.embeddedMediaListPlayerComponent.mediaPlayer().media().play( "$tempFolder$name" )
                       val playerIndexSnapshot = playerIndex
                       for ( i in 0 until duration step 100 ) {
                           while ( !isPlaying ) delay( 10 )
                           if ( playerIndexSnapshot != playerIndex ) break
                           delay( 100 )
                           playTime += 100
                       }
                   }
               }
           }

        }
    }

    DisposableEffect( key1 = null , effect = {
        this.onDispose {
            m3u8Job?.cancel()
            bufferJob?.cancel()
            playerJob?.cancel()
        }
    })

}

@Composable
private fun BasicVideoPlayer( mrl : MutableState<String> , sharedData: AppData ) {
    VideoSurface( mrl.value , sharedData )
}
