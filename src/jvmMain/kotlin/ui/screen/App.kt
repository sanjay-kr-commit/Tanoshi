package ui.screen

import androidx.compose.foundation.background
import androidx.compose.material.Icon
import androidx.compose.material.NavigationRailItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import navigation.NavigationScreens
import navigation.NavigationScreensHost
import preferences.Colors
import shared.AppData

@Composable
fun App( appData : AppData ) {

    StartScreen(
        navContent = {
            appData
                .screens.forEach {
                if ( !appData.hiddenScreens.contains( it.label ) ) NavigationRailItem(
                    selectedContentColor = Colors.SelectedIcon.value ,
                    unselectedContentColor = Colors.UnselectedIcon.value ,
                    modifier = Modifier.background( Color.Transparent ) ,
                    selected = appData.currentScreen.value == it.name,
                    icon = {
                        Icon(
                            imageVector = it.icon,
                            contentDescription = it.label
                        )
                    },
                    label = {
                        Text(
                            if ( appData.hiddenScreens.contains( it.label ) ) ""
                            else it.label
                        )
                    },
                    alwaysShowLabel = false,
                    onClick = {
                        appData
                            .navController.navigate(it.name)
                    }
                )
            }

            if ( appData.hiddenScreens.contains( appData.currentScreen.value ) ) {
                NavigationScreens.valueOf( appData.currentScreen.value ).let {
                    NavigationRailItem(
                        selectedContentColor = Colors.SelectedIcon.value ,
                        modifier = Modifier.background( Color.Transparent ) ,
                        selected = appData.currentScreen.value == it.name,
                        icon = {
                            Icon(
                                imageVector = it.icon,
                                contentDescription = it.label
                            )
                        },
                        onClick = {
                            appData
                                .navController.navigate(it.name)
                        }
                    )
                }
            }

        } ,
        pageContent = {
            NavigationScreensHost(appData.navController, appData )
        } ,
        sharedData = appData
    )

}