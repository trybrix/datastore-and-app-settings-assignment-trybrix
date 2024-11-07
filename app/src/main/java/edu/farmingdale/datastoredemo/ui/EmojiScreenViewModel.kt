package edu.farmingdale.datastoredemo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import edu.farmingdale.datastoredemo.R
import edu.farmingdale.datastoredemo.EmojiReleaseApplication
import edu.farmingdale.datastoredemo.data.local.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class EmojiScreenViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    // UI states access for various
    val uiState: StateFlow<EmojiReleaseUiState> =
        // Combine isLinearLayout and isDarkTheme flows from UserPreferencesRepository
        userPreferencesRepository.isLinearLayout.combine(userPreferencesRepository.isDarkTheme) { isLinearLayout, isDarkTheme ->
            // When both flows emit new values - combines to create a new EmojiReleaseUiState instance
            EmojiReleaseUiState(isLinearLayout, isDarkTheme = isDarkTheme)
        }.stateIn( // fun converts the combined flow into a StateFlow so that it behaves like a stateful object - always holding the most recent value for uiState
            scope = viewModelScope,
            // Flow is set to emits value for when app is on the foreground
            // 5 seconds stop delay is added to ensure it flows continuously
            // for cases such as configuration change
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = EmojiReleaseUiState()
        )

    /*
     * [selectLayout] change the layout and icons accordingly and
     * save the selection in DataStore through [userPreferencesRepository]
     */
    fun selectLayout(isLinearLayout: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveLayoutPreference(isLinearLayout)
        }
    }

    // fun takes a Boolean param whether darkMode should be enabled
    fun toggleTheme(isDarkTheme: Boolean) {
        viewModelScope.launch { // Allows the function to perform asynchronous operations without blocking the main thread
            // Concurrency - Multi Threading - Coroutines
            // Concurrency - managing multiple tasks and can happen with or without multiple threads

            // Multithreading - one way to achieve concurrency and resuming tasks on the same thread
            // + multiple tasks can appear to run at the same time without needing multiple threads

            // Coroutines - achieve concurrency by suspending and resuming tasks on the same thread
            // + so multiple tasks can appear to run at the same time without needing multiple threads
            userPreferencesRepository.saveThemePreference(isDarkTheme) // function writes the isDarkTheme value to DataStore - making it persistent
        }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as EmojiReleaseApplication)
                EmojiScreenViewModel(application.userPreferencesRepository)
            }
        }
    }
}

/*
 * Data class containing various UI States for Emoji Release screens
 */
data class EmojiReleaseUiState(
    val isLinearLayout: Boolean = true,
    val isDarkTheme: Boolean = false, // Set to false unless user changes it
    val toggleContentDescription: Int =
        if (isLinearLayout) R.string.grid_layout_toggle else R.string.linear_layout_toggle,
    val toggleIcon: Int =
        if (isLinearLayout) R.drawable.ic_grid_layout else R.drawable.ic_linear_layout
)
