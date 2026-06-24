package com.example.courseproject.data

import android.content.Context
import com.example.courseproject.model.Contact
import com.example.courseproject.model.ContactType
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class ContactRepository(private val context: Context) {
    private val fileName = "contacts.json"

    fun loadContacts(): List<Contact> {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) {
            return emptyList()
        }

        return try {
            val jsonString = file.readText()
            val jsonArray = JSONArray(jsonString)
            val contacts = mutableListOf<Contact>()
            for (i in 0 until jsonArray.length()) {
                val jsonObj = jsonArray.getJSONObject(i)
                val typeStr = jsonObj.getString("type")
                val type = try {
                    ContactType.valueOf(typeStr)
                } catch (e: Exception) {
                    ContactType.PERSONAL
                }

                contacts.add(
                    Contact(
                        id = jsonObj.getString("id"),
                        type = type,
                        firstName = jsonObj.getString("firstName"),
                        lastName = jsonObj.getString("lastName"),
                        phone = jsonObj.getString("phone"),
                        birthday = jsonObj.optString("birthday", ""),
                        relationship = jsonObj.optString("relationship", ""),
                        company = jsonObj.optString("company", ""),
                        email = jsonObj.optString("email", ""),
                        jobTitle = jsonObj.optString("jobTitle", "")
                    )
                )
            }
            contacts
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun saveContacts(contacts: List<Contact>) {
        try {
            val jsonArray = JSONArray()
            for (contact in contacts) {
                val jsonObj = JSONObject().apply {
                    put("id", contact.id)
                    put("type", contact.type.name)
                    put("firstName", contact.firstName)
                    put("lastName", contact.lastName)
                    put("phone", contact.phone)
                    put("birthday", contact.birthday)
                    put("relationship", contact.relationship)
                    put("company", contact.company)
                    put("email", contact.email)
                    put("jobTitle", contact.jobTitle)
                }
                jsonArray.put(jsonObj)
            }

            val file = File(context.filesDir, fileName)
            FileOutputStream(file).use { output ->
                output.write(jsonArray.toString(4).toByteArray())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
