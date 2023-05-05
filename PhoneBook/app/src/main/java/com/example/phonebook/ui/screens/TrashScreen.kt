package com.example.phonebook.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.painterResource
import com.example.phonebook.R
import com.example.phonebook.domain.model.PhoneModel
import com.example.phonebook.ui.component.AppDrawer
import com.example.phonebook.ui.component.Phone
import com.example.phonebook.ui.routing.Screen
import com.example.phonebook.viewmodel.MainViewModel
import kotlinx.coroutines.launch

private const val NO_DIALOG = 1
private const val RESTORE_NOTES_DIALOG = 2
private const val PERMANENTLY_DELETE_DIALOG = 3

@Composable
@ExperimentalMaterialApi
fun TrashScreen(viewModel: MainViewModel) {
    val phonesInThrash: List<PhoneModel> by viewModel.phonesInTrash.observeAsState(listOf())
    val selectedPhones: List<PhoneModel> by viewModel.selectedPhones.observeAsState(listOf())

    val dialogState = rememberSaveable { mutableStateOf(NO_DIALOG) }
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            val areActionsVisible = selectedPhones.isNotEmpty()
            TrashTopAppBar(
                onNavigationIconClick = {
                    coroutineScope.launch { scaffoldState.drawerState.open() }
                },
                onRestorePhonesClick = { dialogState.value = RESTORE_NOTES_DIALOG },
                onDeletePhonesClick = { dialogState.value = PERMANENTLY_DELETE_DIALOG },
                areActionsVisible = areActionsVisible
            )
        },
        scaffoldState = scaffoldState,
        drawerContent = {
            AppDrawer(
                currentScreen = Screen.Trash,
                closeDrawerAction = {
                    coroutineScope.launch { scaffoldState.drawerState.close() }
                }
            )
        },
        content = {
            Content(
                phones = phonesInThrash,
                onPhoneClick = { viewModel.onPhoneSelected(it) },
                selectedPhones = selectedPhones
            )

            val dialog = dialogState.value
            if (dialog != NO_DIALOG) {
                val confirmAction: () -> Unit = when (dialog) {
                    RESTORE_NOTES_DIALOG -> {
                        {
                            viewModel.restorePhones(selectedPhones)
                            dialogState.value = NO_DIALOG
                        }
                    }
                    PERMANENTLY_DELETE_DIALOG -> {
                        {
                            viewModel.permanentDeletePhones(selectedPhones)
                            dialogState.value = NO_DIALOG
                        }
                    }
                    else -> {
                        {
                            dialogState.value = NO_DIALOG
                        }
                    }
                }

                AlertDialog(
                    onDismissRequest = { dialogState.value = NO_DIALOG },
                    title = { Text(mapDialogTitle(dialog)) },
                    text = { Text(mapDialogText(dialog)) },
                    confirmButton = {
                        TextButton(onClick = confirmAction) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { dialogState.value = NO_DIALOG }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    )
}

@Composable
private fun TrashTopAppBar(
    onNavigationIconClick: () -> Unit,
    onRestorePhonesClick: () -> Unit,
    onDeletePhonesClick: () -> Unit,
    areActionsVisible: Boolean
) {
    TopAppBar(
        title = { Text(text = "Trash", color = MaterialTheme.colors.onPrimary) },
        navigationIcon = {
            IconButton(onClick = onNavigationIconClick) {
                Icon(
                    imageVector = Icons.Filled.List,
                    contentDescription = "Drawer Button"
                )
            }
        },
        actions = {
            if (areActionsVisible) {
                IconButton(onClick = onRestorePhonesClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_restore_from_trash_24),
                        contentDescription = "Restore Contact Button",
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
                IconButton(onClick = onDeletePhonesClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_delete_forever_24),
                        contentDescription = "Delete Contact Button",
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
            }
        }
    )
}

@Composable
@ExperimentalMaterialApi
private fun Content(
    phones: List<PhoneModel>,
    onPhoneClick: (PhoneModel) -> Unit,
    selectedPhones: List<PhoneModel>,
) {
    LazyColumn {
        items(count = phones.size) { phoneIndex ->
            val phone = phones[phoneIndex]
            val isPhoneSelected = selectedPhones.contains(phone)

            Phone(
                phone = phone,
                onPhoneClick = onPhoneClick,
                isSelected = isPhoneSelected
            )
        }
    }
}


private fun mapDialogTitle(dialog: Int): String = when (dialog) {
    RESTORE_NOTES_DIALOG -> "Restore contact"
    PERMANENTLY_DELETE_DIALOG -> "Delete contact permanently"
    else -> throw RuntimeException("Dialog not supported: $dialog")
}

private fun mapDialogText(dialog: Int): String = when (dialog) {
    RESTORE_NOTES_DIALOG -> "Are you sure?"
    PERMANENTLY_DELETE_DIALOG -> "Are you sure?"
    else -> throw RuntimeException("Dialog not supported: $dialog")
}