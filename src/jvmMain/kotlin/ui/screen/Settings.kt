package ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import navigation.NavigationScreens
import shared.AppData

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Settings( sharedData : AppData ) {
    LazyColumn {
        item {
            Box( Modifier.padding( 30.dp )
                .height( 60.dp )
                .fillMaxWidth()
                .clip( RoundedCornerShape( 10.dp ) ).background( Color.DarkGray ).padding( 10.dp )
                .onClick {
                         sharedData.navController.navigate( NavigationScreens.ExtensionRepoList.name )
                }
                , contentAlignment = Alignment.CenterStart ) {
                Text( "Extension Repo" , color = Color.White )
            }
        }
    }
}