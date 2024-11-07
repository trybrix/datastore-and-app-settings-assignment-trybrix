package edu.farmingdale.datastoredemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.farmingdale.datastoredemo.ui.EmojiReleaseApp
import edu.farmingdale.datastoredemo.ui.EmojiScreenViewModel
import edu.farmingdale.datastoredemo.ui.theme.DataStoreDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // viewModel needs uiState to access the preference
            val viewModel: EmojiScreenViewModel = viewModel(factory = EmojiScreenViewModel.Factory)
            val uiState by viewModel.uiState.collectAsState()

            DataStoreDemoTheme(darkTheme = uiState.isDarkTheme) {
                EmojiReleaseApp()
            }
        }
    }
}
