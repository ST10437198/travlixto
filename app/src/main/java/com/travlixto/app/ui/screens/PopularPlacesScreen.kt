package com.travlixto.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.travlixto.app.data.model.Destination
import com.travlixto.app.data.repository.DestinationRepository
import kotlinx.coroutines.launch

@Composable
fun PopularPlacesScreen(onDestinationClick: (Destination) -> Unit) {
    val repository = remember { DestinationRepository() }
    var destinations by remember { mutableStateOf<List<Destination>>(emptyList()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch { repository.getAllDestinations().onSuccess { destinations = it } }
    }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("All Popular Places", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(14.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(2), verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(destinations) { dest -> DestinationCard(dest, onClick = { onDestinationClick(dest) }) }
        }
    }
}
