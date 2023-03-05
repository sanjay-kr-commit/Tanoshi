package ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import preferences.Colors
import preferences.Spacing
import shared.AppData

@Composable
fun StartScreen( navModifier: Modifier = Modifier , navContent : @Composable () -> Unit , pageModifier: Modifier = Modifier , pageContent : @Composable () -> Unit , sharedData : AppData = AppData()) {
    Box( Modifier.background( Colors.StartScreenBackground.value ).fillMaxSize() ) {
        Row(
            Modifier
                .fillMaxSize()
                .background( Colors.StartScreenBackground.value )
                .padding( if ( sharedData.navigationBarHidden.value ) 0.dp else 5.dp )
        ) {

          if ( !sharedData.navigationBarHidden.value ){
              Column(
                navModifier
                    .clip( RoundedCornerShape( Spacing.Roundness.value ) )
                    .background( Colors.WidgetColor.value )
                    .fillMaxHeight()
                    .width( 100.dp ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                navContent()
            }

            Spacer( Modifier.padding( Spacing.WidgetSpacer.value ) )
          }

            Column(
                pageModifier
                    .clip( RoundedCornerShape( if ( sharedData.navigationBarHidden.value ) 0.dp else 5.dp ) )
                    .fillMaxSize()
                    .background( Colors.WidgetColor.value ) )
            {
                pageContent()
            }

        }
    }
}