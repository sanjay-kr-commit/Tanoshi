package ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import logic.helper.Preferences
import logic.helper.resolveCoverImage
import navigation.NavigationScreens
import preferences.Colors
import preferences.Spacing
import shared.AppData
import tanoshi.source.api.model.component.Anime
import tanoshi.source.api.model.component.Manga
import tanoshi.source.api.model.component.Novel
import ui.component.LoadImage
import ui.component.Loading
import ui.component.Text
import ui.component.TextStyling
import java.nio.file.Paths
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.readText

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Bookmark( sharedData : AppData ) {

    val animeList = remember { mutableListOf<Anime>() }
    val mangaList = remember { mutableListOf<Manga>() }
    val novelList = remember { mutableListOf<Novel>() }
    var loading by remember { mutableStateOf( true ) }

    if ( animeList.isEmpty() ) {
        loading = true
        try {
            Paths.get("${Preferences.getDataLocation}/Anime/").listDirectoryEntries().forEach {
                try {
                    animeList.add(Gson().fromJson(it.readText(), Anime::class.java))
                } catch ( a : Exception ) {
                    println( a )
                }
            }
        }catch (_:Exception){}
            loading = false
    }

    if ( mangaList.isEmpty() ) {
        loading = true
        try {
            Paths.get("${Preferences.getDataLocation}/Manga/").listDirectoryEntries().forEach {
                try {
                    mangaList.add(Gson().fromJson(it.readText(), Manga::class.java))
                } catch ( a : Exception ) {
                    println( a )
                }
            }
        }catch (_:Exception){}
        loading = false
    }

    if ( novelList.isEmpty() ) {
        loading = true
        try {
            Paths.get("${Preferences.getDataLocation}/Novel/").listDirectoryEntries().forEach {
                try {
                    novelList.add(Gson().fromJson(it.readText(), Novel::class.java))
                } catch ( a : Exception ) {
                    println( a )
                }
            }
        }catch (_:Exception){}
        loading = false
    }

    if ( loading ) Loading()
    else Box( Modifier.fillMaxSize() , Alignment.Center ){

        LazyVerticalGrid(
            columns = GridCells.Adaptive( minSize = 150.dp ) ,
            modifier = Modifier.fillMaxSize()
        ) {
            animeList.forEach{ anime ->

                item {
                    Box( modifier = Modifier
                        .width(100.dp)
                        .height(250.dp)
                        .padding(2.dp)
                        .clip(RoundedCornerShape(Spacing.Roundness.value))
                        .background(color = Colors.StartScreenBackground.value)
                        .padding(3.dp)
                        .clip(RoundedCornerShape(Spacing.Roundness.value))
                        , contentAlignment = Alignment.BottomCenter ) {
                        LoadImage(
                            anime.resolveCoverImage(),
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(Spacing.Roundness.value))
                                .background(color = Colors.StartScreenBackground.value)
                                .clip(RoundedCornerShape(Spacing.Roundness.value))
                                .onClick {
                                    sharedData.anime.selected.value = sharedData.anime.sources[anime.extensionName]!!
                                    sharedData.anime.selectedEntry = anime
                                    sharedData.navController.navigate( NavigationScreens.AnimeDetail.name )
                                },
                            contentScale = ContentScale.Crop,
                        )
                        Row( Modifier.background( Color.Black ).fillMaxWidth() ) {
                            Text( anime.title , TextStyling( color = Color.White ) )
                        }
                        Box(Modifier.fillMaxSize() ) {
                            Row( Modifier.padding( 5.dp ).clip( RoundedCornerShape( 5.dp )).background( Color.Black ).padding( 5.dp ) ) { Text( "Anime" , TextStyling( color = Color.White ) ) }
                        }
                    }
                }
            }
            mangaList.forEach { manga ->

                item {
                    Box( modifier = Modifier
                        .width(100.dp)
                        .height(250.dp)
                        .padding(2.dp)
                        .clip(RoundedCornerShape(Spacing.Roundness.value))
                        .background(color = Colors.StartScreenBackground.value)
                        .padding(3.dp)
                        .clip(RoundedCornerShape(Spacing.Roundness.value))
                        , contentAlignment = Alignment.BottomCenter ) {
                        LoadImage(
                            manga.resolveCoverImage(),
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(Spacing.Roundness.value))
                                .background(color = Colors.StartScreenBackground.value)
                                .clip(RoundedCornerShape(Spacing.Roundness.value))
                                .onClick {
                                    sharedData.manga.selected.value = sharedData.manga.sources[manga.extensionName]!!
                                    sharedData.manga.selectedEntry = manga
                                    sharedData.navController.navigate( NavigationScreens.MangaDetail.name )
                                },
                            contentScale = ContentScale.Crop,
                        )
                        Row( Modifier.background( Color.Black ).fillMaxWidth() ) {
                            Text( manga.title , TextStyling( color = Color.White ) )
                        }
                        Box(Modifier.fillMaxSize() ) {
                            Row( Modifier.padding( 5.dp ).clip( RoundedCornerShape( 5.dp )).background( Color.Black ).padding( 5.dp ) ) { Text( "Manga" , TextStyling( color = Color.White ) ) }
                        }
                    }
                }

            }
            novelList.forEach { novel ->
                item {
                    Box( modifier = Modifier
                        .width(100.dp)
                        .height(250.dp)
                        .padding(2.dp)
                        .clip(RoundedCornerShape(Spacing.Roundness.value))
                        .background(color = Colors.StartScreenBackground.value)
                        .padding(3.dp)
                        .clip(RoundedCornerShape(Spacing.Roundness.value))
                        , contentAlignment = Alignment.BottomCenter ) {
                        LoadImage(
                            novel.resolveCoverImage(),
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(Spacing.Roundness.value))
                                .background(color = Colors.StartScreenBackground.value)
                                .clip(RoundedCornerShape(Spacing.Roundness.value))
                                .onClick {
                                    sharedData.novel.selected.value = sharedData.novel.sources[novel.extensionName]!!
                                    sharedData.novel.selectedEntry = novel
                                    sharedData.navController.navigate( NavigationScreens.NovelDetail.name )
                                },
                            contentScale = ContentScale.Crop,
                        )
                        Row( Modifier.background( Color.Black ).fillMaxWidth() ) {
                            Text( novel.title , TextStyling( color = Color.White ) )
                        }
                        Box(Modifier.fillMaxSize() ) {
                            Row( Modifier.padding( 5.dp ).clip( RoundedCornerShape( 5.dp )).background( Color.Black ).padding( 5.dp ) ) { Text( "Novel" , TextStyling( color = Color.White ) ) }
                        }
                    }

                }
            }
        }

    }

    DisposableEffect( key1 = null ,effect = {
        this.onDispose {
            animeList.clear()
            mangaList.clear()
            novelList.clear()
        }
    })

}