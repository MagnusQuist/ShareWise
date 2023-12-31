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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavHostController
import com.sdu.sharewise.R
import com.sdu.sharewise.data.model.Group
import com.sdu.sharewise.navigation.Routes

@Composable
fun HomeView(viewModel: HomeViewModel, navController: NavHostController) {
    // Observe groups from ViewModel
    val groups by viewModel.groups.observeAsState(emptyList())

    // Observe error message from ViewModel
    val errorMessage by viewModel.errorMessage.observeAsState(null)

    // Fetch groups when the composable is first created
    LaunchedEffect(Unit) {
        viewModel.fetchGroups()
    }

    val context = LocalContext.current.applicationContext

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp)
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
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "Add Group"
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (groups.isEmpty()) {
                item {
                    Text(
                        text = "No groups yet. Create one and share your expenses!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            } else {
                itemsIndexed (groups) { index, _ ->
                    GroupCard(group = groups[index], isOwned = groups[index].ownerUid == viewModel.getCurrentUser?.uid, navController = navController)
                    Spacer(modifier = Modifier.height(18.dp))
                }
            }

            errorMessage?.let { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
fun LoadingGroups(
    modifier: Modifier = Modifier,
    text: String
) {
    Column (
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = Color.White,
            trackColor = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupCard(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    group: Group,
    isOwned: Boolean
) {
    Card(
        onClick = {
            navController.navigate(
                "selectedGroup/"+group.groupUid, // Use the route with the parameter
                builder = {
                    launchSingleTop = true

                    popUpTo(Routes.Home.route) {
                        saveState = true
                    }
                    navController.graph.startDestinationRoute?.let { route ->
                        popUpTo(route) {
                            saveState = true
                        }
                    }
                    with(navController.currentBackStackEntry?.arguments) {
                        this?.getString("groupUid")?.let { groupUid ->
                            putString("groupUid", groupUid) // Provide the groupId here
                        }
                    }
                }
            )
        },
        modifier = modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.scrim
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp, start = 18.dp, bottom = 12.dp, end = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isOwned) {
                    Icon(
                        modifier = Modifier.size(22.dp),
                        imageVector = ImageVector.vectorResource(id = R.drawable.heart_solid),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                } else {
                    Icon(
                        modifier = Modifier.size(22.dp),
                        imageVector = ImageVector.vectorResource(id = R.drawable.heart_regular),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.surfaceTint
                    )
                }
                Text(
                    text = if (group.members.size == 0) "${group.members.size + 1} member(s)" else "${group.members.size} member(s)",
                    color = MaterialTheme.colorScheme.surfaceTint,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Group Name Row
            group.name?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(18.dp, 2.dp)
                )
            }
            // Group Total Expense
            Text(
                text = "Total expenses: 7645 DKK",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.surfaceTint,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(18.dp, 0.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.outline)
                    .padding(18.dp, 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "You're Owed",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.surfaceTint
                )
                Text(
                    text = "750 DKK",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.surfaceTint
                )
            }
        }
    }
}