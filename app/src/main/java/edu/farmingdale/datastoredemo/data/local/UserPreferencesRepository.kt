
package edu.farmingdale.datastoredemo.data.local

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

/*
 * Concrete class implementation to access data store
 */
class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val IS_LINEAR_LAYOUT = booleanPreferencesKey("is_linear_layout")
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
        const val TAG = "UserPreferencesRepo"
    }

    val isLinearLayout: Flow<Boolean> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[IS_LINEAR_LAYOUT] ?: true
        }

    // Makes a Flow isDarkTheme - users theme preferences
    val isDarkTheme: Flow<Boolean> = dataStore.data
        .catch { exception -> // catch block handles any exceptions while accessing dataStore.data
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences.", exception) // Log the exception to provide info about error
                // Bottom left of screen cat icon
                emit(emptyPreferences()) // if it has exception - sets emptyPreferences()
            } else {
                throw exception
            }
        }
        .map { preferences -> // Map operator transforms the emitted Preferences object into a Boolean value
            preferences[IS_DARK_THEME] ?: false // Default set to false for light theme
        }

    // Allows it to pause and resume without blocking the main thread
    suspend fun saveLayoutPreference(isLinearLayout: Boolean) {
        dataStore.edit { preferences -> // Parameter inside this block represents the current stored data - block allows us to edit this data
            preferences[IS_LINEAR_LAYOUT] = isLinearLayout
            // Saves the layout preference by associating the isLinearLayout Boolean value with the IS_LINEAR_LAYOUT key
            // IS_LINEAR_LAYOUT is a predefined key that identifies this specific preference in DataStore
        }
    }

    suspend fun saveThemePreference(isDarkTheme: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_DARK_THEME] = isDarkTheme
        }
    }
}
