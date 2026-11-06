package com.travlixto.app.ui.screens

import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.travlixto.app.data.model.Destination
import com.travlixto.app.data.repository.DestinationRepository
import com.travlixto.app.ui.theme.TravLightGray
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(onDestinationClick: (Destination) -> Unit) {
    val repository = remember { DestinationRepository() }
    var destinations by remember { mutableStateOf<List<Destination>>(emptyList()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            repository.getAllDestinations().onSuccess { destinations = it }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("Explore South Africa's Beauty.", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Best Destination", fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(12.dp))

        if (destinations.isEmpty()) {
            // Placeholder while Firestore is empty / not yet seeded
            Text("No destinations yet — add documents to the 'destinations' collection in Firestore.",
                fontSize = 12.sp, color = Color.Gray)
        } else {
            LazyVerticalGrid(columns = GridCells.Fixed(2), verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(destinations) { dest ->
                    DestinationCard(dest, onClick = { onDestinationClick(dest) })
                }
            }
        }
    }
}

@Composable
fun DestinationCard(dest: Destination, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .background(TravLightGray, shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
            .padding(10.dp)
            .clickable(onClick = onClick)
    ) {
        if (dest.imageUrl.isBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(Color(0xFFB0DDE8), RoundedCornerShape(10.dp))
            )
        } else {
            AsyncImage(
                model = dest.imageUrl,
                contentDescription = dest.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
        }
        Text(dest.city.ifBlank { "Cape Town" }, fontSize = 11.sp, color = Color.Gray)
    }
}

private fun Modifier.clickableText(onClick: () -> Unit): Modifier {
    return this.clickable {
        onClick()
    }
}