package logic.extensionTypes

import androidx.compose.runtime.*
import tanoshi.source.api.annotation.EXTENSION
import tanoshi.source.api.enum.TYPES
import tanoshi.source.api.model.NovelExtension
import tanoshi.source.api.model.component.NovelChapter
import tanoshi.source.api.model.component.Novel


class Novel {

    val sources : HashMap<String, NovelExtension> = HashMap()

    val query = mutableStateOf( "" )
    val selected : MutableState<NovelExtension?> = mutableStateOf( sources.run { if ( isEmpty() ) null else entries.first().value } )
    val result : MutableState<List<Novel>> = mutableStateOf( listOf() )
    var pageIndex : Int = 1
    val nextPageNotFound : MutableState<Boolean> = mutableStateOf(false)
    var selectedEntry : Novel? = null
    var chapter : NovelChapter? = null
    var selectedTab : String? by  mutableStateOf( null )

    fun updateSources( unknownSources : HashMap<String,Class<*>> ) = unknownSources.forEach { ( name , extension ) ->
        try {
            if ( extension.getAnnotation( EXTENSION::class.java ).contentType == TYPES.NOVEL && !sources.containsKey( name ) )
                sources[name] = extension.getConstructor().newInstance() as NovelExtension
        } catch ( _ : Exception ) {}
    }

    fun updateSelectedSource() {
        selected.value = sources.run { if ( isEmpty() ) null else entries.first().value }
    }

}