package com.travlixto.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.travlixto.app.data.model.isoToDisplayDate
import com.travlixto.app.data.repository.AuthRepository
import com.travlixto.app.data.repository.BookingRepository
import com.travlixto.app.ui.theme.TravCyan
import com.travlixto.app.ui.theme.TravLightGray
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Past trips: your bookings whose travel date is before today. */
@Composable
fun PreviousTripsScreen(onBack: () -> Unit) {
    val repository = remember { BookingRepository() }
    var error by remember { mutableStateOf<String?>(null) }
    val bookings by remember {
        repository.observeMyBookings().catch { error = it.message; emit(emptyList()) }
    }.collectAsState(initial = emptyList())

    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }
    val past = bookings.filter { it.date < today }.sortedByDescending { it.date }

    Column(modifier = Modifier.fillMaxSize()) {
        ScreenHeader(title = "Previous Trips", onBack = onBack)
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            error?.let {
                Text(it, fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }
            if (past.isEmpty()) {
                Text("No previous trips yet.", fontSize = 13.sp, color = Color.Gray)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(past, key = { it.id }) { booking ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(TravLightGray, RoundedCornerShape(10.dp))
                                .padding(14.dp)
                        ) {
                            Text(isoToDisplayDate(booking.date), fontSize = 11.sp, color = Color.Gray)
                            Text(booking.destinationName, fontWeight = FontWeight.SemiBold)
                            Text(booking.city, fontSize = 12.sp, color = Color.Gray)
                            Text(
                                "${booking.travelers} traveler${if (booking.travelers == 1) "" else "s"} \u00B7 ${booking.totalPrice}",
                                fontSize = 12.sp,
                                color = Color.DarkGray
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Placeholder until a bookmarks feature exists: shows an empty state. */
@Composable
fun BookmarkedScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        ScreenHeader(title = "Bookmarked", onBack = onBack)
        Column(
            modifier = Modifier.fillMaxSize().padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(72.dp).background(TravCyan.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Star, contentDescription = null, tint = TravCyan, modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("No bookmarks yet", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text("Places you save will show up here.", fontSize = 13.sp, color = Color.Gray)
        }
    }
}

@Composable
fun SettingsScreen(onBack: () -> Unit, onSignOut: () -> Unit) {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository() }
    val scope = rememberCoroutineScope()
    val email = authRepository.currentUser?.email

    Column(modifier = Modifier.fillMaxSize()) {
        ScreenHeader(title = "Settings", onBack = onBack)
        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 6.dp
        ) {
            Column {
                SettingsRow("Reset Password", Color.Unspecified) {
                    if (email == null) {
                        Toast.makeText(context, "No email on this account.", Toast.LENGTH_SHORT).show()
                    } else {
                        scope.launch {
                            authRepository.sendPasswordReset(email)
                                .onSuccess { Toast.makeText(context, "Reset link sent to $email", Toast.LENGTH_LONG).show() }
                                .onFailure { Toast.makeText(context, it.message ?: "Could not send reset email.", Toast.LENGTH_LONG).show() }
                        }
                    }
                }
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(1.dp).background(Color(0xFFF0F0F2)))
                SettingsRow("Sign Out", Color(0xFFD93025)) {
                    authRepository.signOut()
                    onSignOut()
                }
            }
        }
    }
}

@Composable
private fun SettingsRow(label: String, color: Color, onClick: () -> Unit) {
    Text(
        label,
        fontSize = 14.sp,
        color = color,
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp)
    )
}
