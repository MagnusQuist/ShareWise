package com.sdu.sharewise.ui.components

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import android.Manifest

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestNotificationPermissionDialog() {
    val permissionState = rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    val context = LocalContext.current

    if (!permissionState.status.isGranted) {
        if (permissionState.status.shouldShowRationale) {
            RationaleDialog(
                onDismissRequest = {
                    Toast.makeText(
                        context,
                        "Permission dialog dismissed",
                        Toast.LENGTH_LONG
                    ).show()
                },
                onConfirm = { permissionState.launchPermissionRequest() }
            )
        } else {
            PermissionDialog(
                onRequestPermission = { permissionState.launchPermissionRequest() },
                onDismissRequest = {
                    Toast.makeText(
                        context,
                        "Permission dialog dismissed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }
}