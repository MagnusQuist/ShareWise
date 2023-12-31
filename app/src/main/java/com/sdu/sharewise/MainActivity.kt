package com.sdu.sharewise

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.sdu.sharewise.ui.theme.ShareWiseTheme
import com.sdu.sharewise.navigation.ShareWiseNavHost
import com.sdu.sharewise.ui.components.RequestNotificationPermissionDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShareWiseTheme {
                Surface (
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    RequestNotificationPermissionDialog()
                    ShareWiseNavHost()
                }
            }
        }
    }
}