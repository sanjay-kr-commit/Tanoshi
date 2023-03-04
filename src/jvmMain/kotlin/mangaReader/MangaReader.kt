package mangaReader

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.onClick
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import mangaReader.type.RTL
import mangaReader.type.Webtoon
import shared.AppData
import tanoshi.source.api.model.component.MangaPage
import ui.component.Loading

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MangaReader( sharedData : AppData ) = sharedData.manga.run {

    var fetchingChapterList by remember { mutableStateOf( true ) }
    val mangaPageList = remember { mutableStateOf( emptyList<MangaPage>() ) }
    var selected by remember { mutableStateOf( "RTL" ) }

    Box( Modifier.fillMaxSize().background(Color.Black) , contentAlignment = Alignment.Center ) {
        when {
            // check if the page list is empty
            fetchingChapterList -> {
                Loading(backgroundColor = Color.Transparent)
                sharedData.scope.launch {
                    mangaPageList.value = sharedData.manga.selected.value!!.fetchPageList(chapter!!)
                    fetchingChapterList = false
                }
            }
            selected == "webtoon" -> {
                Webtoon( sharedData , mangaPageList.value )
            }
            selected == "RTL" -> {
                RTL(sharedData, mangaPageList.value)
            }
        }

        Box( Modifier.fillMaxSize().padding( 20.dp ).background( Color.Transparent ) , contentAlignment = Alignment.BottomStart ) {
            var isExpanded by remember { mutableStateOf( false ) }
            Text( "$selected mode" , modifier = Modifier.background( Color.Black ).onClick { isExpanded = true } , color = Color.White )
            DropdownMenu( expanded = isExpanded , onDismissRequest = { isExpanded = false } ) {
                DropdownMenuItem( enabled = true , onClick = {
                    selected = "webtoon"
                    isExpanded = false
                }, content = {
                    Text( "Webtoon" )
                } )
                DropdownMenuItem( enabled = true , onClick = {
                    selected = "RTL"
                    isExpanded = false
                }, content = {
                    Text( "RTL" )
                } )
            }
        }
    }

}