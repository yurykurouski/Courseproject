package com.example.courseproject.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.courseproject.data.ContactRepository
import com.example.courseproject.model.Contact
import com.example.courseproject.model.ContactType
import java.util.UUID

sealed interface Screen {
    object List : Screen
    data class Detail(val contactId: String?) : Screen
}

class ContactViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ContactRepository(application)

    // Contacts data list state
    var contacts by mutableStateOf<List<Contact>>(emptyList())
        private set

    // Navigation state
    var currentScreen by mutableStateOf<Screen>(Screen.List)
        private set

    // UI configuration states
    var searchQuery by mutableStateOf("")
    var isTableView by mutableStateOf(false)

    // Form fields states
    var formContactType by mutableStateOf(ContactType.PERSONAL)
        private set
    var formFirstName by mutableStateOf("")
    var formLastName by mutableStateOf("")
    var formPhone by mutableStateOf("")
    var formBirthday by mutableStateOf("")
    var formRelationship by mutableStateOf("")
    var formCompany by mutableStateOf("")
    var formEmail by mutableStateOf("")
    var formJobTitle by mutableStateOf("")

    // Form validation errors: field name -> error message
    var formErrors by mutableStateOf<Map<String, String>>(emptyMap())
        private set

    init {
        contacts = repository.loadContacts()
    }

    // Navigation and Action Handlers
    fun startAddContact() {
        resetForm()
        currentScreen = Screen.Detail(null)
    }

    fun startEditContact(contact: Contact) {
        resetForm()
        formContactType = contact.type
        formFirstName = contact.firstName
        formLastName = contact.lastName
        formPhone = contact.phone
        formBirthday = contact.birthday
        formRelationship = contact.relationship
        formCompany = contact.company
        formEmail = contact.email
        formJobTitle = contact.jobTitle
        currentScreen = Screen.Detail(contact.id)
    }

    fun navigateTo(screen: Screen) {
        currentScreen = screen
    }

    fun updateContactType(type: ContactType) {
        // Contact type is fixed for existing contacts, only mutable for new ones
        if (currentScreen is Screen.Detail && (currentScreen as Screen.Detail).contactId == null) {
            formContactType = type
        }
    }

    private fun resetForm() {
        formContactType = ContactType.PERSONAL
        formFirstName = ""
        formLastName = ""
        formPhone = ""
        formBirthday = ""
        formRelationship = ""
        formCompany = ""
        formEmail = ""
        formJobTitle = ""
        formErrors = emptyMap()
    }

    fun validateAndSaveContact(): Boolean {
        val errors = mutableMapOf<String, String>()

        if (formFirstName.trim().isEmpty()) {
            errors["firstName"] = "First name is required"
        }
        if (formLastName.trim().isEmpty()) {
            errors["lastName"] = "Last name is required"
        }
        if (formPhone.trim().isEmpty()) {
            errors["phone"] = "Phone number is required"
        }

        if (formContactType == ContactType.BUSINESS && formEmail.trim().isNotEmpty()) {
            val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
            if (!formEmail.trim().matches(Regex(emailPattern))) {
                errors["email"] = "Invalid email format"
            }
        }

        formErrors = errors
        if (errors.isNotEmpty()) {
            return false
        }

        val activeScreen = currentScreen
        val updatedContact = if (activeScreen is Screen.Detail && activeScreen.contactId != null) {
            // Edit existing contact
            Contact(
                id = activeScreen.contactId,
                type = formContactType,
                firstName = formFirstName.trim(),
                lastName = formLastName.trim(),
                phone = formPhone.trim(),
                birthday = if (formContactType == ContactType.PERSONAL) formBirthday.trim() else "",
                relationship = if (formContactType == ContactType.PERSONAL) formRelationship.trim() else "",
                company = if (formContactType == ContactType.BUSINESS) formCompany.trim() else "",
                email = if (formContactType == ContactType.BUSINESS) formEmail.trim() else "",
                jobTitle = if (formContactType == ContactType.BUSINESS) formJobTitle.trim() else ""
            )
        } else {
            // Create new contact
            Contact(
                id = UUID.randomUUID().toString(),
                type = formContactType,
                firstName = formFirstName.trim(),
                lastName = formLastName.trim(),
                phone = formPhone.trim(),
                birthday = if (formContactType == ContactType.PERSONAL) formBirthday.trim() else "",
                relationship = if (formContactType == ContactType.PERSONAL) formRelationship.trim() else "",
                company = if (formContactType == ContactType.BUSINESS) formCompany.trim() else "",
                email = if (formContactType == ContactType.BUSINESS) formEmail.trim() else "",
                jobTitle = if (formContactType == ContactType.BUSINESS) formJobTitle.trim() else ""
            )
        }

        val newList = contacts.toMutableList()
        val index = newList.indexOfFirst { it.id == updatedContact.id }
        if (index != -index - 1 && index >= 0) {
            newList[index] = updatedContact
        } else {
            newList.add(updatedContact)
        }

        contacts = newList
        repository.saveContacts(contacts)
        resetForm()
        currentScreen = Screen.List
        return true
    }

    fun deleteContact(contactId: String) {
        val newList = contacts.filterNot { it.id == contactId }
        contacts = newList
        repository.saveContacts(contacts)
        if (currentScreen is Screen.Detail && (currentScreen as Screen.Detail).contactId == contactId) {
            resetForm()
            currentScreen = Screen.List
        }
    }

    fun getFilteredContacts(): List<Contact> {
        val query = searchQuery.trim().lowercase()
        if (query.isEmpty()) return contacts

        return contacts.filter { contact ->
            contact.firstName.lowercase().contains(query) ||
                    contact.lastName.lowercase().contains(query) ||
                    contact.phone.lowercase().contains(query) ||
                    contact.company.lowercase().contains(query) ||
                    contact.jobTitle.lowercase().contains(query)
        }
    }
}
