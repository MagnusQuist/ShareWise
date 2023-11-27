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
import com.sdu.sharewise.navigation.Routes
import com.sdu.sharewise.ui.components.FormFieldText
import com.sdu.sharewise.ui.components.SettingsGroup

@Composable
fun ProfilePhoneView(
    viewModel: ProfileViewModel = hiltViewModel(),
    navController: NavHostController
) {
    var phone by remember { mutableStateOf("") }
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
                        text = "Edit Phone",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            SettingsGroup(
                name = "",
            ) {

                FormFieldText(
                    text = phone,
                    placeholder = viewModel.getCurrentUser?.phoneNumber?: "",
                    onChange = {
                        phone = it
                    },
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text,
                    keyBoardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    )
                )
                Button(
                    onClick = {viewModel.setPhone(
                        uuid = viewModel.getCurrentUser?.uid?: "",
                        phone = phone
                        )
                        navController.navigate(Routes.Profile.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.primary)
                ) {Text(
                    text = "Save Changes",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )

                }
            }

        }
    }
}