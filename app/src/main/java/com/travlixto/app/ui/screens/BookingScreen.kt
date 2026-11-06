package com.travlixto.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.travlixto.app.data.model.Destination
import com.travlixto.app.data.model.calculateTotal
import com.travlixto.app.data.model.isoToDisplayDate
import com.travlixto.app.data.model.millisToIsoDate
import com.travlixto.app.data.repository.BookingRepository
import com.travlixto.app.ui.theme.TravCyan
import com.travlixto.app.ui.theme.TravLightGray
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    destination: Destination,
    onBack: () -> Unit,
    onBooked: () -> Unit
) {
    val repository = remember { BookingRepository() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var travelers by remember { mutableIntStateOf(1) }
    var notes by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Material's date picker works in UTC midnight; build "today" the same way
    // so today itself is selectable but past days are not.
    val todayUtcMillis = remember {
        val local = Calendar.getInstance()
        Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            clear()
            set(local.get(Calendar.YEAR), local.get(Calendar.MONTH), local.get(Calendar.DAY_OF_MONTH))
        }.timeInMillis
    }
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis >= todayUtcMillis
        }
    )
    val selectedIsoDate = datePickerState.selectedDateMillis?.let { millisToIsoDate(it) }

    val name = destination.name.ifBlank { "Destination" }
    val total = calculateTotal(destination.pricePerPerson, travelers)

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text("Book Trip", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            // Destination summary
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TravLightGray, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text(name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(destination.city.ifBlank { "Cape Town" }, fontSize = 13.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(6.dp))
                Text("${destination.pricePerPerson} per person", fontSize = 13.sp, color = Color.DarkGray)
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("Travel date", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.DateRange, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(selectedIsoDate?.let { isoToDisplayDate(it) } ?: "Select a date")
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("Travelers", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                FilledTonalIconButton(
                    onClick = { if (travelers > 1) travelers-- },
                    enabled = travelers > 1
                ) { Text("-", fontSize = 22.sp) }
                Text(
                    "$travelers",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                FilledTonalIconButton(
                    onClick = { if (travelers < 10) travelers++ },
                    enabled = travelers < 10
                ) { Icon(Icons.Filled.Add, contentDescription = "More travelers") }
            }

            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total", fontWeight = FontWeight.SemiBold)
                Text(total, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            error?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = {
                val date = selectedIsoDate ?: return@Button
                isSaving = true
                error = null
                scope.launch {
                    repository.createBooking(destination, date, travelers, notes)
                        .onSuccess {
                            isSaving = false
                            Toast.makeText(context, "Trip booked!", Toast.LENGTH_SHORT).show()
                            onBooked()
                        }
                        .onFailure {
                            isSaving = false
                            error = it.message ?: "Could not complete your booking. Please try again."
                        }
                }
            },
            enabled = selectedIsoDate != null && !isSaving,
            modifier = Modifier.fillMaxWidth().padding(20.dp).height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TravCyan)
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Confirm Booking")
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = { TextButton(onClick = { showDatePicker = false }) { Text("OK") } },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
