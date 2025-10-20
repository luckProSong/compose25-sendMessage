package com.example.phonecontrol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.phonecontrol.ui.theme.MessageSendTheme

class ParentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MessageSendTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ParentScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}