package com.travlixto.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.travlixto.app.data.model.User
import com.travlixto.app.data.repository.AuthRepository
import com.travlixto.app.data.repository.UserRepository
import com.travlixto.app.ui.theme.TravCyan
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(onEditProfile: () -> Unit, onSignOut: () -> Unit) {
    val authRepository = remember { AuthRepository() }
    val userRepository = remember { UserRepository() }
    var user by remember { mutableStateOf<User?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        authRepository.currentUser?.uid?.let { uid ->
            scope.launch { userRepository.getUserProfile(uid).onSuccess { user = it } }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(Color(0xFFEDEDED), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(40.dp))
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(user?.firstName?.ifBlank { "Traveller" } ?: "Traveller", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(user?.email ?: "", fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            StatColumn("Reward Points", user?.rewardPoints?.toString() ?: "0")
            StatColumn("Travel Trips", user?.travelTrips?.toString() ?: "0")
            StatColumn("Bucket List", user?.bucketList?.toString() ?: "0")
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onEditProfile, colors = ButtonDefaults.buttonColors(containerColor = TravCyan), modifier = Modifier.fillMaxWidth()) {
            Text("Edit Profile")
        }
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedButton(onClick = { authRepository.signOut(); onSignOut() }, modifier = Modifier.fillMaxWidth()) {
            Text("Sign Out")
        }
    }
}

@Composable
private fun StatColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = TravCyan, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(label, fontSize = 11.sp, color = Color.Gray)
    }
}

@Composable
fun EditProfileScreen(onDone: () -> Unit) {
    val authRepository = remember { AuthRepository() }
    val userRepository = remember { UserRepository() }
    val scope = rememberCoroutineScope()

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        authRepository.currentUser?.uid?.let { uid ->
            userRepository.getUserProfile(uid).onSuccess {
                firstName = it.firstName; lastName = it.lastName
                location = it.location; mobile = it.mobileNumber
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Edit Profile", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = {
                authRepository.currentUser?.uid?.let { uid ->
                    scope.launch {
                        userRepository.updateUserProfile(
                            uid,
                            mapOf(
                                "firstName" to firstName,
                                "lastName" to lastName,
                                "location" to location,
                                "mobileNumber" to mobile
                            )
                        )
                        onDone()
                    }
                } ?: onDone()
            }) { Text("Done", color = TravCyan) }
        }
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text("First Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Last Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(value = mobile, onValueChange = { mobile = it }, label = { Text("Mobile Number") }, modifier = Modifier.fillMaxWidth())
    }
}
