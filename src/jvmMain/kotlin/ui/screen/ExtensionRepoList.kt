package ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Download
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import logic.ExtensionManager
import logic.downloader.Download
import logic.downloader.fetchJarPathFromJitpack
import logic.helper.Preferences
import shared.AppData
import tanoshi.lib.util.configDir
import tanoshi.lib.util.toFile

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun ExtensionRepoList( sharedData : AppData ) {

    val extensionList = remember {
        mutableStateListOf<String>().run{
        "$configDir/repoList.json".toFile().let {
                if ( it.isFile ) {
                    Gson().fromJson(it.readText(), MutableList::class.java).forEach { repo ->
                        if (!contains(repo as String)) add(repo)
                    }
                } else {
                    JvmName::class.java
                        .getResource("/defaultRepoList.json")
                        ?.readText().run {
                            Gson().fromJson(this, List::class.java).forEach { repo ->
                                if (!contains(repo as String)) add(repo)
                            }
                        }
                }
        }

            this
        }
    }

    var newRepo by remember { mutableStateOf( "" ) }


    Box {
        LazyColumn(
            Modifier.fillMaxSize().padding(30.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            extensionList.forEach { repo ->
                item {
                    Box(Modifier.padding(5.dp).fillMaxWidth().wrapContentHeight().clip(RoundedCornerShape(10.dp))
                        .background(Color.DarkGray).padding(20.dp)
                        .onClick {
                            extensionList.remove(repo)
                        }, contentAlignment = Alignment.CenterStart) {
                        Text(repo, color = Color.White)
                    }
                }
            }
            item {
                Box(
                    Modifier.padding(5.dp).fillMaxWidth().wrapContentHeight().clip(RoundedCornerShape(10.dp))
                        .background(Color.DarkGray).padding(20.dp), contentAlignment = Alignment.CenterStart
                ) {
                    TextField(
                        value = newRepo,
                        onValueChange = {
                            newRepo = it
                        },
                        singleLine = true,
                        textStyle = TextStyle(color = Color.White),
                        modifier = Modifier.fillMaxSize().onKeyEvent {
                            if (it.key == Key.Enter && newRepo.trim().isNotBlank()) {
                                if (!extensionList.contains(newRepo)) extensionList.add(newRepo)
                                newRepo = ""
                                return@onKeyEvent true
                            }
                            false
                        },
                        label = {
                            Text("Add jitpack Repo or URL of Jar", color = Color.LightGray)
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
        Box( Modifier.fillMaxSize().padding( 30.dp ) , contentAlignment = Alignment.BottomEnd ){
            var helpText by remember { mutableStateOf( "" ) }
            Row {
                Image(
                    Icons.Filled.Clear, "",
                    modifier = Modifier.onClick {
                        sharedData.scope.launch {
                            helpText = "Cleaning extention dir"
                            delay(200)
                            Preferences.getExtensionFolder.toFile().deleteRecursively()
                            sharedData.extensionManager = ExtensionManager().loadJarFromPath(Preferences.getExtensionFolder)
                            sharedData.anime = sharedData.extensionManager.animeSource
                            sharedData.manga = sharedData.extensionManager.mangaSource
                            sharedData.novel = sharedData.extensionManager.novelSource
                            helpText = "All extension has be deleted"
                            delay(1000)
                            helpText = ""
                        }
                    }
                )
                Text( " $helpText" , color = Color.Black )
            }
        }
        Box( Modifier.fillMaxSize().padding( 30.dp ) , contentAlignment = Alignment.BottomStart ){
            var helpText by remember { mutableStateOf( "" ) }
            var job : Job? = remember { null }
            Row {
                Image(
                    Icons.Filled.Download , "" ,
                    modifier = Modifier
                        .onClick {
                            if ( job == null ) job = sharedData.scope.launch {
                                helpText = "Starting Download"
                                delay( 500 )
                                extensionList.forEach {
                                    helpText = "Downloading $it"
                                    if ( Regex( "com.github..*:.*:.*" ).matches( it ) ) {
                                        fetchJarPathFromJitpack( it ).let { info ->
                                            if ( !"${Preferences.getExtensionFolder}/${info["name"]}.version".toFile().isFile ||
                                                "${Preferences.getExtensionFolder}/${info["name"]}.version".toFile().readText().trim() != info["version"]!!.trim()
                                            ) {
                                                Download(info["url"]!!, "${Preferences.getExtensionFolder}/${info["name"]}.jar")
                                                "${Preferences.getExtensionFolder}/${info["name"]}.version".toFile()
                                                    .also { if (!it.isFile) it.createNewFile() }
                                                    .writeText(info["version"]!!)
                                            } else helpText = "Jar is already updated to latest"
                                        }
                                    } else {
                                        Download( it , Preferences.getExtensionFolder + it.substring( it.lastIndexOf( "/" )+1 ) )
                                    }
                                    if ( helpText != "Jar is already updated to latest" ) helpText = "Download Complete"
                                    delay( 500 )
                                }
                                helpText = "Loading Jar"
                                delay( 500 )
                                sharedData.extensionManager.loadJarFromPath( Preferences.getExtensionFolder )
                                helpText = ""
                                job = null
                            }
                        }
                )
                Text( " $helpText" , color = Color.Black )
            }
        }
    }


    DisposableEffect( key1 = extensionList , effect = {
        this.onDispose {
            configDir.toFile().mkdirs()
            "$configDir/repoList.json".toFile()
                .also { if ( !it.isFile ) it.createNewFile() }
                .run {
                    val modifiedGson = Gson().toJson( extensionList ).trim()
                    val previousGson = readText().trim()
                    if ( previousGson != modifiedGson ) writeText( modifiedGson )
                }
        }
    } )

}