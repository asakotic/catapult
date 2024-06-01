package com.example.catapult.core

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AppIconButton(
    imageVector: ImageVector,
    onClick: () -> Unit,
    contentDescription: String? = null
) {
    IconButton(onClick = onClick) {
        Icon(imageVector = imageVector, contentDescription = contentDescription)
    }
}

@Composable
fun SimpleInfo(
    title: String,
    description: String
) {
    Column {
        Text(text = "$title:", fontWeight = FontWeight.Bold)
        Text(text = description.take(250))
    }
}


@Composable
fun ListInfo(
    title: String,
    items: List<String>
) {
    Column {
        Text(text = "$title:", fontWeight = FontWeight.Bold)

        Row {
            items.take(3).forEach {
                Text(text = it)
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}