package com.sdu.sharewise.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun RationaleDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = {
            Text("Permission Required")
        },
        text = {
            Text("Please grant permission to continue.")
        },
        confirmButton = {
            Button(onClick = { onConfirm() }) {
                Text("Grant Permission")
            }
        }
    )
}