package novelReader

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.onClick
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import kotlinx.coroutines.launch
import shared.AppData
import ui.component.Loading
import ui.component.Text
import ui.component.TextStyling

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NovelReader( sharedData : AppData) = sharedData.novel.run {

    var loading by remember { mutableStateOf( true ) }
    var content by remember { mutableStateOf( "" ) }

    if ( loading ) Box( Modifier.fillMaxSize().background( Color.Black ) , contentAlignment = Alignment.Center ){ Loading( Color.Transparent ) }
    else {
        Box(
            Modifier.fillMaxSize().background(Color.Black).padding(10.dp)
        ) {
            LazyColumn(Modifier.fillMaxSize()) {
                item {
                    Text(
                        content
                            .format(),
                        TextStyling(color = Color.White)
                    )
                }
            }
            Row( Modifier.fillMaxWidth() , horizontalArrangement = Arrangement.End ) {
                Image( Icons.Filled.Fullscreen , "" , colorFilter = ColorFilter.tint( Color.White ) , modifier = Modifier.onClick {
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

    if ( loading ) sharedData.scope.launch {
        content = selected.value!!.fetchChapterContent( chapter!! )
        loading = false
    }

    DisposableEffect( key1 = null , effect = {
        this.onDispose {
            sharedData.appState.placement = WindowPlacement.Floating
            sharedData.navigationBarHidden.value = false
        }
    })

}