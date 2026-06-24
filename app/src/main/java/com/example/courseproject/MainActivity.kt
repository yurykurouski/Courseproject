package com.example.courseproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.courseproject.ui.ContactDetailScreen
import com.example.courseproject.ui.ContactListScreen
import com.example.courseproject.ui.ContactViewModel
import com.example.courseproject.ui.Screen
import com.example.courseproject.ui.theme.CourseProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize ViewModel using standard ViewModelProvider to survive configuration changes
        val viewModel = ViewModelProvider(this)[ContactViewModel::class.java]
        
        enableEdgeToEdge()
        setContent {
            CourseProjectTheme {
                CourseProjectApp(viewModel)
            }
        }
    }
}

@Composable
fun CourseProjectApp(viewModel: ContactViewModel) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (val screen = viewModel.currentScreen) {
            is Screen.List -> {
                ContactListScreen(viewModel = viewModel)
            }
            is Screen.Detail -> {
                ContactDetailScreen(
                    viewModel = viewModel,
                    contactId = screen.contactId
                )
            }
        }
    }
}