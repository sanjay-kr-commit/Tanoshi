package logic.downloader

import tanoshi.lib.util.toHtml
import tanoshi.lib.util.toJsoup

fun fetchJarPathFromJitpack( repo : String ) : Map<String,String> {
    val url = "https://jitpack.io/${repo.replace(":", "/").replace("com.github.", "com/github/")}"
    return "$url/maven-metadata.xml"
        .toHtml()
        .toJsoup().run {
            mapOf( "url" to "$url/${select("artifactid").text()}-${select("version").text()}.jar" ,
                "version" to select( "version" ).text() ,
                "name" to select( "artifactid" ).text()
            )
        }
}