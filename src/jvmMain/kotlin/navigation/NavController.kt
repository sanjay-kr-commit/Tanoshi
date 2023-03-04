package navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable

/**
 * NavController Class
 */
class NavController (
    // name of Composable which was loaded at the beginning
    private val startDestination : String ,
    // as we'll navigate through screens the stack will grow
    private var backStackScreens : MutableSet<String> = mutableSetOf()
) {

    // Variable to store the state of the current screen
    var currentScreen : MutableState<String> = mutableStateOf( startDestination )

    // Function to handle the navigation between the screen
    fun navigate( route : String ) {
        // only navigate when the current screen isn't same as requested screen
        if ( route != currentScreen.value ){
            // check is there is any current screen duplicate present in backstack and remove it if it's not the start screen
            if ( backStackScreens.contains(currentScreen.value) && currentScreen.value != startDestination ) {
                backStackScreens.remove( currentScreen.value )
            }
            // if the new screen is start screen clear backstack
            if ( route == startDestination ) {
                backStackScreens = mutableSetOf()
            } else {
                // else add current screen in backStack
                backStackScreens.add( currentScreen.value )
            }
            // change current screen to requested screen
            currentScreen.value = route
        }
    }

    // Function to handle the back
    fun navigateBack() {
        // if stack isn't empty go back to previous screen
        if (backStackScreens.isNotEmpty()) {
            currentScreen.value = backStackScreens.last()
            backStackScreens.remove(currentScreen.value)
        }
    }

}

/**
 * Composable to remember the state of the navcontroller
 */
@Composable
fun rememberNavController(
    startDestination: String,
    backStackScreens: MutableSet<String> = mutableSetOf()
): MutableState<NavController> = rememberSaveable {
    mutableStateOf(NavController(startDestination, backStackScreens))
}

