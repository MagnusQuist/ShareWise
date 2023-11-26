package com.sdu.sharewise.ui.home

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavHostController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.sdu.sharewise.R
import com.sdu.sharewise.data.model.Group
import com.sdu.sharewise.navigation.Routes

@Composable
fun HomeView(viewModel: HomeViewModel, navController: NavHostController) {
    var ownGroups by remember { mutableStateOf(mutableStateListOf<Group?>(null)) }
    var othersGroups by remember { mutableStateOf(mutableStateListOf<Group?>(null)) }

    val context = LocalContext.current.applicationContext

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Profile Picture and Username
            Row(
                modifier = Modifier
                    .clickable {
                        navController.navigate(Routes.Profile.route) {
                            popUpTo(Routes.Home.route)
                        }
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = null,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiaryContainer)
                )
                Column() {
                    Text(
                        text = viewModel.getCurrentUser?.displayName ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = viewModel.getCurrentUser?.email ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            IconButton(
                modifier = Modifier
                    .size(46.dp),
                    //.background(MaterialTheme.colorScheme.tertiary, CircleShape),
                onClick = {
                    navController.navigate(Routes.CreateGroup.route) {
                        popUpTo(Routes.Home.route)
                    }
                    // Toast.makeText(context, "Add new group", Toast.LENGTH_SHORT).show()
                }
            ) {
                Icon(
                    modifier = Modifier
                        .size(28.dp),
                    imageVector = Icons.Default.Add,
                    tint = MaterialTheme.colorScheme.secondary,
                    contentDescription = "Add Group"
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
            item {
                Text(
                    text = "My Groups",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            itemsIndexed (ownGroups) { index, _ ->
                ownGroups[index]?.let { otherGroupCard(group = it) }
                Spacer(modifier = Modifier.height(26.dp))
            }

            item {
                Text(
                    text = "Other Groups",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            itemsIndexed (othersGroups) { index, _ ->
                othersGroups[index]?.let { otherGroupCard(group = it) }
                Spacer(modifier = Modifier.height(26.dp))
            }
        }
    }

    viewModel.getDatabase.getReference("Groups").addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            ownGroups.clear()
            othersGroups.clear()

            for (childSnapshot in snapshot.children) {
                val group = childSnapshot.getValue(Group::class.java)

                if (group != null) {
                    if (group.ownerUid == viewModel.getCurrentUser?.uid) {
                        ownGroups.add(group)
                    } else if (group.members.contains(viewModel.getCurrentUser?.email)) {
                        othersGroups.add(group)
                    }
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Toast.makeText(context, "Fail to get groups.", Toast.LENGTH_SHORT).show()
        }
    })
}

@Composable
fun otherGroupCard(
    modifier: Modifier = Modifier,
    group: Group
) {
    Card(modifier = modifier
        .fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Row(modifier = Modifier
            .height(IntrinsicSize.Min)
        ) {
            Row(modifier = Modifier
                .background(Color(group.color.toColorInt()))
                .padding(14.dp)
                .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .size(20.dp),
                    imageVector = Icons.Default.PersonOutline,
                    tint = MaterialTheme.colorScheme.onSecondary,
                    contentDescription = "People in group"
                )
                Text(
                    text = group.members.size.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(14.dp)
            ) {
                Text(
                    text = group.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(modifier = Modifier
                    .padding(top = 10.dp)
                ) {
                    Column(modifier = Modifier
                        .weight(1f)
                    ) {
                        Text(
                            text = "Total Expense",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "13564.54 kr.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = contentColorFor(MaterialTheme.colorScheme.background)
                        )
                    }
                    Column(modifier = Modifier
                        .weight(1f)
                    ) {
                        Text(
                            textAlign = TextAlign.End,
                            text = "I'm Owed",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            textAlign = TextAlign.End,
                            text = "1430.50 kr.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}