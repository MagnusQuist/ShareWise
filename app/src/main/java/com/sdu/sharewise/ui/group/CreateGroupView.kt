package com.sdu.sharewise.ui.group

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sdu.sharewise.ui.components.ColorPicker
import com.sdu.sharewise.ui.components.FormFieldText
import com.sdu.sharewise.ui.profile.ProfileViewModel

@Composable
fun CreateGroupView(viewModel: ProfileViewModel, navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }

    val context = LocalContext.current.applicationContext

    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                modifier = Modifier
                    .size(46.dp)
                    .background(MaterialTheme.colorScheme.tertiary, CircleShape),
                onClick = {
                    if (navController.previousBackStackEntry != null) {
                        navController.navigateUp()
                    }
                }) {
                Icon(
                    modifier = Modifier
                        .size(24.dp),
                    imageVector = Icons.Default.ArrowBack,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "Go Back"
                )
            }

            Text(text = viewModel.getCurrentUser?.displayName?: "")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column {
            Text(
                text = "Group Owner:",
                style = MaterialTheme.typography.bodyMedium,
                color = contentColorFor(MaterialTheme.colorScheme.background)
            )

            Text(
                text = viewModel.getCurrentUser?.displayName + " (" + viewModel.getCurrentUser?.email + ")" ?: "",
                style = MaterialTheme.typography.headlineSmall,
                color = contentColorFor(MaterialTheme.colorScheme.background)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        FormFieldText(
            text = name,
            placeholder = "Group name",
            onChange = {
                name = it
            },
            leadingIcon = {
                Icon(
                    Icons.Outlined.Group,
                    "Group Icon",
                )
            },
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Text,
            keyBoardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            )
        )

        Spacer(modifier = Modifier.height(18.dp))

        FormFieldText(
            text = desc,
            placeholder = "Group description",
            onChange = {
                desc = it
            },
            leadingIcon = {
                Icon(
                    Icons.Outlined.Description,
                    "Description Icon",
                )
            },
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Text,
            keyBoardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            )
        )

        Spacer(modifier = Modifier.height(18.dp))

        // ColorPicker()
    }
}