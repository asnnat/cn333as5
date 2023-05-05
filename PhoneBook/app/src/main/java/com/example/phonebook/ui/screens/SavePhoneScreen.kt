package com.example.phonebook.ui.screens

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.phonebook.domain.model.NEW_PHONE_ID
import com.example.phonebook.domain.model.PhoneModel
import com.example.phonebook.domain.model.TagModel
import com.example.phonebook.ui.component.PhoneProfile
import com.example.phonebook.ui.routing.PhonesRouter
import com.example.phonebook.ui.routing.Screen
import com.example.phonebook.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun SavePhoneScreen(viewModel: MainViewModel) {
    val phoneEntry by viewModel.phoneEntry.observeAsState(PhoneModel())
    val tags: List<TagModel> by viewModel.tags.observeAsState(listOf())

    val bottomDrawerState = rememberBottomDrawerState(BottomDrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val movePhoneToTrashDialogShownState = rememberSaveable { mutableStateOf(false) }

    BackHandler {
        if (bottomDrawerState.isOpen) {
            coroutineScope.launch { bottomDrawerState.close() }
        } else {
            PhonesRouter.navigateTo(Screen.Phones)
        }
    }

    Scaffold(
        topBar = {
            val isEditingMode: Boolean = phoneEntry.id != NEW_PHONE_ID
            SavePhoneTopAppBar(
                isEditingMode = isEditingMode,
                onBackClick = { PhonesRouter.navigateTo(Screen.Phones) },
                onSavePhoneClick = { viewModel.savePhone(phoneEntry) },
                onOpenTagPickerClick = {
                    coroutineScope.launch { bottomDrawerState.open() }
                },
                onDeletePhoneClick = {
                    movePhoneToTrashDialogShownState.value = true
                }
            )
        }
    ) {
        BottomDrawer(
            drawerState = bottomDrawerState,
            drawerContent = {
                TagPicker(
                    tags = tags,
                    onTagSelect = { tag ->
                        viewModel.onPhoneEntryChange(phoneEntry.copy(tag = tag))
                    }
                )
            }
        ) {
            SavePhoneContent(
                phone = phoneEntry,
                onPhoneChange = { updatePhoneEntry ->
                    viewModel.onPhoneEntryChange(updatePhoneEntry)
                }
            )
        }

        if (movePhoneToTrashDialogShownState.value) {
            AlertDialog(
                onDismissRequest = {
                    movePhoneToTrashDialogShownState.value = false
                },
                title = {
                    Text("Move phone to the trash?")
                },
                text = {
                    Text(
                        "Are you sure?"
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.movePhoneToTrash(phoneEntry)
                    }) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        movePhoneToTrashDialogShownState.value = false
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun SavePhoneTopAppBar(
    isEditingMode: Boolean,
    onBackClick: () -> Unit,
    onSavePhoneClick: () -> Unit,
    onOpenTagPickerClick: () -> Unit,
    onDeletePhoneClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Save Phone Number",
                color = MaterialTheme.colors.onPrimary
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back Button",
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        },
        actions = {
            IconButton(onClick = onSavePhoneClick) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = "Save Phone Button",
                    tint = MaterialTheme.colors.onPrimary
                )
            }

            IconButton(onClick = onOpenTagPickerClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Open Tag Picker Button",
                    tint = MaterialTheme.colors.onPrimary
                )
            }

            if (isEditingMode) {
                IconButton(onClick = onDeletePhoneClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Phone Button",
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
            }
        }
    )
}


@Composable
private fun TagPicker(
    tags: List<TagModel>,
    onTagSelect: (TagModel) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Tag picker",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(tags.size) { itemIndex ->
                val tag = tags[itemIndex]
                TagItem(
                    tag = tag,
                    onTagSelect = onTagSelect
                )
            }
        }
    }
}

@Composable
fun TagItem(
    tag: TagModel,
    onTagSelect: (TagModel) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    onTagSelect(tag)
                }
            )
    ) {
        PhoneProfile(
            modifier = Modifier.padding(10.dp),
            color = Color.White,
            size = 80.dp,
            border = 2.dp
        )
        Text(
            text = tag.name,
            fontSize = 22.sp,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterVertically)
        )
    }
}

@Composable
private fun SavePhoneContent(
    phone: PhoneModel,
    onPhoneChange: (PhoneModel) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        ContentTextField(
            label = "Name",
            value = phone.name,
            onValueChange = { newName ->
                onPhoneChange.invoke(phone.copy(name = newName))
            },
            imeAction = ImeAction.Next
        )

        ContentNumberField(
            modifier = Modifier
                .heightIn(max = 240.dp)
                .padding(top = 16.dp),
            label = "Phone number",
            value = phone.phone_number,
            onValueChange = { newPhoneNumber ->
                onPhoneChange.invoke(phone.copy(phone_number = newPhoneNumber))
            },
            imeAction = ImeAction.Done
        )

        PickedTag(tag = phone.tag)
    }
}

@Composable
private fun ContentTextField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    imeAction: ImeAction
) {
    val focusManager = LocalFocusManager.current

    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colors.surface
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() })
    )
}

@Composable
fun ContentNumberField(
    modifier: Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    imeAction: ImeAction
) {
    val focusManager = LocalFocusManager.current

    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colors.surface
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() })
    )
}

@Composable
private fun PickedTag(tag: TagModel) {
    Row(
        Modifier
            .padding(8.dp)
            .padding(top = 16.dp)
    ) {
        Text(
            text = "Picked tag",
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        )
        PhoneProfile(
            color = Color.White,
            size = 40.dp,
            border = 1.dp,
            modifier = Modifier.padding(4.dp)
        )
    }
}
