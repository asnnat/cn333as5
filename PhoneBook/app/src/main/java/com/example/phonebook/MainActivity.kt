package com.example.phonebook

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.phonebook.ui.routing.PhonesRouter
import com.example.phonebook.ui.routing.Screen
import com.example.phonebook.ui.screens.PhonesScreen
import com.example.phonebook.ui.screens.SavePhoneScreen
import com.example.phonebook.ui.screens.TrashScreen
import com.example.phonebook.ui.theme.PhoneBookTheme
import com.example.phonebook.viewmodel.MainViewModel
import com.example.phonebook.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PhoneBookTheme(darkTheme = isSystemInDarkTheme()) {
                val viewModel: MainViewModel = viewModel(
                    factory = MainViewModelFactory(LocalContext.current.applicationContext as Application)
                )
                MainActivityScreen(viewModel = viewModel)
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun MainActivityScreen(viewModel: MainViewModel) {
    Surface {
        when (PhonesRouter.currentScreen) {
            is Screen.Phones -> PhonesScreen(viewModel)
            is Screen.SavePhone -> SavePhoneScreen(viewModel)
            is Screen.Trash -> TrashScreen(viewModel)
            else -> {}
        }
    }
}