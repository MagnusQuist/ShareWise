package com.sdu.sharewise.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun PermissionDialog(
    onRequestPermission: () -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = {
            Text("Permission Required")
        },
        text = {
            Text("Please grant permission in device settings.")
        },
        confirmButton = {
            Button(onClick = { onRequestPermission() }) {
                Text("Open Settings")
            }
        }
    )
}