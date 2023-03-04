package novelReader

import tanoshi.lib.util.toJsoup

//TODO allot of work to do here
fun String.format() : String {
    return replace( "<br>"  ,">newLine<" )
        .toJsoup()!!
        .text()
        .replace( ">newLine<" , "\n" )
}