package ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import logic.helper.resolveCoverImage
import navigation.NavigationScreens
import shared.AppData
import tanoshi.lib.exception.PageIndexOutOfTheBoundException
import tanoshi.source.api.annotation.IMPLEMENTED
import tanoshi.source.api.annotation.TAB
import tanoshi.source.api.model.component.Manga
import tanoshi.source.api.model.component.Novel
import ui.component.*

@Composable
fun Manga( sharedData : AppData ) = sharedData.manga.run {


    val loading = remember { mutableStateOf( false ) }

    Column {
        Box(Modifier.wrapContentHeight()) {
            MangaExtensionList(sharedData)
        }
        if ( selected.value != null ) {
            if ( selected.value != null ) MangaTabs( sharedData , loading )
            if ( selectedTab == null ) Box( Modifier.wrapContentHeight() ) {
                SearchBarWithButton(query ,
                    onSearchIconClick = {
                        result.value = listOf()
                        pageIndex = 1
                        nextPageNotFound.value = false
                        if ( query.value.trim().isNotEmpty() ) sharedData.scope.launch {
                            loading.value = true
                            result.value = selected.value!!.search(query.value)
                            for ( i in result.value ) i.resolveCoverImage()
                            loading.value = false
                        }
                    } , {
                        result.value = listOf()
                        pageIndex = 1
                        nextPageNotFound.value = false
                    } )
            }
        }
        if ( loading.value ) Box( contentAlignment = Alignment.Center ){ Loading( backgroundColor = Color.Transparent ) }
        else if ( result.value.isNotEmpty() ) MangaGrid( sharedData )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MangaExtensionList( sharedData : AppData ) = sharedData.manga.run {
    if ( sources.isEmpty() ) Text( "No Novel Extension Found" )
    else LazyRow( Modifier.padding( 5.dp ) ) {
        sources.forEach { (_, extension) ->
            item {
                Box(
                    Modifier.padding( 2.dp )
                        .clip( RoundedCornerShape( 5.dp ) )
                        .background(
                            if ( selected.value == extension ) Color.Black
                            else Color.DarkGray
                        )
                        .padding( 2.dp )
                        .onClick {
                            if ( selected.value == extension ) selected.value = null
                            else selected.value = extension
                        }
                ) {
                    Text( "${extension.name} [ ${extension.lang} ]" , color = Color.White )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MangaGrid( sharedData: AppData ) = sharedData.manga.run {
    var loading by remember { mutableStateOf( false ) }
    LazyVerticalGrid(
        columns = GridCells.Adaptive( minSize = 150.dp )
    ){
        items( result.value ) { manga ->

            Box( modifier = Modifier
                .width(100.dp)
                .height(250.dp)
                .padding(2.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(color = Color.Black)
                .padding(3.dp)
                .clip(RoundedCornerShape(5.dp))
                , contentAlignment = Alignment.BottomCenter ) {
                LoadImage(
                    manga.resolveCoverImage(),
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(5.dp))
                        .background(color = Color.Black)
                        .clip(RoundedCornerShape(5.dp))
                        .onClick {
                            selectedEntry = manga
                            sharedData.navController.navigate(NavigationScreens.MangaDetail.name)
                        },
                    contentScale = ContentScale.Crop,
                )
                Row( Modifier.background( Color.Black ).fillMaxWidth() ) {
                    Text( manga.title , TextStyling( color = Color.White ) )
                }
            }
            manga.lang?.let {
                Box(Modifier.padding(5.dp).background(Color.Black).padding(3.dp)) {
                    Text(it , color = Color.White)
                }
            }

        }
        if ( loading ) {
            item {
                Box( Modifier
                    .width(100.dp)
                    .height(250.dp)
                    .padding( 10.dp )
                    .clip( RoundedCornerShape( 10.dp ) )
                    .background( Color.Black )
                    .fillMaxSize()
                ){ Loading() }
            }
        }
        if ( !loading && !nextPageNotFound.value ) item {
            Box(
                Modifier
                    .width(100.dp)
                    .height(250.dp)
                    .padding( 10.dp )
                    .clip( RoundedCornerShape( 10.dp ) )
                    .background( Color.Black )
                    .fillMaxSize()
                    .onClick {
                        sharedData.scope.launch {
                            loading = true
                            try {
                                result.value = arrayListOf<Manga>().apply {
                                    addAll(result.value)
                                    if ( selectedTab == null ) {
                                        selected.value!!.search(query.value, ++pageIndex).let {
                                            for (i in it) i.resolveCoverImage()
                                            addAll(it)
                                        }
                                    } else {
                                        (selected.value!!::class.java.getMethod(selectedTab!!, Int::class.java).invoke(
                                            selected.value,
                                            ++pageIndex
                                        ) as List<*>).forEach { content ->
                                            add(content as Manga)
                                        }
                                    }

                                }
                            } catch (_: PageIndexOutOfTheBoundException){
                                nextPageNotFound.value = true
                            }
                            loading = false
                        }
                    } ,
                contentAlignment = Alignment.Center
            ) {
                Text( "Load More" , color = Color.White )
            }

        }

    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MangaTabs( sharedData: AppData , loading : MutableState<Boolean> ) = sharedData.manga.run {

    var firstTap : Job? = remember { null }

    selected.value?.let { extension ->
        extension::class.java.methods.filter { it.annotations.filterIsInstance<TAB>().isNotEmpty() }.let { tabList ->
            if ( tabList.isNotEmpty() ) LazyRow(Modifier.padding(5.dp).wrapContentSize()) {
                tabList.forEach { method ->
                    if (extension::class.java.getMethod(
                            method.name,
                            Int::class.java
                        ).annotations.filterIsInstance<IMPLEMENTED>().isNotEmpty()
                    ) item {
                        Box(
                            Modifier.padding(2.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(
                                    if (selectedTab == method.name) Color.Black
                                    else Color.DarkGray
                                )
                                .padding(2.dp)
                                .onClick {
                                    if (selectedTab == method.name) {
                                        result.value = emptyList()
                                        selectedTab = null
                                    } else {
                                        selectedTab = method.name
                                        firstTap?.cancel()
                                        firstTap = sharedData.scope.launch {
                                            loading.value = true
                                            pageIndex = 1
                                            val retrievedData = ArrayList<Manga>()
                                            (extension::class.java.getMethod(method.name, Int::class.java).invoke(
                                                extension,
                                                pageIndex
                                            ) as List<*>).forEach { content ->
                                                retrievedData.add(content as Manga)
                                            }
                                            result.value = retrievedData
                                            loading.value = false
                                        }
                                    }
                                }
                        ) {
                            Text(method.name, color = Color.White)
                        }
                    }
                }
            }
        }
    }

}