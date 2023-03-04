package logic.extensionTypes

import androidx.compose.runtime.*
import tanoshi.source.api.annotation.EXTENSION
import tanoshi.source.api.enum.TYPES
import tanoshi.source.api.model.MangaExtension
import tanoshi.source.api.model.component.Manga
import tanoshi.source.api.model.component.MangaChapter


class Manga{

    val sources : HashMap<String, MangaExtension> = HashMap()

    val query = mutableStateOf( "" )
    val selected : MutableState<MangaExtension?> = mutableStateOf( sources.run { if ( isEmpty() ) null else entries.first().value } )
    val result : MutableState<List<Manga>> = mutableStateOf( listOf() )
    var pageIndex : Int = 1
    val nextPageNotFound : MutableState<Boolean> = mutableStateOf(false)
    var selectedEntry : Manga? = null
    var chapter : MangaChapter? = null
    var selectedTab : String? by mutableStateOf( null )
    var contentLoading by mutableStateOf( false )

    fun updateSources( unknownSources : HashMap<String,Class<*>> ) = unknownSources.forEach { ( name , extension ) ->
        try {
            if ( extension.getAnnotation( EXTENSION::class.java ).contentType == TYPES.MANGA && !sources.containsKey( name ) )
                sources[name] = extension.getConstructor().newInstance() as MangaExtension
        } catch ( _ : Exception ) {}
    }

    fun updateSelectedSource() {
        selected.value = sources.run { if ( isEmpty() ) null else entries.first().value }
    }

}