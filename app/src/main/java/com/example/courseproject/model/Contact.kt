package com.example.courseproject.model

import java.util.UUID

enum class ContactType {
    PERSONAL,
    BUSINESS
}

data class Contact(
    val id: String = UUID.randomUUID().toString(),
    val type: ContactType,
    val firstName: String,
    val lastName: String,
    val phone: String,
    // Personal-specific fields
    val birthday: String = "",
    val relationship: String = "",
    // Business-specific fields
    val company: String = "",
    val email: String = "",
    val jobTitle: String = ""
)
