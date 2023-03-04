package videoPlayer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Maximize
import androidx.compose.material.icons.filled.Minimize
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import logic.downloader.Download
import logic.helper.Preferences
import shared.AppData
import tanoshi.lib.util.validateDir
import tanoshi.source.api.model.component.Video
import ui.component.Loading
import ui.component.Text
import ui.component.TextStyling
import java.nio.file.Paths
import kotlin.io.path.*
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VideoPlayer( sharedData: AppData ) = sharedData.run {

    var mrl by remember { mutableStateOf( "" ) }
    var m3u8Job : Job? = remember { null }
    var updateProgress: Job?
    var controlHidden by remember { mutableStateOf( true ) }
    var isPlaying : Boolean by remember { mutableStateOf( true ) }
    var progess : Float by remember { mutableStateOf( 0f ) }
    var time : Long by remember { mutableStateOf( 0L ) }
    var totalTime : String  by remember { mutableStateOf( "" ) }
    var fetchingLinks : Boolean by remember { mutableStateOf( true ) }
    var links by remember { mutableStateOf( emptyList<Video>() ) }
    var m3u8 : Boolean by mutableStateOf( mrl.endsWith( ".m3u8" ) && mrl != "${Preferences.getCacheLocation}/m3u8/index.m3u8" )

    if ( links.isEmpty() ) {
        scope.launch {
            links = anime.selected.value!!.fetchEpisodeLink(anime.episode!!)
            if ( links.isEmpty()) navController.navigateBack()
            else mrl = links.filter { it.quality != "auto" }.first().url
            fetchingLinks = false
        }
    }

    if ( m3u8 ) {
            scope.launch {
                m3u8Job?.let { it.cancel() }
                val tempFolder = "${Preferences.getCacheLocation}/m3u8/"
                val baseLink = mrl.substring(0, mrl.lastIndexOf("/") + 1)

                tempFolder.validateDir()

                Paths.get("${tempFolder}link").run {
                    if (toFile().isFile) {
                        if ( !readText().contains( baseLink ) ) {
                            Paths.get( tempFolder ).toFile().run{
                                deleteRecursively()
                                mkdirs()
                            }
                        }
                    } else writeText(baseLink)
                }

                println("Downloading index.m3u8")

                Download(mrl, "${tempFolder}index.m3u8").run {
                    while (!status) {
                        Thread.sleep(10)
                    }
                }

                mrl = "${tempFolder}index.m3u8"

                println( "wtf $mrl" )

                val parts = Paths.get("${tempFolder}index.m3u8").readText().split("\n").filter { !it.startsWith("#") }

                m3u8Job = scope.launch {

                    for (i in parts) {
                        println(i)
                        if (Paths.get("$tempFolder$i").exists()) {
                            println("File Already Exists")
                            continue
                        }
                        Download("$baseLink$i", "$tempFolder$i")

                    }
                }
                m3u8 = false
            }
    }

    if ( fetchingLinks || m3u8 ) Box( Modifier.background( Color.Black ) ){ Loading() }
    else Box(
        Modifier.fillMaxSize()
            .onClick {
                controlHidden = !controlHidden
                println( controlHidden )
            }
    ) {
        VideoSurface( mrl , sharedData )
        totalTime = (mediaPlayer.status().length()/1000).run { "${this/100}:${this%100}" }
        if (!controlHidden) {
            Column( Modifier.fillMaxSize()  ) {
                Row( Modifier.weight( 1f ).fillMaxWidth() , horizontalArrangement = Arrangement.Center ) {
                    Text( title , TextStyling(color = Color.Red) )
                }
                Row( Modifier.weight( 8f ).fillMaxSize() , horizontalArrangement = Arrangement.Center , verticalAlignment = Alignment.CenterVertically ){
                    Image(
                        if ( isPlaying ) Icons.Filled.Pause
                        else Icons.Filled.PlayArrow ,"" ,
                        modifier = Modifier.size( 100.dp ).onClick {
                            if ( mediaPlayer.status().isPlaying ) mediaPlayer.controls().pause()
                            else mediaPlayer.controls().play()
                        },
                        colorFilter = ColorFilter.tint( color = Color.Red )
                    )
                }
                Row( Modifier.weight( 1f ).fillMaxWidth() , horizontalArrangement = Arrangement.Center ) {
                    Text( time.run { "\n${this/100}:${this%100}" } , TextStyling( modifier = Modifier.weight(1f), color = Color.Red ) )
                    Slider(
                        progess ,
                        onValueChange = {
                            mediaPlayer.controls().setPosition( (it*100).toInt().toFloat()/100 )
                        } ,
                        modifier = Modifier.weight( 8f ) ,
                        colors = SliderDefaults.colors(
                            activeTrackColor = Color.Red ,
                            inactiveTrackColor = Color.DarkGray ,
                            disabledThumbColor = Color.Transparent ,
                            thumbColor = Color.Transparent
                        )
                    )
                    Text( "\n$totalTime" , TextStyling( modifier = Modifier.weight(1f), color = Color.Red ) )
                }
                Row( Modifier.weight( 1f ).fillMaxWidth() , horizontalArrangement = Arrangement.SpaceEvenly ) {
                    Image(
                        if ( appState.placement != WindowPlacement.Fullscreen ) Icons.Filled.Maximize
                        else Icons.Filled.Minimize
                        , "" ,
                        colorFilter = ColorFilter.tint( Color.Red ) ,
                        modifier = Modifier.onClick {
                            if ( appState.placement != WindowPlacement.Fullscreen ) appState.placement = WindowPlacement.Fullscreen
                            else appState.placement = WindowPlacement.Floating
                            navigationBarHidden.value = !navigationBarHidden.value
                        }
                    )
                }
                Box( Modifier.fillMaxSize().padding( 20.dp ).background( Color.Transparent ) , contentAlignment = Alignment.BottomStart ) {
                    var isExpanded by remember { mutableStateOf( false ) }
                    Text( "change link" , modifier = Modifier.background( Color.Black ).onClick { isExpanded = true } , color = Color.White )
                    DropdownMenu( expanded = isExpanded , onDismissRequest = { isExpanded = false } ) {
                        links.forEach {
                            DropdownMenuItem( enabled = true , onClick = {
                                mrl = it.url
                                isExpanded = false
                            }, content = {
                                androidx.compose.material.Text( it.quality )
                            } )
                        }
                    }
                }
            }
        }
    }

    if ( !m3u8 && !fetchingLinks ) {
        updateProgress = scope.launch {
            while (true) {
                delay(100)
                isPlaying = mediaPlayer.status().isPlaying
                progess = mediaPlayer.status().position()
                time = mediaPlayer.status().time() / 1000
            }
        }

        DisposableEffect(key1 = mrl, effect = {
            this.onDispose {
                m3u8Job?.let { it.cancel() }
                updateProgress?.let { it.cancel() }
                navigationBarHidden.value = false
                appState.placement = WindowPlacement.Floating
            }
        })
    }

}