package com.example.courseproject.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.res.painterResource
import com.example.courseproject.R
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.courseproject.model.ContactType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailScreen(
    viewModel: ContactViewModel,
    contactId: String?,
    modifier: Modifier = Modifier
) {
    val isEditing = contactId != null
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) "Contact Details" else "Add Contact",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.List) }) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.validateAndSaveContact() }) {
                        Icon(painterResource(R.drawable.ic_check), contentDescription = "Save Contact")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Contact Type Selection (Only for new contacts)
            if (!isEditing) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Contact Type (Fixed once created)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = viewModel.formContactType == ContactType.PERSONAL,
                                    onClick = { viewModel.updateContactType(ContactType.PERSONAL) }
                                )
                                Text("Personal")
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = viewModel.formContactType == ContactType.BUSINESS,
                                    onClick = { viewModel.updateContactType(ContactType.BUSINESS) }
                                )
                                Text("Business")
                            }
                        }
                    }
                }
            } else {
                // Read-only indicator for type when editing
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Contact Type",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = viewModel.formContactType.name,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            // General Fields Section
            Text(
                text = "General Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // First Name Field
            OutlinedTextField(
                value = viewModel.formFirstName,
                onValueChange = { viewModel.formFirstName = it },
                label = { Text("First Name *") },
                isError = viewModel.formErrors.containsKey("firstName"),
                supportingText = {
                    viewModel.formErrors["firstName"]?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Last Name Field
            OutlinedTextField(
                value = viewModel.formLastName,
                onValueChange = { viewModel.formLastName = it },
                label = { Text("Last Name *") },
                isError = viewModel.formErrors.containsKey("lastName"),
                supportingText = {
                    viewModel.formErrors["lastName"]?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Phone Field
            OutlinedTextField(
                value = viewModel.formPhone,
                onValueChange = { viewModel.formPhone = it },
                label = { Text("Phone Number *") },
                isError = viewModel.formErrors.containsKey("phone"),
                supportingText = {
                    viewModel.formErrors["phone"]?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Type-Specific Fields Section
            Text(
                text = if (viewModel.formContactType == ContactType.PERSONAL) "Personal Details" else "Business Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (viewModel.formContactType == ContactType.PERSONAL) {
                // Birthday Field
                OutlinedTextField(
                    value = viewModel.formBirthday,
                    onValueChange = { viewModel.formBirthday = it },
                    label = { Text("Birthday (e.g. YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Relationship Field
                OutlinedTextField(
                    value = viewModel.formRelationship,
                    onValueChange = { viewModel.formRelationship = it },
                    label = { Text("Relationship (e.g. Friend, Spouse, Family)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                // Company Field
                OutlinedTextField(
                    value = viewModel.formCompany,
                    onValueChange = { viewModel.formCompany = it },
                    label = { Text("Company") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Email Field
                OutlinedTextField(
                    value = viewModel.formEmail,
                    onValueChange = { viewModel.formEmail = it },
                    label = { Text("Email Address") },
                    isError = viewModel.formErrors.containsKey("email"),
                    supportingText = {
                        viewModel.formErrors["email"]?.let {
                            Text(it, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )

                // Job Title Field
                OutlinedTextField(
                    value = viewModel.formJobTitle,
                    onValueChange = { viewModel.formJobTitle = it },
                    label = { Text("Job Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save and Action Buttons
            Button(
                onClick = { viewModel.validateAndSaveContact() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(painterResource(R.drawable.ic_check), contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isEditing) "Save Changes" else "Save Contact", fontWeight = FontWeight.Bold)
            }

            if (isEditing) {
                OutlinedButton(
                    onClick = { viewModel.deleteContact(contactId) },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Icon(painterResource(R.drawable.ic_delete), contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete Contact", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
