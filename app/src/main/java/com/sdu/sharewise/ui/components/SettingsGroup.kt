package com.sdu.sharewise.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SettingsGroup(
    name: String,
    topBorder: Boolean,
    bottomBorder: Boolean,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        if (topBorder) {
            Divider(modifier = Modifier.background(MaterialTheme.colorScheme.tertiary))
        }
        Text(
            text = name.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary,
        )
        if (name !== "") { Spacer(modifier = Modifier.height(4.dp)) }
        Surface(
            color = Color.Transparent,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(4),
        ) {
            Column {
                content()
            }
        }
        if (bottomBorder) {
            Divider(modifier = Modifier.background(MaterialTheme.colorScheme.tertiary))
        }
    }
}