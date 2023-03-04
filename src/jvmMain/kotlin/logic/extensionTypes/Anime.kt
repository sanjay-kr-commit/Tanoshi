package logic.extensionTypes


import androidx.compose.runtime.*
import tanoshi.source.api.annotation.EXTENSION
import tanoshi.source.api.enum.TYPES
import tanoshi.source.api.model.AnimeExtension
import tanoshi.source.api.model.component.Anime
import tanoshi.source.api.model.component.AnimeEpisode

class Anime {

    val sources : HashMap<String, AnimeExtension> = HashMap()

    val query = mutableStateOf( "" )
    val selected : MutableState<AnimeExtension?> = mutableStateOf( sources.run { if ( isEmpty() ) null else entries.first().value } )
    val result : MutableState<List<Anime>> = mutableStateOf( listOf() )
    var pageIndex : Int = 1
    val nextPageNotFound : MutableState<Boolean> = mutableStateOf(false)
    var selectedEntry : Anime? = null
    var episode : AnimeEpisode? = null
    var selectedTab : String? by mutableStateOf( null )
//    val query : MutableState<String> = mutableStateOf( "" )
//    var selectedEntry : Anime? = null
//    var pageIndex : Int = 1
//    val nextPage : MutableState<Boolean> = mutableStateOf( false )
//    val showProgress : MutableState<Boolean> = mutableStateOf( false )
//    val selectedFunction : MutableState<String?> = mutableStateOf( null )
//    val tabResult : MutableState<List<Anime>> = mutableStateOf( listOf() )

    fun updateSources( unknownSources : HashMap<String,Class<*>> ) = unknownSources.forEach { ( name , extension ) ->
        try {
            if ( extension.getAnnotation( EXTENSION::class.java ).contentType == TYPES.ANIME && !sources.containsKey( name ) )
                sources[name] = extension
                    .getConstructor()
                    .newInstance()
                        as AnimeExtension
        } catch ( e : Exception ) {
            println( e )
        }
    }

    fun updateSelectedSource() {
        selected.value = sources.run { if ( isEmpty() ) null else entries.first().value }
    }

}