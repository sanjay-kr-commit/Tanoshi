import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import shared.AppData
import ui.screen.App


fun main() = application {
    val appData = AppData()
    Window(state = appData.appState ,onCloseRequest = ::exitApplication , title = "Tanoshi" ) {
        App( appData )
    }
}