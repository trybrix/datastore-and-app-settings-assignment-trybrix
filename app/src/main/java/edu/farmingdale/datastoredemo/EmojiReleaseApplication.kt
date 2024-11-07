package edu.farmingdale.datastoredemo

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import edu.farmingdale.datastoredemo.data.local.UserPreferencesRepository


private const val LAYOUT_PREFERENCE_NAME = "layout_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = LAYOUT_PREFERENCE_NAME
)

class EmojiReleaseApplication: Application() {
    // Late-initialized variable for the UserPreferencesRepository.
    // This will manage access to user preferences stored in DataStore.
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate() {
        super.onCreate()
        // Init userPreferencesRepository with the DataStore instance
        // Allows userPreferencesRepository to read and save user preferences throughout the app's lifetime
        userPreferencesRepository = UserPreferencesRepository(dataStore)
    }
}