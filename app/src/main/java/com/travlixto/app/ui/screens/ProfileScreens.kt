package com.travlixto.app.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.storage.FirebaseStorage
import com.travlixto.app.data.model.User
import com.travlixto.app.data.repository.AuthRepository
import com.travlixto.app.data.repository.UserRepository
import com.travlixto.app.ui.theme.TravBlue
import com.travlixto.app.ui.theme.TravCyan
import com.travlixto.app.ui.theme.TravDark
import com.travlixto.app.ui.theme.TravLightGray
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// ---------------------------------------------------------------------
// Shared pieces (also used by ProfileSubScreens.kt)
// ---------------------------------------------------------------------

/** Round back button on the left, centered title, optional widget on the right. */
@Composable
internal fun ScreenHeader(
    title: String,
    onBack: () -> Unit,
    trailing: (@Composable () -> Unit)? = null
) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(40.dp)
                .background(TravLightGray, CircleShape)
                .clip(CircleShape)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.size(20.dp))
        }
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        if (trailing != null) {
            Box(modifier = Modifier.align(Alignment.CenterEnd)) { trailing() }
        }
    }
}

@Composable
private fun Avatar(photoUrl: String, size: Dp = 88.dp) {
    if (photoUrl.isNotBlank()) {
        AsyncImage(
            model = photoUrl,
            contentDescription = "Profile picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(size).clip(CircleShape)
        )
    } else {
        Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(size), tint = TravDark)
    }
}

// ---------------------------------------------------------------------
// Profile screen
// ---------------------------------------------------------------------

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onEditProfile: () -> Unit,
    onBookmarks: () -> Unit,
    onPreviousTrips: () -> Unit,
    onSettings: () -> Unit
) {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository() }
    val userRepository = remember { UserRepository() }
    var user by remember { mutableStateOf<User?>(null) }
    var showVersion by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        authRepository.currentUser?.uid?.let { uid ->
            userRepository.getUserProfile(uid).onSuccess { user = it }
        }
    }

    val fullName = listOfNotNull(user?.firstName, user?.lastName)
        .filter { it.isNotBlank() }
        .joinToString(" ")
        .ifBlank { "Traveller" }
    val email = user?.email?.ifBlank { null } ?: authRepository.currentUser?.email.orEmpty()

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScreenHeader(title = "Profile", onBack = onBack) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(TravCyan.copy(alpha = 0.12f), CircleShape)
                    .clip(CircleShape)
                    .clickable(onClick = onEditProfile),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit profile", tint = TravCyan, modifier = Modifier.size(20.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Avatar(photoUrl = user?.profilePictureUrl.orEmpty(), size = 88.dp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(fullName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(email, fontSize = 13.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatColumn("Reward Points", user?.rewardPoints?.toString() ?: "0", Modifier.weight(1f))
            Box(modifier = Modifier.width(1.dp).height(36.dp).background(Color(0xFFE6E6E9)))
            StatColumn("Travel Trips", user?.travelTrips?.toString() ?: "0", Modifier.weight(1f))
            Box(modifier = Modifier.width(1.dp).height(36.dp).background(Color(0xFFE6E6E9)))
            StatColumn("Bucket List", user?.bucketList?.toString() ?: "0", Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))
        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 6.dp
        ) {
            Column {
                MenuRow(Icons.Filled.Person, "Profile", onEditProfile)
                MenuDivider()
                MenuRow(Icons.Filled.Star, "Bookmarked", onBookmarks)
                MenuDivider()
                MenuRow(Icons.Filled.Place, "Previous Trips", onPreviousTrips)
                MenuDivider()
                MenuRow(Icons.Filled.Settings, "Settings", onSettings)
                MenuDivider()
                MenuRow(Icons.Filled.Info, "Version") { showVersion = true }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showVersion) {
        val versionName = remember {
            try {
                context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "1.0"
            } catch (e: Exception) {
                "1.0"
            }
        }
        AlertDialog(
            onDismissRequest = { showVersion = false },
            confirmButton = { TextButton(onClick = { showVersion = false }) { Text("OK") } },
            title = { Text("Version") },
            text = { Text("Travlixto $versionName") }
        )
    }
}

@Composable
private fun StatColumn(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = TravCyan, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
private fun MenuRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Text(label, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
    }
}

@Composable
private fun MenuDivider() {
    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(1.dp).background(Color(0xFFF0F0F2)))
}

// ---------------------------------------------------------------------
// Edit profile screen
// ---------------------------------------------------------------------

@Composable
fun EditProfileScreen(onDone: () -> Unit) {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository() }
    val userRepository = remember { UserRepository() }
    val scope = rememberCoroutineScope()

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var countryCode by remember { mutableStateOf("+27") }
    var mobile by remember { mutableStateOf("") }
    var photoUrl by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        authRepository.currentUser?.uid?.let { uid ->
            userRepository.getUserProfile(uid).onSuccess {
                firstName = it.firstName
                lastName = it.lastName
                location = it.location
                photoUrl = it.profilePictureUrl
                val m = it.mobileNumber.trim()
                if (m.startsWith("+") && m.contains(" ")) {
                    countryCode = m.substringBefore(" ")
                    mobile = m.substringAfter(" ")
                } else {
                    mobile = m
                }
            }
        }
    }

    // Picks an image from the phone and uploads it to Firebase Storage.
    val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        val uid = authRepository.currentUser?.uid
        if (uri != null && uid != null) {
            scope.launch {
                isUploading = true
                error = null
                try {
                    val ref = FirebaseStorage.getInstance().reference.child("profile_pictures/$uid.jpg")
                    ref.putFile(uri).await()
                    val url = ref.downloadUrl.await().toString()
                    userRepository.updateUserProfile(uid, mapOf("profilePictureUrl" to url))
                        .onSuccess { photoUrl = url }
                        .onFailure { error = it.message ?: "Could not save your picture." }
                } catch (e: Exception) {
                    error = e.message ?: "Could not upload your picture."
                }
                isUploading = false
            }
        }
    }

    val displayName = listOf(firstName, lastName).filter { it.isNotBlank() }.joinToString(" ").ifBlank { "Your name" }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        ScreenHeader(title = "Edit Profile", onBack = onDone) {
            if (isSaving) {
                CircularProgressIndicator(modifier = Modifier.size(22.dp).padding(end = 2.dp), strokeWidth = 2.dp)
            } else {
                TextButton(onClick = {
                    val uid = authRepository.currentUser?.uid
                    if (uid == null) {
                        onDone()
                    } else {
                        isSaving = true
                        error = null
                        scope.launch {
                            val phone = if (mobile.isBlank()) "" else "$countryCode ${mobile.trim()}"
                            userRepository.updateUserProfile(
                                uid,
                                mapOf(
                                    "firstName" to firstName.trim(),
                                    "lastName" to lastName.trim(),
                                    "location" to location.trim(),
                                    "mobileNumber" to phone
                                )
                            ).onSuccess {
                                isSaving = false
                                Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                                onDone()
                            }.onFailure {
                                isSaving = false
                                error = it.message ?: "Could not save your profile."
                            }
                        }
                    }
                }) { Text("Done", color = TravBlue, fontWeight = FontWeight.Medium) }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Avatar(photoUrl = photoUrl, size = 88.dp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(displayName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            if (isUploading) {
                Text("Uploading...", fontSize = 13.sp, color = Color.Gray)
            } else {
                Text(
                    "Change Profile Picture",
                    fontSize = 13.sp,
                    color = TravCyan,
                    modifier = Modifier.clickable { pickImage.launch("image/*") }.padding(4.dp)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            LabeledField("First Name", firstName, { firstName = it })
            Spacer(modifier = Modifier.height(16.dp))
            LabeledField("Last Name", lastName, { lastName = it })
            Spacer(modifier = Modifier.height(16.dp))
            LabeledField("Location", location, { location = it })
            Spacer(modifier = Modifier.height(16.dp))
            LabeledField(
                label = "Mobile Number",
                value = mobile,
                onValueChange = { mobile = it.filter { c -> c.isDigit() || c == ' ' } },
                keyboardType = KeyboardType.Phone,
                leading = { CountryCodePicker(countryCode) { countryCode = it } }
            )

            error?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun LabeledField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    leading: (@Composable () -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            leadingIcon = leading,
            trailingIcon = {
                if (value.isNotBlank()) {
                    Icon(Icons.Filled.Check, contentDescription = null, tint = TravBlue, modifier = Modifier.size(18.dp))
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = TravLightGray,
                unfocusedContainerColor = TravLightGray,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}

@Composable
private fun CountryCodePicker(selected: String, onSelected: (String) -> Unit) {
    var open by remember { mutableStateOf(false) }
    Box {
        Row(
            modifier = Modifier.clickable { open = true }.padding(start = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(selected, color = Color.Gray, fontSize = 14.sp)
            Icon(Icons.Filled.ArrowDropDown, contentDescription = "Country code", tint = Color.Gray)
        }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            listOf("+27", "+264", "+267", "+263", "+1", "+44", "+61", "+91").forEach { code ->
                DropdownMenuItem(text = { Text(code) }, onClick = { onSelected(code); open = false })
            }
        }
    }
}
