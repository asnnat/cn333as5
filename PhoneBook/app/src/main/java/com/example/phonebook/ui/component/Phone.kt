package com.example.phonebook.ui.component

import android.content.Intent
import android.media.Image
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.phonebook.database.PhoneDbModel
import com.example.phonebook.domain.model.PhoneModel
import com.example.phonebook.domain.model.TagModel

@ExperimentalMaterialApi
@Composable
fun Phone (
    modifier: Modifier = Modifier,
    phone: PhoneModel,
    onPhoneClick: (PhoneModel) -> Unit = {},
    isSelected: Boolean
){
    val background = if (isSelected)
        Color.LightGray
    else
        MaterialTheme.colors.surface

    val ctx = LocalContext.current

    Card(
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        backgroundColor = background
    ) {
        ListItem(
            text = { Text(text = phone.name, maxLines = 1) },
            secondaryText = {
                Text(text = phone.phone_number, maxLines = 1)
            },
            icon = {
                PhoneProfile(
                    color = Color.White,
                    size = 40.dp,
                    border = 1.dp,
                    tag = phone.tag,
                    onclick = {
                        val u = Uri.parse("tel:" + phone.phone_number)
                        val i = Intent(Intent.ACTION_DIAL, u)
                        try {
                            ctx.startActivity(i)
                        } catch (s: SecurityException) {
                            Toast.makeText(ctx, "An error occurred", Toast.LENGTH_LONG).show()
                        }
                    }
                )
            },
            modifier = Modifier.clickable {
                onPhoneClick.invoke(phone)
            }
        )
    }
}

@Composable
fun PhoneProfile(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.primaryVariant,
    size: Dp,
    border: Dp,
    tag: TagModel = TagModel.DEFAULT,
    onclick: () -> Unit = {}
) {
    var imageVector = Icons.Default.Person

    if (tag.name == "Home") {
        imageVector = Icons.Default.Home
    } else if (tag.name == "Work") {
        imageVector = Icons.Default.Phone
    } else {
        imageVector = Icons.Default.Person
    }
    IconButton(onClick = onclick) {
        Icon(
            imageVector = imageVector,
            contentDescription = "Save Phone Button",
            tint = MaterialTheme.colors.primaryVariant
        )
    }
}

@Preview
@Composable
fun PhoneProfilePreview() {
    PhoneProfile(
        color = Color.White,
        size = 40.dp,
        border = 2.dp
    )
}

@ExperimentalMaterialApi
@Preview
@Composable
private fun NotePreview() {
    Phone(phone = PhoneModel(1, "Natnicha", "0644153591"), isSelected = true)
}