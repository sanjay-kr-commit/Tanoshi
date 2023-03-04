package mangaReader.type

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import kotlinx.coroutines.*
import logic.helper.resolveMangaPage
import shared.AppData
import tanoshi.source.api.model.component.MangaPage
import ui.component.LoadImage
import ui.component.Loading
import ui.component.Text
import ui.component.TextStyling

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RTL( sharedData : AppData , pageList : List<MangaPage> ) = sharedData.manga.run {

    // window size
    val size = remember { mutableStateOf( IntSize.Zero ) }
    var fetchingPage by remember { mutableStateOf( true ) }
    var index by remember { mutableStateOf( 1 ) }
    var page by remember { mutableStateOf("") }
    var job : Job? = remember { null }
    val mangaPageList = remember { pageList }

    Box(
        Modifier
            .fillMaxSize()
            .background( Color.Black )
            .padding( 10.dp )
            .onSizeChanged { size.value = it }
    ){

        when {

            // end of the chapter
            index > mangaPageList.size -> {

                Box( Modifier.fillMaxSize() , contentAlignment = Alignment.Center ){
                    Text( "End of the Chapter" , TextStyling( color = Color.White ) )
                }

            }

            // show loading when fetching page
            fetchingPage -> {
                Box( Modifier.fillMaxSize() , contentAlignment = Alignment.Center ){
                    Loading( Color.Transparent )
                }
                job?.let { it.cancel() }
                job = sharedData.scope.launch {
                    delay(10)
                    page = selectedEntry!!.resolveMangaPage( mangaPageList , index )
                    fetchingPage = false
                }
            }

            // reader
            else -> {

                Box( Modifier.fillMaxSize() ) {
                    // page view
                    Box( Modifier.fillMaxSize() , contentAlignment = Alignment.Center ){
                        LoadImage( page , modifier = Modifier.width(
                            size.value.width.dp
                        ).height(
                            size.value.height.dp
                        ))
                    }

                }

            }
            // end of reader

        }

        // Page Index
        if ( index <= mangaPageList.size )Box( Modifier.padding( 10.dp ).background( Color.Black ).padding( 10.dp ) ) {
            Text( "$index/${mangaPageList.size}" , TextStyling( color = Color.White ) )
        }

        // controls
        Row( Modifier.fillMaxSize() ) {
            Column( Modifier.weight( 1f ).fillMaxSize()
                .onClick {
                    if ( index <= mangaPageList.size ) {
                        index++
                        fetchingPage = true
                    } else {
                        sharedData.navController.navigateBack()
                    }
                }
            ) {}
            Column( Modifier.weight(1f).fillMaxSize() ){}
            Column( Modifier.weight( 1f ).fillMaxSize()
                .onClick {
                    if (index > 1) {
                        index--
                        fetchingPage = true
                    } else {
                        sharedData.navController.navigateBack()
                    }
                } ,
                horizontalAlignment = Alignment.End
            ) {
                Image( Icons.Filled.Fullscreen , "" , colorFilter = ColorFilter.tint( Color.White ) ,modifier = Modifier.onClick {
                    if ( sharedData.appState.placement != WindowPlacement.Fullscreen ) {
                        sharedData.appState.placement = WindowPlacement.Fullscreen
                        sharedData.navigationBarHidden.value = true
                    } else {
                        sharedData.appState.placement = WindowPlacement.Floating
                        sharedData.navigationBarHidden.value = false
                    }
                } )
            }
        }

    }

    DisposableEffect( key1 = null , effect = {
        this.onDispose {
            sharedData.appState.placement = WindowPlacement.Floating
            sharedData.navigationBarHidden.value = false
        }
    } )

}