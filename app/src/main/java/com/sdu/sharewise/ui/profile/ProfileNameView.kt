package com.sdu.sharewise.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sdu.sharewise.ui.components.FormFieldText

@Composable
fun ProfileNameView(
    viewModel: ProfileViewModel = hiltViewModel(),
    navController: NavHostController
) {
    var name by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current

    Surface(
        color = MaterialTheme.colorScheme.onSecondary,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    IconButton(
                        modifier = Modifier
                            .size(22.dp),
                        onClick = {
                            if (navController.previousBackStackEntry != null) {
                                navController.navigateUp()
                            }
                        }) {
                        Icon(
                            modifier = Modifier
                                .size(22.dp),
                            imageVector = Icons.Default.ArrowBackIos,
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = "Go Back"
                        )
                    }

                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = "Edit Name",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column {

                FormFieldText(
                    text = name,
                    placeholder = viewModel.getCurrentUser?.displayName?: "",
                    onChange = {
                        name = it
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Person,
                            "Person Icon",
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

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {viewModel.setUsername(
                        uuid = viewModel.getCurrentUser?.uid?: "",
                        name = name
                        )
                        navController.navigateUp() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "Save Changes",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                }
            }
        }
    }
}