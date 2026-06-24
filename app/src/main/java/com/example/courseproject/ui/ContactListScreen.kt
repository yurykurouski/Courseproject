package com.example.courseproject.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.courseproject.R
import com.example.courseproject.model.Contact
import com.example.courseproject.model.ContactType

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ContactListScreen(
    viewModel: ContactViewModel,
    modifier: Modifier = Modifier
) {
    val filteredContacts = viewModel.getFilteredContacts()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Contacts",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                actions = {
                    IconButton(onClick = { viewModel.isTableView = !viewModel.isTableView }) {
                        Icon(
                            painter = if (viewModel.isTableView) {
                                painterResource(id = R.drawable.ic_home) // Use home icon or list icon
                            } else {
                                painterResource(id = R.drawable.ic_account_box) // Use grid icon
                            },
                            contentDescription = if (viewModel.isTableView) "Show List" else "Show Table"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.startAddContact() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(painterResource(R.drawable.ic_add), contentDescription = "Add Contact")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = viewModel.searchQuery,
                onValueChange = { viewModel.searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                placeholder = { Text("Search by name, phone, details...") },
                leadingIcon = { Icon(painterResource(R.drawable.ic_search), contentDescription = "Search") },
                trailingIcon = {
                    if (viewModel.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.searchQuery = "" }) {
                            Icon(painterResource(R.drawable.ic_clear), contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            if (filteredContacts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (viewModel.searchQuery.isEmpty()) "No contacts yet.\nTap + to add one!" else "No matching contacts found.",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                if (viewModel.isTableView) {
                    // Table Layout
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        stickyHeader {
                            TableHeaderRow()
                        }
                        items(filteredContacts, key = { it.id }) { contact ->
                            TableContactRow(
                                contact = contact,
                                onClick = { viewModel.startEditContact(contact) },
                                onDelete = { viewModel.deleteContact(contact.id) }
                            )
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        }
                    }
                } else {
                    // List Layout
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredContacts, key = { it.id }) { contact ->
                            ListContactItem(
                                contact = contact,
                                onClick = { viewModel.startEditContact(contact) },
                                onDelete = { viewModel.deleteContact(contact.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// Subcomponents for List View
@Composable
fun ListContactItem(
    contact: Contact,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            val initials = (contact.firstName.take(1) + contact.lastName.take(1)).uppercase()
            val avatarColor = if (contact.type == ContactType.PERSONAL) {
                Color(0xFF3F51B5) // Indigo
            } else {
                Color(0xFF009688) // Teal
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(avatarColor, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${contact.firstName} ${contact.lastName}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TypeBadge(type = contact.type)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = contact.phone,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Additional details depending on type
                if (contact.type == ContactType.PERSONAL && contact.relationship.isNotEmpty()) {
                    Text(
                        text = "Relationship: ${contact.relationship}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else if (contact.type == ContactType.BUSINESS && contact.company.isNotEmpty()) {
                    Text(
                        text = "Company: ${contact.company}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Action
            IconButton(onClick = onDelete) {
                Icon(
                    painter = painterResource(R.drawable.ic_delete),
                    contentDescription = "Delete Contact",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

// Subcomponents for Table View
@Composable
fun TableHeaderRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(vertical = 10.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Name",
            modifier = Modifier.weight(2f),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Type",
            modifier = Modifier.weight(1.2f),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Phone",
            modifier = Modifier.weight(1.8f),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Info",
            modifier = Modifier.weight(2f),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Act",
            modifier = Modifier.weight(0.8f),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TableContactRow(
    contact: Contact,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${contact.firstName} ${contact.lastName}",
            modifier = Modifier.weight(2f),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Box(modifier = Modifier.weight(1.2f)) {
            TypeBadge(type = contact.type)
        }

        Text(
            text = contact.phone,
            modifier = Modifier.weight(1.8f),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        val infoText = if (contact.type == ContactType.PERSONAL) {
            contact.relationship
        } else {
            contact.company
        }
        Text(
            text = infoText.ifEmpty { "-" },
            modifier = Modifier.weight(2f),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        IconButton(
            onClick = onDelete,
            modifier = Modifier
                .weight(0.8f)
                .size(24.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_delete),
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun TypeBadge(type: ContactType) {
    val containerColor = if (type == ContactType.PERSONAL) {
        Color(0xECEFF1FF) // Pale blue
    } else {
        Color(0xFFE0F2F1) // Pale teal
    }
    val contentColor = if (type == ContactType.PERSONAL) {
        Color(0xFF1A237E) // Navy
    } else {
        Color(0xFF004D40) // Dark teal
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(containerColor)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = type.name.lowercase().replaceFirstChar { it.uppercase() },
            color = contentColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
