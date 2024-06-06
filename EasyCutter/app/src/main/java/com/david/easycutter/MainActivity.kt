package com.david.easycutter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.david.easycutter.navigation.Navigation
import com.david.easycutter.ui.theme.EasyCutterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EasyCutterTheme {
                Navigation()
            }
        }
    }
}