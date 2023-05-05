package com.example.phonebook.ui.routing

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

sealed class Screen {
    object Phones: Screen()
    object SavePhone: Screen()
    object Trash: Screen()
}

object PhonesRouter {
    var currentScreen: Screen by mutableStateOf(Screen.Phones)

    fun navigateTo(destination: Screen) {
        currentScreen = destination
    }
}