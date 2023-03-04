package logic.helper

import tanoshi.lib.util.validateDir

object Preferences {

    private const val appName : String = "tanoshi"

    private val isWindows : Boolean
        get() = System.getProperty("os.name").lowercase().contains("window")

    val getCacheLocation : String
        get() = "${System.getProperty("user.dir")}/.cache/$appName".validateDir()


    val getDataLocation : String
        get() = "${System.getProperty("user.dir")}/.local/$appName/data".validateDir()

    val getConfigFolder : String
        get() = "${System.getProperty("user.dir")}/.local/$appName/config".validateDir()


    val getExtensionFolder : String
        get() = "${System.getProperty("user.dir")}/.local/$appName/extensions".validateDir()


    private fun checkPlayer() : String {
        val configFolder = getConfigFolder
        val playerConfigFile = "$configFolder/videoPlayer"
        if ( !playerConfigFile.doesFileExist() ) {
            val unix = "vlc "
            val windows = "vlc "
            playerConfigFile.toFile().writeText(
                if ( isWindows ) windows else unix
            )
        }
        return playerConfigFile.toFile().readText()
    }

    val getVideoPlayer : String
        get() = checkPlayer()

}