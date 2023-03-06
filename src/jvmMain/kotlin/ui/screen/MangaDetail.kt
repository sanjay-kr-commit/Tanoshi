package ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import kotlinx.coroutines.launch
import logic.helper.Preferences
import logic.helper.resolveCoverImage
import navigation.NavigationScreens
import shared.AppData
import ui.component.LoadImage
import ui.component.Loading
import ui.component.Text
import ui.component.TextStyling
import java.nio.file.Paths
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.writeText

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MangaDetail( sharedData : AppData ) = sharedData.manga.run {
    val rowSize = remember { mutableStateOf( IntSize.Zero ) }
    var error by remember { mutableStateOf( "" ) }
    var fetchDetails by remember { mutableStateOf(
        selectedEntry!!.description == null
    ) }
    var isBookmarked by remember { mutableStateOf( Paths.get( "${Preferences.getDataLocation}/Manga/${selectedEntry!!.title}.json" ).exists() ) }

    if ( fetchDetails ) {
        sharedData.scope.launch {
            selected.value!!.fetchDetails( selectedEntry!! )
            fetchDetails = false
        }
    }
    Column {
        Box(Modifier.padding(10.dp).weight(1f).fillMaxSize()) {

            Column( Modifier.fillMaxSize() ) {
                Row( Modifier.weight( 1f ).fillMaxSize()
                    .clip( RoundedCornerShape( 10.dp ) )
                    .background( Color.LightGray ) ) {  }
                Spacer( Modifier.height( 2.dp ) )
                Row( Modifier.weight( 1f ).fillMaxSize()
                    .clip( RoundedCornerShape( 10.dp ) )
                    .background( Color.DarkGray ) ) {  }
            }

            Row( Modifier.fillMaxSize()
                .onSizeChanged { rowSize.value = it }
                , verticalAlignment = Alignment.CenterVertically
            ) {

                Spacer( Modifier.width( 50.dp ) )

                Column( Modifier.weight( 1f ) ) {

                    Box( Modifier
                        .height( rowSize.value.height.dp )
                        .width( (rowSize.value.height/2).dp )
                        .fillMaxSize()
                        .clip( RoundedCornerShape( 10.dp ) )
                    ) {
                        LoadImage( selectedEntry!!.resolveCoverImage() ,
                            modifier = Modifier
                                .height( rowSize.value.height.dp )
                                .width( (rowSize.value.height/2).dp )
                        )
                    }

                }

                Spacer( Modifier.width( 10.dp ) )

                Column( Modifier.weight( 2f ).fillMaxSize() ) {
                    Column( Modifier.weight( 1f ).fillMaxSize() ,
                        verticalArrangement = Arrangement.Center ,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val description = """|ID : ${selectedEntry!!.id}
                               |Title : ${selectedEntry!!.title}
                               |Genre : ${selectedEntry!!.genre}
                               |Lang : ${selectedEntry!!.lang}
                               |Source : ${selected.value!!.name}
                               |Source Site : ${selected.value!!.baseUrl}
                            """.trimMargin()
                        if ( fetchDetails ) Loading() else LazyRow {
                            item {
                                Text(
                                    description
                                )
                            }
                        }
                    }
                    Column( Modifier.weight( 1f ).fillMaxSize(),
                        verticalArrangement = Arrangement.Center ,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if ( fetchDetails ) Loading() else Text( "Description : ${selectedEntry!!.description}" ,
                            TextStyling( color = Color.White )
                        )
                        Image(
                            if ( isBookmarked ) Icons.Filled.BookmarkAdded
                            else Icons.Filled.BookmarkAdd
                            , "" ,
                            modifier = Modifier.padding( 5.dp ).background( Color.Black ).clip( RoundedCornerShape( 5.dp ) ).padding( 5.dp ).onClick {
                                if ( isBookmarked ){
                                    Paths.get( "${Preferences.getDataLocation}/Manga/${selectedEntry!!.title}.json" ).deleteIfExists()
                                    isBookmarked = false
                                } else {
                                    Paths.get( "${Preferences.getDataLocation}/Manga" )
                                        .also { it.toFile().also { dir -> dir.mkdirs() }.createNewFile() }
                                        .also { selectedEntry!!.extensionName = selected.value!!::class.java.name }
                                    Paths.get( "${Preferences.getDataLocation}/Manga/${selectedEntry!!.title}.json" )
                                        .writeText(Gson().toJson(selectedEntry) )
                                    isBookmarked = Paths.get( "${Preferences.getDataLocation}/Manga/${selectedEntry!!.title}.json" ).exists()
                                }
                            } ,
                            colorFilter = ColorFilter.tint( Color.White )
                        )
                    }
                }

            }
        }
        // episode list
        Box(Modifier.weight(1f) , contentAlignment = Alignment.Center) {
            var fetchEpisodeList by remember { mutableStateOf(
                try {
                    selectedEntry!!.chapterList.isEmpty()
                    false
                } catch ( _ : Exception ) {
                    true
                }
            ) }
            if ( fetchEpisodeList ) sharedData.scope.launch {
                selectedEntry!!.chapterList = selected.value!!.fetchChapterList( selectedEntry!! )
                if ( selectedEntry!!.chapterList.isEmpty() ) error = "List is Empty"
                fetchEpisodeList = false
            }
            if ( fetchEpisodeList ) Loading( Color.Transparent )
            else if (error.isNotEmpty()) {
                Box( Modifier.fillMaxSize() , contentAlignment = Alignment.Center ) {
                    Text( error , TextStyling( fontWeight = FontWeight.ExtraBold , color = Color.Black  ) )
                }
            }
            else LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 150.dp) , modifier = Modifier.fillMaxSize().padding(10.dp)) {
                selectedEntry!!.chapterList.forEach {
                    item {
                        Box( Modifier
                            .padding( 10.dp )
                            .clip( RoundedCornerShape( 5.dp ) )
                            .background( Color.Black )
                            .padding( 10.dp )
                            .onClick {
                                chapter = it
                                sharedData.navController.navigate(NavigationScreens.MangaReader.name)
                            }
                            , contentAlignment = Alignment.Center ) {
                            Text( it.chapter_number.toString() , style = TextStyling( color = Color.White ) )
                        }
                    }
                }
            }
        }
    }
}