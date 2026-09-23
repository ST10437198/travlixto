package com.travlixto.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.travlixto.app.data.model.Destination
import com.travlixto.app.data.model.ScheduleItem
import com.travlixto.app.data.repository.DestinationRepository
import com.travlixto.app.ui.theme.TravLightGray
import kotlinx.coroutines.launch

@Composable
fun ScheduleScreen(scheduleItems: List<ScheduleItem>) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("Schedule", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Text("My Schedule", fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(10.dp))
        if (scheduleItems.isEmpty()) {
            Text("No trips scheduled yet.", fontSize = 13.sp, color = Color.Gray)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(scheduleItems) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(TravLightGray, RoundedCornerShape(10.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(item.date, fontSize = 11.sp, color = Color.Gray)
                            Text(item.destinationName, fontWeight = FontWeight.SemiBold)
                            Text(item.city, fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchScreen(onResultClick: (Destination) -> Unit) {
    val repository = remember { DestinationRepository() }
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<Destination>>(emptyList()) }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("Search", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                if (it.isNotBlank()) {
                    scope.launch {
                        repository.searchDestinations(it).onSuccess { r -> results = r }
                    }
                } else {
                    results = emptyList()
                }
            },
            label = { Text("Search for Places") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(results) { dest ->
                Text(
                    dest.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TravLightGray, RoundedCornerShape(10.dp))
                        .padding(14.dp)
                        .then(Modifier)
                )
            }
        }
    }
}
