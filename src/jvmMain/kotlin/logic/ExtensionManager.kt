package logic

import logic.extensionTypes.Anime
import logic.extensionTypes.Manga
import logic.extensionTypes.Novel
import tanoshi.source.api.annotation.EXTENSION
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.util.zip.ZipFile

class ExtensionManager {

    private val sources : HashMap<String , Class<*>> = HashMap()

    private var intPreviousSize : Int = 0
    private val jarsUrl : ArrayList<URL> = ArrayList()
    private var childClassLoader : URLClassLoader = URLClassLoader( jarsUrl.toTypedArray() , ClassLoader.getSystemClassLoader() )

    val animeSource = Anime()
    val mangaSource = Manga()
    val novelSource = Novel()

    private fun updateExtensionList() {
        animeSource.updateSources( sources )
        mangaSource.updateSources( sources )
        novelSource.updateSources( sources )
        animeSource.updateSelectedSource()
        mangaSource.updateSelectedSource()
        novelSource.updateSelectedSource()
    }

    private fun loadClass( className : String ) : Class<*> {
        if ( intPreviousSize != jarsUrl.size ) {
            childClassLoader = URLClassLoader( jarsUrl.toTypedArray() , ClassLoader.getSystemClassLoader() )
            intPreviousSize = jarsUrl.size
        }
        return Class.forName( className , true , childClassLoader )
    }

    fun loadJarFromPath( jarDir : String ) : ExtensionManager {
        if ( ! File( jarDir ).isDirectory ) throw Exception( "Not a Directory" )
        val listOfJar = File( jarDir ).listFiles()
        if ( listOfJar.isNullOrEmpty() ) return this
        val jars = ArrayList<File>( listOfJar.size )
        for ( file in listOfJar ) if ( file.toString().endsWith( ".jar" ) ) jars.add( file )
        for ( i in 0 until  jars.size ) {
            try {
                jarsUrl.add( jars[i].toURI().toURL() )
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        for ( jar in jars ) {
            for ( entry in ZipFile( jar ).entries() ) {
                if ( !entry.name.endsWith( ".class" ) ) continue
                val className = entry.name.replace( "/" , "." ).removeSuffix(".class")
                val loadedClass = loadClass( className )
                if ( loadedClass.isAnnotationPresent( EXTENSION::class.java ) ){
                    sources[className] = loadedClass
                }
            }
        }
        updateExtensionList()
        return this
    }

}