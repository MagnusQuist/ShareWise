package com.sdu.sharewise.ui.group

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sdu.sharewise.data.Resource
import com.sdu.sharewise.navigation.Routes
import com.sdu.sharewise.ui.components.FormFieldText
import com.sdu.sharewise.ui.components.NavigateToHomeButton

@Composable
fun CreateGroupView(viewModel: CreateGroupViewModel, navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var color = { mutableStateListOf<String>() }

    var currentMember by remember { mutableStateOf("") }
    var members by remember { mutableStateOf(mutableStateListOf<String?>(null)) }

    val createGroupFlow = viewModel.createGroupFlow.collectAsState()

    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current.applicationContext

    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
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
                NavigateToHomeButton(navController = navController)

                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "Create Group",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(86.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    )
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

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

        FormFieldTextAddMember(
            text = currentMember,
            membersList = members,
            placeholder = "Add members (email)",
            onChange = {
                currentMember = it
            },
            leadingIcon = {
                Icon(
                    Icons.Outlined.Person,
                    "Member Icon",
                )
            },
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Text,
            keyBoardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
            context = context,
            viewModel = viewModel
        )

        Spacer(modifier = Modifier.height(18.dp))

        if (members.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight(0.8F)
            ) {
                itemsIndexed (members) { index, member ->
                    if (member != null) {
                        var email by remember { mutableStateOf<String?>(null) }

                        // Get email from uuid
                        LaunchedEffect(member) {
                            viewModel.findEmailByUuid(member) { tag, message ->
                                if (tag == "success") {
                                    if (message != null) {
                                        email = message
                                    }
                                } else {
                                    email = "Email not found.."
                                }
                            }
                        }

                        if (email != null) {
                            MemberItem(email = email!!, index = index, membersList = members)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Create Button
        Button(
            onClick = {
                viewModel.createGroup(name, desc, members)
                Toast.makeText(context, "Creating Group", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    trackColor = MaterialTheme.colorScheme.secondary
                )
            } else {
                Text(
                    text = "Create Group",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        createGroupFlow.value?.let {
            when (it) {
                is Resource.Failure -> {
                    val context = LocalContext.current
                    Toast.makeText(context, it.exception.message, Toast.LENGTH_LONG).show()
                }
                Resource.Loading -> {
                    isLoading = true
                }
                is Resource.Success -> {
                    LaunchedEffect(Unit) {
                        Toast.makeText(context, "Group Created", Toast.LENGTH_SHORT).show()
                        navController.navigate(Routes.Home.route) {
                            popUpTo("Home") { inclusive = true }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MemberItem(
    email: String?,
    index: Int,
    membersList: SnapshotStateList<String?>,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 8.dp)
            .height(40.dp)
            .border(1.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(8.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row() {
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(12.dp))
            if (email != null) {
                Text(text = email, style = MaterialTheme.typography.bodyMedium)
            }
        }

        IconButton(
            onClick = {
                membersList.removeAt(index)
            }
        ) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = null
            )
        }
    }
}

@Composable
fun FormFieldTextAddMember(
    modifier: Modifier = Modifier,
    text: String,
    placeholder: String,
    leadingIcon: @Composable() (() -> Unit)? = null,
    onChange: (String) -> Unit = {},
    imeAction: ImeAction = ImeAction.Next,
    keyboardType: KeyboardType = KeyboardType.Text,
    keyBoardActions: KeyboardActions = KeyboardActions(),
    isEnabled: Boolean = true,
    membersList: SnapshotStateList<String?>,
    context: Context,
    viewModel: CreateGroupViewModel
) {
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth(),
        value = text,
        shape = MaterialTheme.shapes.small,
        onValueChange = onChange,
        leadingIcon = leadingIcon,
        textStyle = TextStyle(fontSize = 16.sp, color = MaterialTheme.colorScheme.primary),
        keyboardOptions = KeyboardOptions(imeAction = imeAction, keyboardType = keyboardType),
        keyboardActions = keyBoardActions,
        enabled = isEnabled,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
            disabledBorderColor = MaterialTheme.colorScheme.secondary,
            disabledTextColor = MaterialTheme.colorScheme.secondary,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.secondary,
            errorBorderColor = MaterialTheme.colorScheme.error,
        ),
        placeholder = {
            Text(text = placeholder, style = TextStyle(fontSize = 16.sp, color = MaterialTheme.colorScheme.secondary))
        },
        trailingIcon = {
            IconButton(
                onClick = {
                    if (text.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$".toRegex())) {
                        viewModel.findUuidByEmail(text) { tag, message ->
                            if (tag == "success") {
                                membersList.add(message)
                            } else {
                                Toast.makeText(context, tag, Toast.LENGTH_SHORT).show()
                            }
                        }
                        onChange("")
                    } else {
                        Toast.makeText(context, "Input valid email", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null
                )
            }
        }
    )
}